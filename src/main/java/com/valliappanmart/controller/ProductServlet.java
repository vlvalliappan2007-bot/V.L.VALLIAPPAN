package com.valliappanmart.controller;

import com.valliappanmart.dao.ProductDao;
import com.valliappanmart.dao.ReviewDao;
import com.valliappanmart.dao.WishlistDao;
import com.valliappanmart.model.Product;
import com.valliappanmart.model.Review;
import com.valliappanmart.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product", "/product/*"})
public class ProductServlet extends HttpServlet {
    private ProductDao productDao;
    private ReviewDao reviewDao;
    private WishlistDao wishlistDao;

    @Override
    public void init() throws ServletException {
        productDao = new ProductDao();
        reviewDao = new ReviewDao();
        wishlistDao = new WishlistDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String idParam = request.getParameter("id");
        String slugParam = request.getParameter("slug");

        Product product = null;

        // 1. Try finding by ID parameter
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                product = productDao.findById(Long.parseLong(idParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        // 2. Try finding by slug parameter
        if (product == null && slugParam != null && !slugParam.trim().isEmpty()) {
            product = productDao.findBySlug(slugParam.trim());
        }

        // 3. Try finding by path info, e.g., /product/1 or /product/slug
        if (product == null && pathInfo != null && pathInfo.length() > 1) {
            String pathVal = pathInfo.substring(1);
            try {
                long pid = Long.parseLong(pathVal);
                product = productDao.findById(pid);
            } catch (NumberFormatException e) {
                product = productDao.findBySlug(pathVal);
            }
        }

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
            return;
        }

        List<Review> reviews = reviewDao.findByProductId(product.getId());
        List<Product> relatedProducts = productDao.findRelated(product.getCategoryId(), product.getId(), 4);

        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        boolean isWishlisted = false;
        if (currentUser != null) {
            isWishlisted = wishlistDao.isWishlisted(currentUser.getId(), product.getId());
        }

        request.setAttribute("product", product);
        request.setAttribute("reviews", reviews);
        request.setAttribute("relatedProducts", relatedProducts);
        request.setAttribute("isWishlisted", isWishlisted);

        request.getRequestDispatcher("/WEB-INF/views/product-detail.jsp").forward(request, response);
    }
}
