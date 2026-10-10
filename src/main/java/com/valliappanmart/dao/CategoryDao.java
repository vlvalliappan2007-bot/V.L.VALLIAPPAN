package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(p.id) as product_count " +
                     "FROM categories c LEFT JOIN products p ON c.id = p.category_id AND p.is_active = TRUE " +
                     "GROUP BY c.id ORDER BY c.id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = mapRow(rs);
                c.setProductCount(rs.getInt("product_count"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category findById(Long id) {
        String sql = "SELECT c.*, COUNT(p.id) as product_count " +
                     "FROM categories c LEFT JOIN products p ON c.id = p.category_id " +
                     "WHERE c.id = ? GROUP BY c.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = mapRow(rs);
                    c.setProductCount(rs.getInt("product_count"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Category findBySlug(String slug) {
        String sql = "SELECT c.*, COUNT(p.id) as product_count " +
                     "FROM categories c LEFT JOIN products p ON c.id = p.category_id " +
                     "WHERE c.slug = ? GROUP BY c.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slug);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Category c = mapRow(rs);
                    c.setProductCount(rs.getInt("product_count"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long create(Category c) {
        String sql = "INSERT INTO categories (name, slug, description, icon_class, banner_image) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getSlug());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getIconClass());
            ps.setString(5, c.getBannerImage());
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

    public boolean update(Category c) {
        String sql = "UPDATE categories SET name = ?, slug = ?, description = ?, icon_class = ?, banner_image = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getSlug());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getIconClass());
            ps.setString(5, c.getBannerImage());
            ps.setLong(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setSlug(rs.getString("slug"));
        c.setDescription(rs.getString("description"));
        c.setIconClass(rs.getString("icon_class"));
        c.setBannerImage(rs.getString("banner_image"));
        return c;
    }
}
