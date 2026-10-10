package com.valliappanmart.controller;

import com.valliappanmart.dao.ReviewDao;
import com.valliappanmart.model.Review;
import com.valliappanmart.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ReviewServlet", urlPatterns = {"/review", "/review/*"})
public class ReviewServlet extends HttpServlet {
    private ReviewDao reviewDao;

    @Override
    public void init() throws ServletException {
        reviewDao = new ReviewDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("currentUser") : null;

        String productIdStr = request.getParameter("productId");
        if (productIdStr == null || productIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        long productId;
        try {
            productId = Long.parseLong(productIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/shop");
            return;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/product?id=" + productId);
            return;
        }

        String ratingStr = request.getParameter("rating");
        String headline = request.getParameter("headline");
        String comment = request.getParameter("comment");

        int rating = 5;
        if (ratingStr != null) {
            try {
                rating = Integer.parseInt(ratingStr.trim());
                if (rating < 1) rating = 1;
                if (rating > 5) rating = 5;
            } catch (NumberFormatException ignored) {}
        }

        if (headline != null && comment != null && !headline.trim().isEmpty()) {
            Review review = new Review();
            review.setProductId(productId);
            review.setUserId(user.getId());
            review.setRating(rating);
            review.setHeadline(headline.trim());
            review.setComment(comment.trim());
            reviewDao.create(review);
        }

        response.sendRedirect(request.getContextPath() + "/product?id=" + productId);
    }
}
