package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerDao {

    public Seller findByUserId(Long userId) {
        String sql = "SELECT s.*, u.name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM sellers s JOIN users u ON s.user_id = u.id WHERE s.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Seller findById(Long id) {
        String sql = "SELECT s.*, u.name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM sellers s JOIN users u ON s.user_id = u.id WHERE s.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long create(Seller seller) {
        String sql = "INSERT INTO sellers (user_id, store_name, business_type, gst_number, store_description, rating, is_verified, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, seller.getUserId());
            ps.setString(2, seller.getStoreName());
            ps.setString(3, seller.getBusinessType() != null ? seller.getBusinessType() : "Retail");
            ps.setString(4, seller.getGstNumber());
            ps.setString(5, seller.getStoreDescription());
            ps.setBigDecimal(6, seller.getRating() != null ? seller.getRating() : new java.math.BigDecimal("4.50"));
            ps.setBoolean(7, seller.isVerified());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Seller seller) {
        String sql = "UPDATE sellers SET store_name = ?, business_type = ?, gst_number = ?, store_description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seller.getStoreName());
            ps.setString(2, seller.getBusinessType());
            ps.setString(3, seller.getGstNumber());
            ps.setString(4, seller.getStoreDescription());
            ps.setLong(5, seller.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setVerified(Long id, boolean verified) {
        String sql = "UPDATE sellers SET is_verified = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, verified);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Seller> findAll() {
        List<Seller> list = new ArrayList<>();
        String sql = "SELECT s.*, u.name as owner_name, u.email as owner_email, u.phone as owner_phone " +
                     "FROM sellers s JOIN users u ON s.user_id = u.id ORDER BY s.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM sellers";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Seller mapRow(ResultSet rs) throws SQLException {
        Seller s = new Seller();
        s.setId(rs.getLong("id"));
        s.setUserId(rs.getLong("user_id"));
        s.setStoreName(rs.getString("store_name"));
        s.setBusinessType(rs.getString("business_type"));
        s.setGstNumber(rs.getString("gst_number"));
        s.setStoreDescription(rs.getString("store_description"));
        s.setRating(rs.getBigDecimal("rating"));
        s.setVerified(rs.getBoolean("is_verified"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            s.setOwnerName(rs.getString("owner_name"));
            s.setOwnerEmail(rs.getString("owner_email"));
            s.setOwnerPhone(rs.getString("owner_phone"));
        } catch (SQLException ignored) {
        }
        return s;
    }
}
