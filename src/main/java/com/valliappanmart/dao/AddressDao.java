package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDao {

    public List<Address> findByUserId(Long userId) {
        List<Address> list = new ArrayList<>();
        String sql = "SELECT * FROM addresses WHERE user_id = ? ORDER BY is_default DESC, id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Address findById(Long id) {
        String sql = "SELECT * FROM addresses WHERE id = ?";
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

    public Long create(Address a) {
        String sql = "INSERT INTO addresses (user_id, full_name, phone, address_line1, address_line2, city, state, pincode, is_default) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, a.getUserId());
            ps.setString(2, a.getFullName());
            ps.setString(3, a.getPhone());
            ps.setString(4, a.getAddressLine1());
            ps.setString(5, a.getAddressLine2());
            ps.setString(6, a.getCity());
            ps.setString(7, a.getState());
            ps.setString(8, a.getPincode());
            ps.setBoolean(9, a.isDefault());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    if (a.isDefault()) {
                        setDefault(a.getUserId(), id);
                    }
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Address a) {
        String sql = "UPDATE addresses SET full_name = ?, phone = ?, address_line1 = ?, address_line2 = ?, " +
                     "city = ?, state = ?, pincode = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getFullName());
            ps.setString(2, a.getPhone());
            ps.setString(3, a.getAddressLine1());
            ps.setString(4, a.getAddressLine2());
            ps.setString(5, a.getCity());
            ps.setString(6, a.getState());
            ps.setString(7, a.getPincode());
            ps.setLong(8, a.getId());
            ps.setLong(9, a.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setDefault(Long userId, Long addressId) {
        String resetSql = "UPDATE addresses SET is_default = FALSE WHERE user_id = ?";
        String setSql = "UPDATE addresses SET is_default = TRUE WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psReset = conn.prepareStatement(resetSql);
                 PreparedStatement psSet = conn.prepareStatement(setSql)) {
                psReset.setLong(1, userId);
                psReset.executeUpdate();

                psSet.setLong(1, addressId);
                psSet.setLong(2, userId);
                psSet.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id, Long userId) {
        String sql = "DELETE FROM addresses WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Address mapRow(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setFullName(rs.getString("full_name"));
        a.setPhone(rs.getString("phone"));
        a.setAddressLine1(rs.getString("address_line1"));
        a.setAddressLine2(rs.getString("address_line2"));
        a.setCity(rs.getString("city"));
        a.setState(rs.getString("state"));
        a.setPincode(rs.getString("pincode"));
        a.setDefault(rs.getBoolean("is_default"));
        return a;
    }
}
