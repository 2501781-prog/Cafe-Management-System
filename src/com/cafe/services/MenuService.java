package com.cafe.services;

/**
 * MenuService.java
 * Module 2: Menu & Inventory Management
 * @author Zainab
 */

import com.cafe.database.DBConnection;
import com.cafe.models.MenuItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService {

    public boolean addItem(MenuItem item) throws SQLException {
        String sql = "INSERT INTO menu_items (item_name, category, price, is_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getCategory());
            ps.setBigDecimal(3, item.getPrice());
            ps.setBoolean(4, item.isAvailable());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateItem(MenuItem item) throws SQLException {
        String sql = "UPDATE menu_items SET item_name=?, category=?, price=?, is_available=? WHERE item_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setString(2, item.getCategory());
            ps.setBigDecimal(3, item.getPrice());
            ps.setBoolean(4, item.isAvailable());
            ps.setInt(5, item.getItemId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<MenuItem> getAllItems() throws SQLException {
        String sql = "SELECT * FROM menu_items ORDER BY category, item_name";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { items.add(mapRow(rs)); }
        }
        return items;
    }

    public List<MenuItem> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE item_name LIKE ? OR category LIKE ? ORDER BY category";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { items.add(mapRow(rs)); }
            }
        }
        return items;
    }

    public List<MenuItem> getAvailableItems() throws SQLException {
        String sql = "SELECT * FROM menu_items WHERE is_available = TRUE ORDER BY category, item_name";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { items.add(mapRow(rs)); }
        }
        return items;
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(
            rs.getInt("item_id"),
            rs.getString("item_name"),
            rs.getString("category"),
            rs.getBigDecimal("price"),
            rs.getBoolean("is_available")
        );
    }
}
