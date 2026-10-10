package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public Product findById(Long id) {
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE p.id = ?";
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

    public Product findBySlug(String slug) {
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE p.slug = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, slug);
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

    public List<Product> search(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.name as category_name, s.store_name " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "JOIN sellers s ON p.seller_id = s.id " +
            "WHERE p.is_active = TRUE "
        );

        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ? OR LOWER(p.brand) LIKE ?) ");
            String term = "%" + keyword.trim().toLowerCase() + "%";
            params.add(term);
            params.add(term);
            params.add(term);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }

        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }

        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }

        // Sorting
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price ASC");
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.price DESC");
        } else if ("rating".equalsIgnoreCase(sortBy)) {
            sql.append("ORDER BY p.rating DESC, p.rating_count DESC");
        } else {
            sql.append("ORDER BY p.id DESC");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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

    public List<Product> findBySellerId(Long sellerId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE p.seller_id = ? ORDER BY p.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
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

    public List<Product> findFeatured(int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE p.is_active = TRUE ORDER BY p.rating DESC, p.rating_count DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
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

    public List<Product> findRelated(Long categoryId, Long excludeProductId, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "WHERE p.category_id = ? AND p.id <> ? AND p.is_active = TRUE LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            ps.setLong(2, excludeProductId);
            ps.setInt(3, limit);
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

    public Long create(Product p) {
        String sql = "INSERT INTO products (seller_id, category_id, name, slug, description, price, original_price, " +
                     "stock_quantity, is_active, main_image, sub_image_1, sub_image_2, rating, rating_count, brand, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, p.getSellerId());
            ps.setLong(2, p.getCategoryId());
            ps.setString(3, p.getName());
            ps.setString(4, p.getSlug());
            ps.setString(5, p.getDescription());
            ps.setBigDecimal(6, p.getPrice());
            ps.setBigDecimal(7, p.getOriginalPrice() != null ? p.getOriginalPrice() : p.getPrice());
            ps.setInt(8, p.getStockQuantity());
            ps.setBoolean(9, p.isActive());
            ps.setString(10, p.getMainImage());
            ps.setString(11, p.getSubImage1());
            ps.setString(12, p.getSubImage2());
            ps.setBigDecimal(13, p.getRating() != null ? p.getRating() : new BigDecimal("4.5"));
            ps.setInt(14, p.getRatingCount());
            ps.setString(15, p.getBrand());
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

    public boolean update(Product p) {
        String sql = "UPDATE products SET category_id = ?, name = ?, slug = ?, description = ?, price = ?, original_price = ?, " +
                     "stock_quantity = ?, is_active = ?, main_image = ?, sub_image_1 = ?, sub_image_2 = ?, brand = ? " +
                     "WHERE id = ? AND seller_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, p.getCategoryId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getSlug());
            ps.setString(4, p.getDescription());
            ps.setBigDecimal(5, p.getPrice());
            ps.setBigDecimal(6, p.getOriginalPrice());
            ps.setInt(7, p.getStockQuantity());
            ps.setBoolean(8, p.isActive());
            ps.setString(9, p.getMainImage());
            ps.setString(10, p.getSubImage1());
            ps.setString(11, p.getSubImage2());
            ps.setString(12, p.getBrand());
            ps.setLong(13, p.getId());
            ps.setLong(14, p.getSellerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id, Long sellerId) {
        String sql = sellerId != null ? "DELETE FROM products WHERE id = ? AND seller_id = ?" : "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            if (sellerId != null) {
                ps.setLong(2, sellerId);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleStock(Long id, int quantity) {
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean toggleStatus(Long id, boolean active) {
        String sql = "UPDATE products SET is_active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
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

    public int countBySellerId(Long sellerId) {
        String sql = "SELECT COUNT(*) FROM products WHERE seller_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
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

    public List<Product> findAllAdmin() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name, s.store_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "JOIN sellers s ON p.seller_id = s.id " +
                     "ORDER BY p.id DESC";
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

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setName(rs.getString("name"));
        p.setSlug(rs.getString("slug"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setOriginalPrice(rs.getBigDecimal("original_price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setActive(rs.getBoolean("is_active"));
        p.setMainImage(rs.getString("main_image"));
        p.setSubImage1(rs.getString("sub_image_1"));
        p.setSubImage2(rs.getString("sub_image_2"));
        p.setRating(rs.getBigDecimal("rating"));
        p.setRatingCount(rs.getInt("rating_count"));
        p.setBrand(rs.getString("brand"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            p.setCategoryName(rs.getString("category_name"));
            p.setStoreName(rs.getString("store_name"));
        } catch (SQLException ignored) {
        }
        return p;
    }
}
