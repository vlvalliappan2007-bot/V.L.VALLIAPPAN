package com.valliappanmart.controller;

import com.google.gson.Gson;
import com.valliappanmart.dao.WishlistDao;
import com.valliappanmart.model.User;
import com.valliappanmart.model.WishlistItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "WishlistServlet", urlPatterns = {"/wishlist", "/wishlist/*"})
public class WishlistServlet extends HttpServlet {
    private WishlistDao wishlistDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        wishlistDao = new WishlistDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/wishlist");
            return;
        }

        if ("/count".equalsIgnoreCase(pathInfo)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            int count = wishlistDao.getWishlistCount(user.getId());
            Map<String, Object> map = new HashMap<>();
            map.put("count", count);
            response.getWriter().write(gson.toJson(map));
            return;
        }

        if ("/remove".equalsIgnoreCase(pathInfo)) {
            String productIdStr = request.getParameter("productId");
            if (productIdStr != null) {
                try {
                    wishlistDao.removeItem(user.getId(), Long.parseLong(productIdStr.trim()));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }

        List<WishlistItem> items = wishlistDao.getItems(user.getId());
        request.setAttribute("wishlistItems", items);
        request.getRequestDispatcher("/WEB-INF/views/wishlist.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", "Please sign in to add items to your Wishlist.");
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(map));
            return;
        }

        String isAjax = request.getHeader("X-Requested-With");
        boolean ajaxRequest = "XMLHttpRequest".equalsIgnoreCase(isAjax) || "json".equalsIgnoreCase(request.getParameter("format"));

        if ("/add".equalsIgnoreCase(pathInfo)) {
            String productIdStr = request.getParameter("productId");
            boolean added = false;
            if (productIdStr != null) {
                try {
                    Long productId = Long.parseLong(productIdStr.trim());
                    added = wishlistDao.addItem(user.getId(), productId);
                } catch (NumberFormatException ignored) {}
            }

            int count = wishlistDao.getWishlistCount(user.getId());

            if (ajaxRequest) {
                response.setContentType("application/json");
                Map<String, Object> map = new HashMap<>();
                map.put("success", added);
                map.put("wishlistCount", count);
                map.put("message", added ? "Added to your Wishlist!" : "Already in your Wishlist.");
                response.getWriter().write(gson.toJson(map));
                return;
            } else {
                response.sendRedirect(request.getContextPath() + "/wishlist");
                return;
            }
        }

        if ("/remove".equalsIgnoreCase(pathInfo)) {
            String productIdStr = request.getParameter("productId");
            if (productIdStr != null) {
                try {
                    wishlistDao.removeItem(user.getId(), Long.parseLong(productIdStr.trim()));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/wishlist");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/wishlist");
    }
}
