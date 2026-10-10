package com.valliappanmart.controller;

import com.valliappanmart.dao.CategoryDao;
import com.valliappanmart.dao.OrderDao;
import com.valliappanmart.dao.ProductDao;
import com.valliappanmart.dao.SellerDao;
import com.valliappanmart.dao.UserDao;
import com.valliappanmart.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "SellerServlet", urlPatterns = {"/seller", "/seller/*"})
public class SellerServlet extends HttpServlet {
    private SellerDao sellerDao;
    private ProductDao productDao;
    private OrderDao orderDao;
    private CategoryDao categoryDao;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        sellerDao = new SellerDao();
        productDao = new ProductDao();
        orderDao = new OrderDao();
        categoryDao = new CategoryDao();
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/seller");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if ("/become".equalsIgnoreCase(pathInfo)) {
            Seller existing = sellerDao.findByUserId(user.getId());
            if (existing != null) {
                response.sendRedirect(request.getContextPath() + "/seller");
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(request, response);
            return;
        }

        Seller seller = sellerDao.findByUserId(user.getId());
        if (seller == null && !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/seller/become");
            return;
        } else if (seller == null && user.isAdmin()) {
            seller = sellerDao.findById(1L); // Admin viewing demo seller
        }

        if ("/product/add".equalsIgnoreCase(pathInfo)) {
            List<Category> categories = categoryDao.findAll();
            request.setAttribute("categories", categories);
            request.setAttribute("isEdit", false);
            request.getRequestDispatcher("/WEB-INF/views/seller-product-form.jsp").forward(request, response);
            return;
        }

        if ("/product/edit".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    Product product = productDao.findById(Long.parseLong(idStr.trim()));
                    if (product != null && (product.getSellerId().equals(seller.getId()) || user.isAdmin())) {
                        List<Category> categories = categoryDao.findAll();
                        request.setAttribute("product", product);
                        request.setAttribute("categories", categories);
                        request.setAttribute("isEdit", true);
                        request.getRequestDispatcher("/WEB-INF/views/seller-product-form.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        // Seller Dashboard view
        List<Product> sellerProducts = productDao.findBySellerId(seller.getId());
        List<OrderItem> orderItems = orderDao.findItemsBySellerId(seller.getId());
        BigDecimal totalRevenue = orderDao.calculateSellerRevenue(seller.getId());
        int totalOrders = orderDao.countSellerOrders(seller.getId());

        request.setAttribute("seller", seller);
        request.setAttribute("products", sellerProducts);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", sellerProducts.size());

        request.getRequestDispatcher("/WEB-INF/views/seller-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/seller");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "";

        if ("/become".equalsIgnoreCase(pathInfo)) {
            String storeName = request.getParameter("storeName");
            String businessType = request.getParameter("businessType");
            String gstNumber = request.getParameter("gstNumber");
            String description = request.getParameter("storeDescription");

            if (storeName != null && !storeName.trim().isEmpty()) {
                Seller s = new Seller();
                s.setUserId(user.getId());
                s.setStoreName(storeName.trim());
                s.setBusinessType(businessType != null ? businessType.trim() : "Retail");
                s.setGstNumber(gstNumber != null ? gstNumber.trim() : "");
                s.setStoreDescription(description != null ? description.trim() : "");
                s.setVerified(true);
                sellerDao.create(s);

                // Elevate user role
                user.setRole("SELLER");
                userDao.updateRole(user.getId(), "SELLER");
                session.setAttribute("currentUser", user);
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        Seller seller = sellerDao.findByUserId(user.getId());
        if (seller == null && !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/seller/become");
            return;
        }

        Long sellerId = seller != null ? seller.getId() : 1L;

        if ("/product/save".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            String name = request.getParameter("name");
            String categoryIdStr = request.getParameter("categoryId");
            String priceStr = request.getParameter("price");
            String origPriceStr = request.getParameter("originalPrice");
            String stockStr = request.getParameter("stockQuantity");
            String description = request.getParameter("description");
            String brand = request.getParameter("brand");
            String mainImage = request.getParameter("mainImage");
            String subImage1 = request.getParameter("subImage1");
            String subImage2 = request.getParameter("subImage2");

            Product p = new Product();
            p.setSellerId(sellerId);
            p.setCategoryId(Long.parseLong(categoryIdStr));
            p.setName(name);
            p.setSlug(name.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
            p.setDescription(description);
            p.setPrice(new BigDecimal(priceStr));
            p.setOriginalPrice(origPriceStr != null && !origPriceStr.isEmpty() ? new BigDecimal(origPriceStr) : new BigDecimal(priceStr));
            p.setStockQuantity(Integer.parseInt(stockStr));
            p.setActive(true);
            p.setBrand(brand);
            p.setMainImage(mainImage != null && !mainImage.trim().isEmpty() ? mainImage : "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?auto=format&fit=crop&w=800&q=80");
            p.setSubImage1(subImage1);
            p.setSubImage2(subImage2);

            if (idStr != null && !idStr.trim().isEmpty()) {
                p.setId(Long.parseLong(idStr.trim()));
                productDao.update(p);
            } else {
                productDao.create(p);
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        if ("/product/delete".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    productDao.delete(Long.parseLong(idStr.trim()), user.isAdmin() ? null : sellerId);
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        if ("/product/toggle-status".equalsIgnoreCase(pathInfo)) {
            String idStr = request.getParameter("id");
            String activeStr = request.getParameter("active");
            if (idStr != null && activeStr != null) {
                try {
                    productDao.toggleStatus(Long.parseLong(idStr.trim()), Boolean.parseBoolean(activeStr));
                } catch (Exception ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        if ("/order/update-status".equalsIgnoreCase(pathInfo)) {
            String itemIdStr = request.getParameter("orderItemId");
            String status = request.getParameter("status");
            if (itemIdStr != null && status != null) {
                try {
                    orderDao.updateItemStatus(Long.parseLong(itemIdStr.trim()), status.trim());
                } catch (Exception ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/seller");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/seller");
    }
}
