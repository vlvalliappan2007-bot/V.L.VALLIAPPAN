package com.valliappanmart.controller;

import com.valliappanmart.dao.*;
import com.valliappanmart.model.Category;
import com.valliappanmart.model.Order;
import com.valliappanmart.model.Product;
import com.valliappanmart.model.Seller;
import com.valliappanmart.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/admin/*"})
public class AdminServlet extends HttpServlet {
    private UserDao userDao;
    private SellerDao sellerDao;
    private ProductDao productDao;
    private CategoryDao categoryDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        sellerDao = new SellerDao();
        productDao = new ProductDao();
        categoryDao = new CategoryDao();
        orderDao = new OrderDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> users = userDao.findAll();
        List<Seller> sellers = sellerDao.findAll();
        List<Category> categories = categoryDao.findAll();
        List<Product> products = productDao.findAllAdmin();
        List<Order> orders = orderDao.findAllAdmin();

        BigDecimal totalRevenue = orderDao.calculateTotalRevenue();
        int totalOrders = orderDao.countAll();
        int totalUsers = userDao.countAll();
        int totalProducts = productDao.countAll();

        request.setAttribute("users", users);
        request.setAttribute("sellers", sellers);
        request.setAttribute("categories", categories);
        request.setAttribute("products", products);
        request.setAttribute("orders", orders);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalProducts", totalProducts);

        request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if ("/user/role".equalsIgnoreCase(pathInfo)) {
            String userIdStr = request.getParameter("userId");
            String role = request.getParameter("role");
            if (userIdStr != null && role != null) {
                try {
                    userDao.updateRole(Long.parseLong(userIdStr.trim()), role.trim());
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=users");
            return;
        }

        if ("/user/status".equalsIgnoreCase(pathInfo)) {
            String userIdStr = request.getParameter("userId");
            String status = request.getParameter("status");
            if (userIdStr != null && status != null) {
                try {
                    userDao.updateStatus(Long.parseLong(userIdStr.trim()), status.trim());
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=users");
            return;
        }

        if ("/seller/verify".equalsIgnoreCase(pathInfo)) {
            String sellerIdStr = request.getParameter("sellerId");
            String verifiedStr = request.getParameter("verified");
            if (sellerIdStr != null && verifiedStr != null) {
                try {
                    sellerDao.setVerified(Long.parseLong(sellerIdStr.trim()), Boolean.parseBoolean(verifiedStr));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=sellers");
            return;
        }

        if ("/category/add".equalsIgnoreCase(pathInfo)) {
            String name = request.getParameter("name");
            String slug = request.getParameter("slug");
            String description = request.getParameter("description");
            String iconClass = request.getParameter("iconClass");
            String bannerImage = request.getParameter("bannerImage");

            if (name != null && !name.trim().isEmpty()) {
                Category c = new Category();
                c.setName(name.trim());
                c.setSlug(slug != null && !slug.trim().isEmpty() ? slug.trim() : name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
                c.setDescription(description != null ? description.trim() : "");
                c.setIconClass(iconClass != null ? iconClass.trim() : "bi-tag");
                c.setBannerImage(bannerImage != null ? bannerImage.trim() : "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1200&q=80");
                categoryDao.create(c);
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=categories");
            return;
        }

        if ("/category/delete".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    categoryDao.delete(Long.parseLong(idStr.trim()));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=categories");
            return;
        }

        if ("/product/toggle".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            String activeStr = request.getParameter("active");
            if (idStr != null && activeStr != null) {
                try {
                    productDao.toggleStatus(Long.parseLong(idStr.trim()), Boolean.parseBoolean(activeStr));
                } catch (Exception ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/admin?tab=products");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin");
    }
}
