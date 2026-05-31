package com.cafe.services;

/**
 * @author GulNaaz
 * Module 4: Billing & Reports
 */

import com.cafe.database.DBConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SalesReportService – Module 4: Sales & Reports
 *
 * All database queries for the admin sales analytics panel live here.
 * Each method opens its own connection, runs one query, and closes cleanly.
 */
public class SalesReportService {

    // ---------------------------------------------------------------
    // Daily revenue for a specific date
    // ---------------------------------------------------------------
    public BigDecimal getDailyRevenue(LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total "
                   + "FROM orders WHERE DATE(order_datetime) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }
        }
    }

    // ---------------------------------------------------------------
    // Total orders placed on a specific date
    // ---------------------------------------------------------------
    public int getDailyOrderCount(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM orders WHERE DATE(order_datetime) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }

    // ---------------------------------------------------------------
    // Revenue grouped by day for a date range  →  used in bar chart
    // Returns rows: [date_label (String), revenue (BigDecimal)]
    // ---------------------------------------------------------------
    public List<Object[]> getRevenueByDateRange(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT DATE(order_datetime) AS day, "
                   + "       COALESCE(SUM(total_amount), 0) AS revenue "
                   + "FROM orders "
                   + "WHERE DATE(order_datetime) BETWEEN ? AND ? "
                   + "GROUP BY day ORDER BY day ASC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getString("day"),
                        rs.getBigDecimal("revenue")
                    });
                }
            }
        }
        return rows;
    }

    // ---------------------------------------------------------------
    // Revenue grouped by category  →  used in category breakdown table
    // Returns rows: [category (String), total_sold (int), revenue (BigDecimal)]
    // ---------------------------------------------------------------
    public List<Object[]> getRevenueByCategory(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT m.category, "
                   + "       SUM(oi.quantity) AS total_sold, "
                   + "       SUM(oi.line_total) AS revenue "
                   + "FROM order_items oi "
                   + "INNER JOIN menu_items m ON oi.item_id = m.item_id "
                   + "INNER JOIN orders o ON oi.order_id = o.order_id "
                   + "WHERE DATE(o.order_datetime) BETWEEN ? AND ? "
                   + "GROUP BY m.category ORDER BY revenue DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getString("category"),
                        rs.getInt("total_sold"),
                        rs.getBigDecimal("revenue")
                    });
                }
            }
        }
        return rows;
    }

    // ---------------------------------------------------------------
    // Top N best-selling menu items in a date range
    // Returns rows: [item_name (String), category (String), qty_sold (int), revenue (BigDecimal)]
    // ---------------------------------------------------------------
    public List<Object[]> getTopSellingItems(LocalDate from, LocalDate to, int limit) throws SQLException {
        String sql = "SELECT m.item_name, m.category, "
                   + "       SUM(oi.quantity) AS qty_sold, "
                   + "       SUM(oi.line_total) AS revenue "
                   + "FROM order_items oi "
                   + "INNER JOIN menu_items m ON oi.item_id = m.item_id "
                   + "INNER JOIN orders o ON oi.order_id = o.order_id "
                   + "WHERE DATE(o.order_datetime) BETWEEN ? AND ? "
                   + "GROUP BY m.item_id, m.item_name, m.category "
                   + "ORDER BY qty_sold DESC LIMIT ?";
        List<Object[]> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getInt("qty_sold"),
                        rs.getBigDecimal("revenue")
                    });
                }
            }
        }
        return rows;
    }

    // ---------------------------------------------------------------
    // Summary totals for a date range (used in summary cards)
    // Returns: [total_revenue, total_orders, avg_order_value]
    // ---------------------------------------------------------------
    public BigDecimal[] getRangeSummary(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total_revenue, "
                   + "       COUNT(*) AS total_orders, "
                   + "       COALESCE(AVG(total_amount), 0) AS avg_order "
                   + "FROM orders "
                   + "WHERE DATE(order_datetime) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BigDecimal[]{
                        rs.getBigDecimal("total_revenue"),
                        new BigDecimal(rs.getInt("total_orders")),
                        rs.getBigDecimal("avg_order")
                    };
                }
            }
        }
        return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
    }
}
