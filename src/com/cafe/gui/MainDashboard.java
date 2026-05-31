package com.cafe.gui;

/**
 * MainDashboard.java — Module 2: Zainab Raza
 * Stage 2: Dashboard + Menu & Inventory panels.
 */

import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;

public class MainDashboard extends JFrame {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final DashboardPanel dashboardPanel;
    private final MenuPanel menuPanel;
    private JButton dashboardButton;
    private JButton menuButton;

    public MainDashboard() {
        setTitle("Cafe Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 720));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        dashboardPanel = new DashboardPanel();
        menuPanel = new MenuPanel();

        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(menuPanel,      "MENU");

        add(createSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIUtils.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 720));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(28, 22, 20, 22));
        JLabel title = new JLabel("<html>Cafe<br>Management</html>");
        title.setForeground(Color.WHITE);
        title.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 26));
        JLabel sub = new JLabel("Module 2: Menu & Inventory");
        sub.setForeground(new Color(190, 203, 224));
        sub.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 12));
        top.add(title, BorderLayout.NORTH);
        top.add(sub, BorderLayout.SOUTH);

        JPanel nav = new JPanel(new java.awt.GridLayout(0, 1, 0, 10));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        dashboardButton = UIUtils.createNavButton("DB  Dashboard");
        menuButton      = UIUtils.createNavButton("MN  Menu & Inventory");

        dashboardButton.addActionListener(e -> {
            dashboardPanel.refreshStats();
            setActive(dashboardButton);
            cardLayout.show(contentPanel, "DASHBOARD");
        });
        menuButton.addActionListener(e -> {
            menuPanel.loadAllItems();
            setActive(menuButton);
            cardLayout.show(contentPanel, "MENU");
        });

        nav.add(dashboardButton);
        nav.add(menuButton);

        JLabel footer = new JLabel("<html>Zainab Raza — Module 2<br>OOP Lab Project</html>");
        footer.setForeground(new Color(150, 166, 190));
        footer.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 11));
        footer.setBorder(BorderFactory.createEmptyBorder(16, 22, 20, 22));

        sidebar.add(top, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        setActive(dashboardButton);
        return sidebar;
    }

    private void setActive(JButton active) {
        for (JButton b : new JButton[]{dashboardButton, menuButton}) {
            if (b == null) continue;
            boolean on = b == active;
            b.setBackground(on ? UIUtils.PRIMARY : new Color(30, 41, 59));
            b.setFont(new Font(UIUtils.FONT_FAMILY, on ? Font.BOLD : Font.PLAIN, 13));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { }
            new LoginPanel().setVisible(true);
        });
    }
}
