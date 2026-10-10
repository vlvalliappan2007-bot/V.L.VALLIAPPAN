package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.CartItem;
import com.valliappanmart.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDao {

    public List<CartItem> getItems(Long userId, String sessionId) {
        List<CartItem> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.*, p.name as p_name, p.slug as p_slug, p.description as p_desc, " +
            "p.price as p_price, p.original_price as p_orig_price, p.stock_quantity as p_stock, " +
            "p.main_image as p_image, p.brand as p_brand, s.store_name " +
            "FROM cart c " +
            "JOIN products p ON c.product_id = p.id " +
            "JOIN sellers s ON p.seller_id = s.id " +
            "WHERE "
        );

        if (userId != null) {
            sql.append("c.user_id = ? ");
        } else {
            sql.append("c.session_id = ? AND c.user_id IS NULL ");
        }
        sql.append("ORDER BY c.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (userId != null) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId != null ? sessionId : "");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getObject("user_id") != null ? rs.getLong("user_id") : null);
                    item.setSessionId(rs.getString("session_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
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

    public boolean addItem(Long userId, String sessionId, Long productId, int quantity) {
        String findSql;
        if (userId != null) {
            findSql = "SELECT id, quantity FROM cart WHERE user_id = ? AND product_id = ?";
        } else {
            findSql = "SELECT id, quantity FROM cart WHERE session_id = ? AND user_id IS NULL AND product_id = ?";
        }

        try (Connection conn = DBConnection.getConnection()) {
            Long existingId = null;
            int currentQty = 0;

            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                if (userId != null) {
                    ps.setLong(1, userId);
                    ps.setLong(2, productId);
                } else {
                    ps.setString(1, sessionId);
                    ps.setLong(2, productId);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getLong("id");
                        currentQty = rs.getInt("quantity");
                    }
                }
            }

            if (existingId != null) {
                String updateSql = "UPDATE cart SET quantity = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, currentQty + quantity);
                    ps.setLong(2, existingId);
                    return ps.executeUpdate() > 0;
                }
            } else {
                String insertSql = "INSERT INTO cart (user_id, session_id, product_id, quantity, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    if (userId != null) {
                        ps.setLong(1, userId);
                    } else {
                        ps.setNull(1, Types.BIGINT);
                    }
                    ps.setString(2, sessionId);
                    ps.setLong(3, productId);
                    ps.setInt(4, quantity);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            return removeItem(cartItemId);
        }
        String sql = "UPDATE cart SET quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeItem(Long cartItemId) {
        String sql = "DELETE FROM cart WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cartItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean clearCart(Long userId, String sessionId) {
        String sql = userId != null ? "DELETE FROM cart WHERE user_id = ?" : "DELETE FROM cart WHERE session_id = ? AND user_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCartCount(Long userId, String sessionId) {
        String sql = userId != null ? "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE user_id = ?" :
                     "SELECT COALESCE(SUM(quantity), 0) FROM cart WHERE session_id = ? AND user_id IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) {
                ps.setLong(1, userId);
            } else {
                ps.setString(1, sessionId != null ? sessionId : "");
            }
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

    public void mergeSessionCartToUser(String sessionId, Long userId) {
        if (sessionId == null || userId == null) return;
        List<CartItem> sessionItems = getItems(null, sessionId);
        for (CartItem item : sessionItems) {
            addItem(userId, null, item.getProductId(), item.getQuantity());
        }
        clearCart(null, sessionId);
    }
}
