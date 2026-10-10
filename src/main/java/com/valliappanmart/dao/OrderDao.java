package com.valliappanmart.dao;

import com.valliappanmart.config.DBConnection;
import com.valliappanmart.model.Order;
import com.valliappanmart.model.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    public Long createOrder(Order order) {
        String orderSql = "INSERT INTO orders (order_number, user_id, shipping_address, subtotal, discount, tax, " +
                          "shipping_cost, total_amount, payment_method, payment_status, order_status, tracking_number, created_at) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        String itemSql = "INSERT INTO order_items (order_id, product_id, seller_id, product_name, unit_price, quantity, total_price, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        String decStockSql = "UPDATE products SET stock_quantity = GREATEST(0, stock_quantity - ?) WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Long orderId = null;
            try (PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getOrderNumber());
                psOrder.setLong(2, order.getUserId());
                psOrder.setString(3, order.getShippingAddress());
                psOrder.setBigDecimal(4, order.getSubtotal());
                psOrder.setBigDecimal(5, order.getDiscount() != null ? order.getDiscount() : BigDecimal.ZERO);
                psOrder.setBigDecimal(6, order.getTax() != null ? order.getTax() : BigDecimal.ZERO);
                psOrder.setBigDecimal(7, order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO);
                psOrder.setBigDecimal(8, order.getTotalAmount());
                psOrder.setString(9, order.getPaymentMethod());
                psOrder.setString(10, order.getPaymentStatus() != null ? order.getPaymentStatus() : "PAID");
                psOrder.setString(11, order.getOrderStatus() != null ? order.getOrderStatus() : "CONFIRMED");
                psOrder.setString(12, order.getTrackingNumber());
                psOrder.executeUpdate();

                try (ResultSet rs = psOrder.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getLong(1);
                    }
                }
            }

            if (orderId == null) {
                conn.rollback();
                return null;
            }

            try (PreparedStatement psItem = conn.prepareStatement(itemSql);
                 PreparedStatement psStock = conn.prepareStatement(decStockSql)) {
                for (OrderItem item : order.getItems()) {
                    psItem.setLong(1, orderId);
                    psItem.setLong(2, item.getProductId());
                    psItem.setLong(3, item.getSellerId());
                    psItem.setString(4, item.getProductName());
                    psItem.setBigDecimal(5, item.getUnitPrice());
                    psItem.setInt(6, item.getQuantity());
                    psItem.setBigDecimal(7, item.getTotalPrice());
                    psItem.setString(8, item.getStatus() != null ? item.getStatus() : "PROCESSING");
                    psItem.addBatch();

                    // Decrement stock
                    psStock.setInt(1, item.getQuantity());
                    psStock.setLong(2, item.getProductId());
                    psStock.addBatch();
                }
                psItem.executeBatch();
                psStock.executeBatch();
            }

            conn.commit();
            return orderId;
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
        return null;
    }

    public Order findById(Long id) {
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsByOrderId(conn, order.getId()));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Order findByOrderNumber(String orderNumber) {
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o JOIN users u ON o.user_id = u.id WHERE o.order_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsByOrderId(conn, order.getId()));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Order> findByUserId(Long userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o JOIN users u ON o.user_id = u.id WHERE o.user_id = ? ORDER BY o.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsByOrderId(conn, order.getId()));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderItem> findItemsBySellerId(Long sellerId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, p.main_image as product_image, s.store_name, o.order_number, o.created_at, o.shipping_address " +
                     "FROM order_items oi " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN sellers s ON oi.seller_id = s.id " +
                     "WHERE oi.seller_id = ? ORDER BY oi.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setSellerId(rs.getLong("seller_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setTotalPrice(rs.getBigDecimal("total_price"));
                    item.setStatus(rs.getString("status"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setSellerStoreName(rs.getString("store_name"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> findAllAdmin() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name as user_name, u.email as user_email, u.phone as user_phone " +
                     "FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setItems(findItemsByOrderId(conn, order.getId()));
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateItemStatus(Long orderItemId, String status) {
        String sql = "UPDATE order_items SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, orderItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BigDecimal calculateTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE payment_status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateSellerRevenue(Long sellerId) {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM order_items WHERE seller_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM orders";
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

    public int countSellerOrders(Long sellerId) {
        String sql = "SELECT COUNT(DISTINCT order_id) FROM order_items WHERE seller_id = ?";
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

    private List<OrderItem> findItemsByOrderId(Connection conn, Long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.main_image as product_image, s.store_name " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN sellers s ON oi.seller_id = s.id " +
                     "WHERE oi.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setSellerId(rs.getLong("seller_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setTotalPrice(rs.getBigDecimal("total_price"));
                    item.setStatus(rs.getString("status"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setSellerStoreName(rs.getString("store_name"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setUserId(rs.getLong("user_id"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setSubtotal(rs.getBigDecimal("subtotal"));
        o.setDiscount(rs.getBigDecimal("discount"));
        o.setTax(rs.getBigDecimal("tax"));
        o.setShippingCost(rs.getBigDecimal("shipping_cost"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setTrackingNumber(rs.getString("tracking_number"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            o.setUserName(rs.getString("user_name"));
            o.setUserEmail(rs.getString("user_email"));
            o.setUserPhone(rs.getString("user_phone"));
        } catch (SQLException ignored) {}
        return o;
    }
}
