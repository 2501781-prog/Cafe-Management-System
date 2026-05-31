package com.cafe.gui;

/**
 * @author Gul Naz
 * Module 4: Billing & Reports
 */

import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * AdminLoginDialog – Modal login screen for Module 4.
 *
 * Hard-coded credentials (admin / admin123) are fine for a lab project.
 * In a real system you would hash the password and store it in the database.
 *
 * Usage:
 *   AdminLoginDialog dlg = new AdminLoginDialog(parentFrame);
 *   dlg.setVisible(true);
 *   if (dlg.isAuthenticated()) { ... open sales panel ... }
 */
public class AdminLoginDialog extends JDialog {

    // ── Credentials ──────────────────────────────────────────────────
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private boolean authenticated = false;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public AdminLoginDialog(JFrame parent) {
        super(parent, "Admin Login – Sales & Reports", true);   // modal = true
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // ── Fields ───────────────────────────────────────────────────
        usernameField = new JTextField(18);
        UIUtils.styleTextField(usernameField);

        passwordField = new JPasswordField(18);
        UIUtils.styleTextField(passwordField);

        errorLabel = new JLabel(" ");
        errorLabel.setForeground(UIUtils.DANGER);
        errorLabel.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 12));

        // ── Layout ───────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIUtils.BACKGROUND);

        // Header banner
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIUtils.SIDEBAR);
        header.setBorder(BorderFactory.createEmptyBorder(22, 28, 22, 28));
        JLabel title = new JLabel("Admin Login");
        title.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Sales & Reports – Module 4");
        sub.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 13));
        sub.setForeground(new Color(190, 203, 224));
        header.add(title, BorderLayout.NORTH);
        header.add(sub,   BorderLayout.SOUTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(28, 36, 20, 36));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(form, gbc, 0, "Username", usernameField);
        addFormRow(form, gbc, 1, "Password", passwordField);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        form.add(errorLabel, gbc);

        JButton loginBtn = UIUtils.createButton("Login", UIUtils.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(200, 38));
        gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 4, 4, 4);
        form.add(loginBtn, gbc);

        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);

        // ── Hint label ───────────────────────────────────────────────
        JLabel hint = new JLabel("Default: admin / admin123", JLabel.CENTER);
        hint.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 11));
        hint.setForeground(UIUtils.MUTED);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        root.add(hint, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(parent);

        // ── Actions ──────────────────────────────────────────────────
        loginBtn.addActionListener(e -> attemptLogin());

        // Press Enter anywhere in the dialog to login
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "login");
        root.getActionMap().put("login", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { attemptLogin(); }
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 13));
        lbl.setForeground(UIUtils.TEXT);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 0;
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (ADMIN_USERNAME.equals(user) && ADMIN_PASSWORD.equals(pass)) {
            authenticated = true;
            dispose();
        } else {
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }

    /** Returns true only if the user successfully logged in. */
    public boolean isAuthenticated() {
        return authenticated;
    }
}
