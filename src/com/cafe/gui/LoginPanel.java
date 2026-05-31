package com.cafe.gui;

/**
 * LoginPanel.java
 * Module 1: Authentication & Dashboard
 * @author Imman Fatima
 *
 * This is the first screen the user sees when the app starts.
 * It asks for username and password, checks them against the database,
 * and if correct, opens the main dashboard.
 */

import com.cafe.services.AuthService;
import com.cafe.utils.SessionManager;
import com.cafe.utils.UIUtils;
import com.cafe.utils.MessageUtils;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;

public class LoginPanel extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private AuthService authService;

    public LoginPanel() {
        authService = new AuthService();
        setTitle("Cafe Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIUtils.BACKGROUND);

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UIUtils.SIDEBAR);
        banner.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JLabel appName = new JLabel("Cafe Management");
        appName.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        JLabel tagline = new JLabel("Please login to continue");
        tagline.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 13));
        tagline.setForeground(new Color(190, 203, 224));
        banner.add(appName, BorderLayout.NORTH);
        banner.add(tagline, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(userLabel, gbc);
        usernameField = new JTextField();
        UIUtils.styleTextField(usernameField);
        gbc.gridy = 1;
        form.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 13));
        gbc.gridy = 2;
        form.add(passLabel, gbc);
        passwordField = new JPasswordField();
        UIUtils.styleTextField(passwordField);
        gbc.gridy = 3;
        form.add(passwordField, gbc);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(UIUtils.DANGER);
        errorLabel.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 12));
        gbc.gridy = 4;
        form.add(errorLabel, gbc);

        JButton loginBtn = UIUtils.createButton("Login", UIUtils.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 5;
        gbc.insets = new Insets(12, 0, 0, 0);
        form.add(loginBtn, gbc);

        JLabel hint = new JLabel("Default: admin / admin123", JLabel.CENTER);
        hint.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 11));
        hint.setForeground(UIUtils.MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        root.add(banner, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(hint, BorderLayout.SOUTH);
        setContentPane(root);

        loginBtn.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }
        try {
            if (authService.authenticate(username, password)) {
                SessionManager.setCurrentUser(username);
                MainDashboard dashboard = new MainDashboard();
                dashboard.setVisible(true);
                dispose();
            } else {
                errorLabel.setText("Wrong username or password. Try again.");
                passwordField.setText("");
                passwordField.requestFocus();
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) { }
            new LoginPanel().setVisible(true);
        });
    }
}
