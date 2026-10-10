package com.valliappanmart.controller;

import com.valliappanmart.dao.AddressDao;
import com.valliappanmart.dao.CartDao;
import com.valliappanmart.dao.OrderDao;
import com.valliappanmart.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/checkout/*", "/order/*"})
public class CheckoutServlet extends HttpServlet {
    private CartDao cartDao;
    private AddressDao addressDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        cartDao = new CartDao();
        addressDao = new AddressDao();
        orderDao = new OrderDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String fullPath = servletPath + (pathInfo != null ? pathInfo : "");

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/checkout");
            return;
        }

        if (fullPath.startsWith("/order/success")) {
            String orderNumber = request.getParameter("orderNumber");
            Order order = orderDao.findByOrderNumber(orderNumber);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/shop");
                return;
            }
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/order-success.jsp").forward(request, response);
            return;
        }

        if (fullPath.startsWith("/order/track")) {
            String orderNumber = request.getParameter("orderNumber");
            Order order = null;
            if (orderNumber != null && !orderNumber.trim().isEmpty()) {
                order = orderDao.findByOrderNumber(orderNumber.trim());
            } else {
                List<Order> orders = orderDao.findByUserId(user.getId());
                if (!orders.isEmpty()) {
                    order = orders.get(0);
                }
            }
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/order-tracking.jsp").forward(request, response);
            return;
        }

        if (fullPath.startsWith("/order/invoice")) {
            String orderNumber = request.getParameter("orderNumber");
            Order order = orderDao.findByOrderNumber(orderNumber);
            if (order == null || (!order.getUserId().equals(user.getId()) && !user.isAdmin())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invoice not found or unauthorized.");
                return;
            }
            request.setAttribute("order", order);
            request.getRequestDispatcher("/WEB-INF/views/invoice.jsp").forward(request, response);
            return;
        }

        // Standard /checkout
        List<CartItem> cartItems = cartDao.getItems(user.getId(), null);
        if (cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        List<Address> addresses = addressDao.findByUserId(user.getId());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
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

        BigDecimal tax = taxableAmount.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(shipping);

        request.setAttribute("cartItems", cartItems);
        request.setAttribute("addresses", addresses);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("discount", discount);
        request.setAttribute("shipping", shipping);
        request.setAttribute("tax", tax);
        request.setAttribute("total", total);
        request.setAttribute("promoCode", promoCode);

        request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/checkout");
            return;
        }

        List<CartItem> cartItems = cartDao.getItems(user.getId(), null);
        if (cartItems.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Address resolution
        String addressIdStr = request.getParameter("addressId");
        String shippingAddressText = "";

        if (addressIdStr != null && !addressIdStr.trim().isEmpty() && !addressIdStr.equals("new")) {
            try {
                Address a = addressDao.findById(Long.parseLong(addressIdStr.trim()));
                if (a != null) {
                    shippingAddressText = a.getFormattedAddress();
                }
            } catch (NumberFormatException ignored) {}
        }

        // If new address was entered during checkout
        if (shippingAddressText.isEmpty() || "new".equals(addressIdStr)) {
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String line1 = request.getParameter("addressLine1");
            String line2 = request.getParameter("addressLine2");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String pincode = request.getParameter("pincode");

            if (fullName != null && !fullName.trim().isEmpty() && line1 != null && city != null) {
                Address a = new Address();
                a.setUserId(user.getId());
                a.setFullName(fullName.trim());
                a.setPhone(phone != null ? phone.trim() : user.getPhone());
                a.setAddressLine1(line1.trim());
                a.setAddressLine2(line2 != null ? line2.trim() : "");
                a.setCity(city.trim());
                a.setState(state != null ? state.trim() : "Tamil Nadu");
                a.setPincode(pincode != null ? pincode.trim() : "600001");
                a.setDefault(true);
                addressDao.create(a);
                shippingAddressText = a.getFormattedAddress();
            } else {
                shippingAddressText = user.getName() + ", Default Shipping Address, Chennai, Tamil Nadu - 600020 | Mobile: " + user.getPhone();
            }
        }

        // Financials
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
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

        BigDecimal tax = taxableAmount.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableAmount.add(shipping);

        // Payment simulation
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "UPI";
        }

        // Generate Order
        String orderNumber = "VM-" + System.currentTimeMillis() % 10000000 + "-" + (int)(Math.random() * 900 + 100);
        String trackingNumber = "TRK-IN-" + (int)(Math.random() * 9000000 + 1000000);

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUserId(user.getId());
        order.setShippingAddress(shippingAddressText);
        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTax(tax);
        order.setShippingCost(shipping);
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("COD".equalsIgnoreCase(paymentMethod) ? "PENDING" : "PAID");
        order.setOrderStatus("CONFIRMED");
        order.setTrackingNumber(trackingNumber);

        for (CartItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setProductId(item.getProductId());
            oi.setSellerId(item.getProduct().getSellerId());
            oi.setProductName(item.getProduct().getName());
            oi.setUnitPrice(item.getProduct().getPrice());
            oi.setQuantity(item.getQuantity());
            oi.setTotalPrice(item.getItemTotal());
            oi.setStatus("PROCESSING");
            order.getItems().add(oi);
        }

        Long createdOrderId = orderDao.createOrder(order);

        if (createdOrderId != null) {
            // Clear cart & promo code
            cartDao.clearCart(user.getId(), null);
            session.removeAttribute("appliedPromoCode");
            response.sendRedirect(request.getContextPath() + "/order/success?orderNumber=" + orderNumber);
        } else {
            session.setAttribute("checkoutError", "Failed to place order. Please review your details and try again.");
            response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }
}
