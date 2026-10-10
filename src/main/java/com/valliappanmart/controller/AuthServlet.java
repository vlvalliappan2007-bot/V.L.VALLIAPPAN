package com.valliappanmart.controller;

import com.valliappanmart.dao.AddressDao;
import com.valliappanmart.dao.CartDao;
import com.valliappanmart.dao.OrderDao;
import com.valliappanmart.dao.UserDao;
import com.valliappanmart.model.Address;
import com.valliappanmart.model.Order;
import com.valliappanmart.model.User;
import com.valliappanmart.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/*", "/profile", "/profile/*"})
public class AuthServlet extends HttpServlet {
    private UserDao userDao;
    private CartDao cartDao;
    private AddressDao addressDao;
    private OrderDao orderDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
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

        if (fullPath.startsWith("/auth/logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        if (fullPath.startsWith("/auth/login")) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        if (fullPath.startsWith("/auth/register")) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        if (fullPath.startsWith("/profile")) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("currentUser") : null;
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/profile");
                return;
            }

            if (fullPath.startsWith("/profile/address/delete")) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    try {
                        addressDao.delete(Long.parseLong(idStr.trim()), user.getId());
                    } catch (NumberFormatException ignored) {}
                }
                response.sendRedirect(request.getContextPath() + "/profile");
                return;
            }

            List<Address> addresses = addressDao.findByUserId(user.getId());
            List<Order> orders = orderDao.findByUserId(user.getId());

            request.setAttribute("addresses", addresses);
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/shop");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String fullPath = servletPath + (pathInfo != null ? pathInfo : "");

        if (fullPath.startsWith("/auth/login")) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String redirect = request.getParameter("redirect");

            User user = userDao.findByEmail(email);
            if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
                    request.setAttribute("errorMessage", "Your account has been suspended. Please contact support.");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                    return;
                }

                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", user);

                // Merge guest session cart items into user account
                cartDao.mergeSessionCartToUser(session.getId(), user.getId());

                if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("login") && !redirect.contains("register")) {
                    response.sendRedirect(request.getContextPath() + redirect);
                } else if (user.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin");
                } else if (user.isSeller()) {
                    response.sendRedirect(request.getContextPath() + "/seller");
                } else {
                    response.sendRedirect(request.getContextPath() + "/shop");
                }
            } else {
                request.setAttribute("errorMessage", "Invalid email address or password. Please try again.");
                request.setAttribute("enteredEmail", email);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
            return;
        }

        if (fullPath.startsWith("/auth/register")) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String role = request.getParameter("role");

            if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null || password.length() < 6) {
                request.setAttribute("errorMessage", "Please provide a valid name, email, and password of at least 6 characters.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            if (!password.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Passwords do not match. Please verify and retype.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            if (userDao.findByEmail(email) != null) {
                request.setAttribute("errorMessage", "An account with this email address already exists. Please sign in instead.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                return;
            }

            User newUser = new User();
            newUser.setName(name.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPhone(phone != null ? phone.trim() : "");
            newUser.setPasswordHash(PasswordUtil.hashPassword(password));
            newUser.setRole("SELLER".equalsIgnoreCase(role) ? "SELLER" : "BUYER");
            newUser.setStatus("ACTIVE");

            Long userId = userDao.create(newUser);
            if (userId != null) {
                newUser.setId(userId);
                HttpSession session = request.getSession(true);
                session.setAttribute("currentUser", newUser);

                if (newUser.isSeller()) {
                    response.sendRedirect(request.getContextPath() + "/seller/become");
                } else {
                    response.sendRedirect(request.getContextPath() + "/shop");
                }
            } else {
                request.setAttribute("errorMessage", "Failed to create account due to an internal error. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            }
            return;
        }

        if (fullPath.startsWith("/profile/update")) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("currentUser") : null;
            if (user != null) {
                String name = request.getParameter("name");
                String phone = request.getParameter("phone");
                if (name != null && !name.trim().isEmpty()) {
                    user.setName(name.trim());
                    user.setPhone(phone != null ? phone.trim() : "");
                    userDao.update(user);
                    session.setAttribute("currentUser", user);
                    session.setAttribute("profileSuccess", "Your profile details have been saved.");
                }
            }
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if (fullPath.startsWith("/profile/address/add")) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("currentUser") : null;
            if (user != null) {
                Address a = new Address();
                a.setUserId(user.getId());
                a.setFullName(request.getParameter("fullName"));
                a.setPhone(request.getParameter("phone"));
                a.setAddressLine1(request.getParameter("addressLine1"));
                a.setAddressLine2(request.getParameter("addressLine2"));
                a.setCity(request.getParameter("city"));
                a.setState(request.getParameter("state"));
                a.setPincode(request.getParameter("pincode"));
                a.setDefault("on".equalsIgnoreCase(request.getParameter("isDefault")));
                addressDao.create(a);
            }
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        if (fullPath.startsWith("/profile/address/default")) {
            HttpSession session = request.getSession(false);
            User user = session != null ? (User) session.getAttribute("currentUser") : null;
            if (user != null) {
                String idStr = request.getParameter("id");
                if (idStr != null) {
                    try {
                        addressDao.setDefault(user.getId(), Long.parseLong(idStr.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/shop");
    }
}
