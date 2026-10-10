package com.valliappanmart.controller;

import com.google.gson.Gson;
import com.valliappanmart.dao.CartDao;
import com.valliappanmart.model.CartItem;
import com.valliappanmart.model.User;
import com.valliappanmart.util.CurrencyUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CartServlet", urlPatterns = {"/cart", "/cart/*"})
public class CartServlet extends HttpServlet {
    private CartDao cartDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        cartDao = new CartDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        Long userId = user != null ? user.getId() : null;
        String sessionId = session.getId();

        if ("/count".equalsIgnoreCase(pathInfo)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            int count = cartDao.getCartCount(userId, sessionId);
            Map<String, Object> map = new HashMap<>();
            map.put("count", count);
            response.getWriter().write(gson.toJson(map));
            return;
        }

        if ("/remove".equalsIgnoreCase(pathInfo)) {
            String itemIdStr = request.getParameter("id");
            if (itemIdStr != null) {
                try {
                    cartDao.removeItem(Long.parseLong(itemIdStr.trim()));
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Main Cart View
        List<CartItem> items = cartDao.getItems(userId, sessionId);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            subtotal = subtotal.add(item.getItemTotal());
        }

        String promoCode = (String) session.getAttribute("appliedPromoCode");
        BigDecimal discount = BigDecimal.ZERO;
        if (promoCode != null) {
            if ("AMAZON10".equalsIgnoreCase(promoCode)) {
                discount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            } else if ("VALLIAPPAN500".equalsIgnoreCase(promoCode)) {
                discount = subtotal.compareTo(new BigDecimal("1000")) >= 0 ? new BigDecimal("500.00") : BigDecimal.ZERO;
            }
        }

        BigDecimal taxableAmount = subtotal.subtract(discount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        // Standard Indian GST (included in product prices or calculated as 18% display break-up)
        // Free shipping on orders over ₹499
        BigDecimal shipping = BigDecimal.ZERO;
        if (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(new BigDecimal("499.00")) < 0) {
            shipping = new BigDecimal("40.00");
        }

        BigDecimal tax = taxableAmount.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(shipping);

        request.setAttribute("cartItems", items);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("discount", discount);
        request.setAttribute("shipping", shipping);
        request.setAttribute("tax", tax);
        request.setAttribute("total", total);
        request.setAttribute("promoCode", promoCode);

        request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("currentUser");
        Long userId = user != null ? user.getId() : null;
        String sessionId = session.getId();

        String isAjax = request.getHeader("X-Requested-With");
        boolean ajaxRequest = "XMLHttpRequest".equalsIgnoreCase(isAjax) || "json".equalsIgnoreCase(request.getParameter("format"));

        if ("/add".equalsIgnoreCase(pathInfo)) {
            String productIdStr = request.getParameter("productId");
            String quantityStr = request.getParameter("quantity");
            int quantity = 1;
            if (quantityStr != null) {
                try {
                    quantity = Math.max(1, Integer.parseInt(quantityStr.trim()));
                } catch (NumberFormatException ignored) {}
            }

            if (productIdStr != null) {
                try {
                    Long productId = Long.parseLong(productIdStr.trim());
                    cartDao.addItem(userId, sessionId, productId, quantity);
                } catch (NumberFormatException ignored) {}
            }

            int cartCount = cartDao.getCartCount(userId, sessionId);

            if (ajaxRequest) {
                response.setContentType("application/json");
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("cartCount", cartCount);
                map.put("message", "Product successfully added to your Cart!");
                response.getWriter().write(gson.toJson(map));
                return;
            } else {
                String redirect = request.getParameter("redirect");
                if ("cart".equalsIgnoreCase(redirect)) {
                    response.sendRedirect(request.getContextPath() + "/cart");
                } else if ("checkout".equalsIgnoreCase(redirect)) {
                    response.sendRedirect(request.getContextPath() + "/checkout");
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
                return;
            }
        }

        if ("/update".equalsIgnoreCase(pathInfo)) {
            String itemIdStr = request.getParameter("itemId");
            String quantityStr = request.getParameter("quantity");

            if (itemIdStr != null && quantityStr != null) {
                try {
                    Long itemId = Long.parseLong(itemIdStr.trim());
                    int quantity = Integer.parseInt(quantityStr.trim());
                    cartDao.updateQuantity(itemId, quantity);
                } catch (NumberFormatException ignored) {}
            }

            // Recalculate totals
            List<CartItem> items = cartDao.getItems(userId, sessionId);
            BigDecimal subtotal = BigDecimal.ZERO;
            for (CartItem item : items) {
                subtotal = subtotal.add(item.getItemTotal());
            }

            String promoCode = (String) session.getAttribute("appliedPromoCode");
            BigDecimal discount = BigDecimal.ZERO;
            if (promoCode != null) {
                if ("AMAZON10".equalsIgnoreCase(promoCode)) {
                    discount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
                } else if ("VALLIAPPAN500".equalsIgnoreCase(promoCode)) {
                    discount = subtotal.compareTo(new BigDecimal("1000")) >= 0 ? new BigDecimal("500.00") : BigDecimal.ZERO;
                }
            }

            BigDecimal taxableAmount = subtotal.subtract(discount);
            if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
                taxableAmount = BigDecimal.ZERO;
            }

            BigDecimal shipping = BigDecimal.ZERO;
            if (subtotal.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(new BigDecimal("499.00")) < 0) {
                shipping = new BigDecimal("40.00");
            }

            BigDecimal total = taxableAmount.add(shipping);
            int cartCount = cartDao.getCartCount(userId, sessionId);

            if (ajaxRequest) {
                response.setContentType("application/json");
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("cartCount", cartCount);
                map.put("subtotal", CurrencyUtil.formatINR(subtotal));
                map.put("discount", CurrencyUtil.formatINR(discount));
                map.put("shipping", shipping.compareTo(BigDecimal.ZERO) == 0 ? "FREE" : CurrencyUtil.formatINR(shipping));
                map.put("total", CurrencyUtil.formatINR(total));
                response.getWriter().write(gson.toJson(map));
                return;
            } else {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }
        }

        if ("/apply-coupon".equalsIgnoreCase(pathInfo)) {
            String promo = request.getParameter("couponCode");
            if (promo != null && (!promo.trim().isEmpty())) {
                String code = promo.trim().toUpperCase();
                if ("AMAZON10".equals(code) || "VALLIAPPAN500".equals(code)) {
                    session.setAttribute("appliedPromoCode", code);
                    session.setAttribute("couponSuccess", "Promo code " + code + " applied successfully!");
                } else {
                    session.setAttribute("couponError", "Invalid Promo Code. Try 'AMAZON10' or 'VALLIAPPAN500'.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if ("/remove-coupon".equalsIgnoreCase(pathInfo)) {
            session.removeAttribute("appliedPromoCode");
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
