package com.valliappanmart.filter;

import com.valliappanmart.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        // Admin protection
        if (path.startsWith("/admin")) {
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login?redirect=" + path);
                return;
            }
            if (!currentUser.isAdmin()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Administrator privileges required.");
                return;
            }
        }

        // Seller protection (allow /seller/become for buyers)
        if (path.startsWith("/seller") && !path.equals("/seller/become")) {
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login?redirect=" + path);
                return;
            }
            if (!currentUser.isSeller() && !currentUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/seller/become");
                return;
            }
        }

        // Buyer protected routes: checkout, wishlist, profile
        if (path.startsWith("/checkout") || path.startsWith("/wishlist") || path.startsWith("/profile")) {
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login?redirect=" + path);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
