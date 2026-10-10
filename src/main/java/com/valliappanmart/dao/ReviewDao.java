package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Review;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDao {

    public List<Review> findByProductId(Long productId) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name as user_name " +
                     "FROM reviews r JOIN users u ON r.user_id = u.id " +
                     "WHERE r.product_id = ? ORDER BY r.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setHeadline(rs.getString("headline"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setUserName(rs.getString("user_name"));
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean create(Review r) {
        String insertSql = "INSERT INTO reviews (product_id, user_id, rating, headline, comment, created_at) " +
                           "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String updateProductSql = "UPDATE products SET " +
                                  "rating = (SELECT AVG(CAST(rating AS DECIMAL(3,2))) FROM reviews WHERE product_id = ?), " +
                                  "rating_count = (SELECT COUNT(*) FROM reviews WHERE product_id = ?) " +
                                  "WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setLong(1, r.getProductId());
                psInsert.setLong(2, r.getUserId());
                psInsert.setInt(3, r.getRating());
                psInsert.setString(4, r.getHeadline());
                psInsert.setString(5, r.getComment());
                psInsert.executeUpdate();
            }

            try (PreparedStatement psUpdate = conn.prepareStatement(updateProductSql)) {
                psUpdate.setLong(1, r.getProductId());
                psUpdate.setLong(2, r.getProductId());
                psUpdate.setLong(3, r.getProductId());
                psUpdate.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {}
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {}
                DBConnection.closeQuietly(conn);
            }
        }
        return false;
    }
}
