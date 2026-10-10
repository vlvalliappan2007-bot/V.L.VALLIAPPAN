package com.valliappanmart.controller;

import com.google.gson.Gson;
import com.valliappanmart.dao.CategoryDao;
import com.valliappanmart.dao.ProductDao;
import com.valliappanmart.model.Category;
import com.valliappanmart.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "ShopServlet", urlPatterns = {"", "/shop", "/search"})
public class ShopServlet extends HttpServlet {
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        productDao = new ProductDao();
        categoryDao = new CategoryDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("q");
        String categoryIdStr = request.getParameter("category");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String sortBy = request.getParameter("sortBy");
        String format = request.getParameter("format");

        Long categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Long.parseLong(categoryIdStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        // Live JSON autosuggest for header search
        if ("json".equalsIgnoreCase(format)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            List<Product> suggestions = productDao.search(keyword, categoryId, null, null, "rating");
            List<Map<String, Object>> result = suggestions.stream().limit(8).map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("price", p.getPrice());
                map.put("image", p.getMainImage());
                map.put("category", p.getCategoryName());
                map.put("slug", p.getSlug());
                return map;
            }).collect(Collectors.toList());
            response.getWriter().write(gson.toJson(result));
            return;
        }

        BigDecimal minPrice = null;
        if (minPriceStr != null && !minPriceStr.trim().isEmpty()) {
            try {
                minPrice = new BigDecimal(minPriceStr.trim());
            } catch (Exception ignored) {}
        }

        BigDecimal maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = new BigDecimal(maxPriceStr.trim());
            } catch (Exception ignored) {}
        }

        List<Product> products = productDao.search(keyword, categoryId, minPrice, maxPrice, sortBy);
        List<Category> categories = categoryDao.findAll();
        List<Product> featuredProducts = productDao.findFeatured(4);

        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("featuredProducts", featuredProducts);
        request.setAttribute("currentKeyword", keyword);
        request.setAttribute("currentCategoryId", categoryId);
        request.setAttribute("currentMinPrice", minPriceStr);
        request.setAttribute("currentMaxPrice", maxPriceStr);
        request.setAttribute("currentSortBy", sortBy);

        request.getRequestDispatcher("/WEB-INF/views/shop.jsp").forward(request, response);
    }
}
