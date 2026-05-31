package com.cafe.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class UIUtils {
    public static final String FONT_FAMILY = "Segoe UI";
    
    // Modern, high-contrast color palette
    public static final Color SIDEBAR = new Color(15, 23, 42);         
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);   
    public static final Color BACKGROUND = new Color(243, 244, 246);   
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(229, 231, 235);       
    
    public static final Color PRIMARY = new Color(79, 70, 229);        
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);  
    public static final Color SUCCESS = new Color(16, 185, 129);       
    public static final Color WARNING = new Color(245, 158, 11);       
    public static final Color DANGER = new Color(239, 68, 68);         
    
    public static final Color MUTED = new Color(100, 116, 139);   
    public static final Color TEXT = new Color(15, 23, 42);       
    public static final Color TABLE_STRIPE = new Color(248, 250, 252); 

    private UIUtils() {
    }

    public static JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        
        Color hover = (color.equals(PRIMARY)) ? PRIMARY_HOVER : color.darker();
        
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false); 
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(hover),
                BorderFactory.createEmptyBorder(8, 24, 8, 24))); // Increased horizontal padding
                
        // Force a strict minimum size so layout managers can't crush the button
        Dimension prefSize = button.getPreferredSize();
        button.setPreferredSize(new Dimension(Math.max(120, prefSize.width), 38));
        button.setMinimumSize(new Dimension(100, 38));
        
        installHover(button, color, hover);
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = createButton(text, new Color(71, 85, 105));
        return button;
    }

    public static JButton createNavButton(String text) {
        JButton button = createButton(text, SIDEBAR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        
        // Nav buttons usually span the width of the sidebar, so we give them a taller height
        button.setPreferredSize(new Dimension(200, 42)); 
        
        installHover(button, SIDEBAR, SIDEBAR_HOVER);
        return button;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(createPanelBorder(18));
        return panel;
    }

    public static Border createPanelBorder(int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14)); // Slightly larger font for readability
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
                
        // Enforce a good default size for search boxes / inputs
        field.setPreferredSize(new Dimension(250, 38));
        field.setMinimumSize(new Dimension(150, 38));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        comboBox.setBackground(SURFACE);
        comboBox.setForeground(TEXT);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER));
        
        // Enforce a good default size for dropdowns
        comboBox.setPreferredSize(new Dimension(250, 38));
        comboBox.setMinimumSize(new Dimension(150, 38));
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(120, 38)); // Default spinner size
        
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField field = ((JSpinner.DefaultEditor) editor).getTextField();
            styleTextField(field);
            // Spinners usually need to be smaller than search boxes
            field.setPreferredSize(new Dimension(100, 38)); 
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(36); // Slightly taller rows
        table.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        table.setSelectionBackground(new Color(224, 231, 255)); 
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(SURFACE);
        header.setForeground(TEXT);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40)); // Taller header
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? SURFACE : TABLE_STRIPE);
                    component.setForeground(TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        return scrollPane;
    }

    public static JLabel createPageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 26));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createSmallMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        label.setForeground(MUTED);
        return label;
    }

    public static JPanel createTitleBlock(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);
        panel.add(createPageTitle(title), BorderLayout.NORTH);
        panel.add(createSmallMutedLabel(subtitle), BorderLayout.SOUTH);
        return panel;
    }

    public static void pad(JComponent component) {
        component.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
    }

    private static void installHover(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    return;
                }
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("active"))) {
                    Object activeColor = button.getClientProperty("activeColor");
                    if (activeColor instanceof Color) {
                        button.setBackground((Color) activeColor);
                    }
                    return;
                }
                button.setBackground(normalColor);
            }
        });
    }
}