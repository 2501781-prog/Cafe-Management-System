package com.cafe.services;

/**
 * AuthService.java
 * Module 1: Authentication & Dashboard
 * @author Imman Fatima
 *
 * Handles checking if a username + password is correct.
 * Talks to the 'users' table in the database.
 * Never stores the password in plain text in memory longer than needed.
 */

import com.cafe.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    /**
     * Checks if the given username and password match a record in the users table.
     *
     * @param username  what the user typed in the username field
     * @param password  what the user typed in the password field
     * @return true if credentials are valid, false if not
     * @throws SQLException if the database is unreachable
     */
    public boolean authenticate(String username, String password) throws SQLException {
        // We use ? placeholders — never put user input directly into SQL
        // That would allow SQL injection attacks
        String sql = "SELECT COUNT(*) AS found FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // COUNT(*) returns 1 if a matching row exists, 0 if not
                    return rs.getInt("found") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Gets the display name of a user (for showing in the sidebar).
     *
     * @param username the logged-in username
     * @return full name from database, or username if not found
     */
    public String getDisplayName(String username) throws SQLException {
        String sql = "SELECT full_name FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        }
        return username; // fallback: just show the username
    }
}
