package com.valliappanmart.controller;

import com.valliappanmart.dao.CategoryDao;
import com.valliappanmart.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CategoryServlet", urlPatterns = {"/category", "/category/*"})
public class CategoryServlet extends HttpServlet {
    private CategoryDao categoryDao;

    @Override
    public void init() throws ServletException {
        categoryDao = new CategoryDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String idParam = request.getParameter("id");
        String slugParam = request.getParameter("slug");

        Category category = null;

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                category = categoryDao.findById(Long.parseLong(idParam.trim()));
            } catch (NumberFormatException ignored) {}
        }

        if (category == null && slugParam != null && !slugParam.trim().isEmpty()) {
            category = categoryDao.findBySlug(slugParam.trim());
        }

        if (category == null && pathInfo != null && pathInfo.length() > 1) {
            String val = pathInfo.substring(1);
            try {
                category = categoryDao.findById(Long.parseLong(val));
            } catch (NumberFormatException e) {
                category = categoryDao.findBySlug(val);
            }
        }

        if (category != null) {
            response.sendRedirect(request.getContextPath() + "/shop?category=" + category.getId());
        } else {
            response.sendRedirect(request.getContextPath() + "/shop");
        }
    }
}
