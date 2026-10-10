package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Product;
import com.valliappanmart.model.WishlistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDao {

    public List<WishlistItem> getItems(Long userId) {
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT w.*, p.name as p_name, p.slug as p_slug, p.description as p_desc, " +
                     "p.price as p_price, p.original_price as p_orig_price, p.stock_quantity as p_stock, " +
                     "p.main_image as p_image, p.brand as p_brand, s.store_name " +
                     "FROM wishlist w " +
                     "JOIN products p ON w.product_id = p.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE w.user_id = ? ORDER BY w.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));

                    Product p = new Product();
                    p.setId(rs.getLong("product_id"));
                    p.setName(rs.getString("p_name"));
                    p.setSlug(rs.getString("p_slug"));
                    p.setDescription(rs.getString("p_desc"));
                    p.setPrice(rs.getBigDecimal("p_price"));
                    p.setOriginalPrice(rs.getBigDecimal("p_orig_price"));
                    p.setStockQuantity(rs.getInt("p_stock"));
                    p.setMainImage(rs.getString("p_image"));
                    p.setBrand(rs.getString("p_brand"));
                    p.setStoreName(rs.getString("store_name"));

                    item.setProduct(p);
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addItem(Long userId, Long productId) {
        String sql = "MERGE INTO wishlist (user_id, product_id, created_at) KEY (user_id, product_id) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeItem(Long userId, Long productId) {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isWishlisted(Long userId, Long productId) {
        if (userId == null || productId == null) return false;
        String sql = "SELECT 1 FROM wishlist WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getWishlistCount(Long userId) {
        if (userId == null) return 0;
        String sql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
