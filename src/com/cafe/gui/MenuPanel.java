package com.cafe.gui;

/**
 * MenuPanel.java
 * Module 2: Menu & Inventory Management
 * @author Zainab Raza
 *
 * The main screen for managing the cafe menu.
 * Staff can add new items, edit prices, mark items unavailable, delete items.
 * Has a search bar to filter the JTable in real time.
 *
 * Layout:
 *   TOP    — title + search bar
 *   CENTER — JTable showing all menu items
 *   SOUTH  — form fields + Add/Update/Delete buttons
 */

import com.cafe.models.MenuItem;
import com.cafe.services.MenuService;
import com.cafe.utils.MessageUtils;
import com.cafe.utils.UIUtils;
import com.cafe.utils.ValidationUtils;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class MenuPanel extends JPanel {

    private MenuService menuService;

    // Table
    private JTable menuTable;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JCheckBox availableCheck;

    // Buttons
    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton clearBtn;

    // Search
    private JTextField searchField;

    // Track which row is selected for update/delete
    private int selectedItemId = -1;

    public MenuPanel() {
        menuService = new MenuService();
        setLayout(new BorderLayout(12, 12));
        setBackground(UIUtils.BACKGROUND);
        UIUtils.pad(this);

        buildTableModel();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        loadAllItems();
        wireTableSelection();
    }

    private void buildTableModel() {
        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Item Name", "Category", "Price (Rs.)", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.add(UIUtils.createTitleBlock("Menu & Inventory", "Add, edit, or remove cafe menu items"), BorderLayout.WEST);

        // Search bar on the right
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchField = new JTextField(18);
        UIUtils.styleTextField(searchField);
        JButton searchBtn = UIUtils.createButton("Search", UIUtils.PRIMARY);
        JButton showAllBtn = UIUtils.createSecondaryButton("Show All");

        searchBtn.addActionListener(e -> searchItems());
        showAllBtn.addActionListener(e -> loadAllItems());

        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildTable() {
        menuTable = new JTable(tableModel);
        UIUtils.styleTable(menuTable);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(50);  // ID column narrow
        return UIUtils.createTableScrollPane(menuTable);
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: labels
        addLabel(form, gbc, 0, 0, "Item Name");
        addLabel(form, gbc, 1, 0, "Category");
        addLabel(form, gbc, 2, 0, "Price (Rs.)");
        addLabel(form, gbc, 3, 0, "Available");

        // Row 1: fields
        nameField = new JTextField(16);
        UIUtils.styleTextField(nameField);
        addField(form, gbc, 0, 1, nameField);

        categoryCombo = new JComboBox<>(new String[]{"Coffee", "Fast Food", "Dessert", "Cold Drink", "Other"});
        UIUtils.styleComboBox(categoryCombo);
        addField(form, gbc, 1, 1, categoryCombo);

        priceField = new JTextField(10);
        UIUtils.styleTextField(priceField);
        addField(form, gbc, 2, 1, priceField);

        availableCheck = new JCheckBox("Yes");
        availableCheck.setSelected(true);
        availableCheck.setOpaque(false);
        addField(form, gbc, 3, 1, availableCheck);

        // Row 2: buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setOpaque(false);
        addBtn    = UIUtils.createButton("Add Item", UIUtils.SUCCESS);
        updateBtn = UIUtils.createButton("Update", UIUtils.PRIMARY);
        deleteBtn = UIUtils.createButton("Delete", UIUtils.DANGER);
        clearBtn  = UIUtils.createSecondaryButton("Clear");

        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        addBtn.addActionListener(e -> addItem());
        updateBtn.addActionListener(e -> updateItem());
        deleteBtn.addActionListener(e -> deleteItem());
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        form.add(btnPanel, gbc);

        return form;
    }

    // When user clicks a row in the table, fill the form with that item's data
    private void wireTableSelection() {
        menuTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && menuTable.getSelectedRow() >= 0) {
                int row = menuTable.getSelectedRow();
                selectedItemId = (int) tableModel.getValueAt(row, 0);
                nameField.setText((String) tableModel.getValueAt(row, 1));
                categoryCombo.setSelectedItem(tableModel.getValueAt(row, 2));
                priceField.setText(tableModel.getValueAt(row, 3).toString());
                availableCheck.setSelected("Yes".equals(tableModel.getValueAt(row, 4)));
                updateBtn.setEnabled(true);
                deleteBtn.setEnabled(true);
                addBtn.setEnabled(false);
            }
        });
    }

    private void addItem() {
        if (!validateForm()) return;
        try {
            MenuItem item = buildItemFromForm();
            if (menuService.addItem(item)) {
                MessageUtils.showInfo(this, "Item added successfully.");
                loadAllItems();
                clearForm();
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    private void updateItem() {
        if (selectedItemId < 0 || !validateForm()) return;
        try {
            MenuItem item = buildItemFromForm();
            item.setItemId(selectedItemId);
            if (menuService.updateItem(item)) {
                MessageUtils.showInfo(this, "Item updated.");
                loadAllItems();
                clearForm();
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    private void deleteItem() {
        if (selectedItemId < 0) return;
        if (!MessageUtils.confirm(this, "Delete this item? This cannot be undone.")) return;
        try {
            if (menuService.deleteItem(selectedItemId)) {
                MessageUtils.showInfo(this, "Item deleted.");
                loadAllItems();
                clearForm();
            }
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    private void searchItems() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadAllItems(); return; }
        try {
            populateTable(menuService.search(keyword));
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    public void loadAllItems() {
        try {
            populateTable(menuService.getAllItems());
        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    private void populateTable(List<MenuItem> items) {
        tableModel.setRowCount(0);
        for (MenuItem item : items) {
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                item.getCategory(),
                item.getPrice(),
                item.isAvailable() ? "Yes" : "No"
            });
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            MessageUtils.showValidationError(this, "Item name cannot be empty.");
            return false;
        }
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                MessageUtils.showValidationError(this, "Price must be greater than zero.");
                return false;
            }
        } catch (NumberFormatException e) {
            MessageUtils.showValidationError(this, "Price must be a valid number.");
            return false;
        }
        return true;
    }

    private MenuItem buildItemFromForm() {
        return new MenuItem(
            0,
            nameField.getText().trim(),
            (String) categoryCombo.getSelectedItem(),
            new BigDecimal(priceField.getText().trim()),
            availableCheck.isSelected()
        );
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        categoryCombo.setSelectedIndex(0);
        availableCheck.setSelected(true);
        selectedItemId = -1;
        addBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        menuTable.clearSelection();
    }

    private void addLabel(JPanel p, GridBagConstraints g, int x, int y, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 12));
        g.gridx = x; g.gridy = y; g.gridwidth = 1;
        p.add(lbl, g);
    }

    private void addField(JPanel p, GridBagConstraints g, int x, int y, JComponent field) {
        g.gridx = x; g.gridy = y; g.gridwidth = 1;
        p.add(field, g);
    }
}
