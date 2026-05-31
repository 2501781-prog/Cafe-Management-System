package com.cafe.gui;

/**
 * @author GulNaaz
 * Module 4: Billing & Reports
 */

import com.cafe.services.SalesReportService;
import com.cafe.utils.MessageUtils;
import com.cafe.utils.UIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * SalesReportPanel – Module 4: Sales & Reports
 *
 * Layout:
 *   ┌─────────────────────────────────────────────────────────────┐
 *   │  Header: title + date-range picker + Generate button        │
 *   ├──────────┬──────────┬──────────┬──────────────────────────  │
 *   │ Revenue  │ Orders   │ Avg Order│  (summary cards row)       │
 *   ├──────────┴──────────┴──────────┴──────────────────────────  │
 *   │  Bar Chart (daily revenue)                                  │
 *   ├─────────────────────────────────────────────────────────────│
 *   │  Top Items table  │  Category Breakdown table               │
 *   └─────────────────────────────────────────────────────────────┘
 */
public class SalesReportPanel extends JPanel {

    private final SalesReportService service = new SalesReportService();

    // Summary card labels
    private final JLabel revenueValue   = metricLabel();
    private final JLabel ordersValue    = metricLabel();
    private final JLabel avgOrderValue  = metricLabel();
    private final JLabel dailyRevValue  = metricLabel();

    // Date pickers (using JSpinner with SpinnerDateModel)
    private final JSpinner fromSpinner;
    private final JSpinner toSpinner;

    // Chart
    private final BarChartPanel barChart = new BarChartPanel();

    // Tables
    private final DefaultTableModel topItemsModel;
    private final DefaultTableModel categoryModel;

    public SalesReportPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UIUtils.BACKGROUND);
        UIUtils.pad(this);

        // ── Date spinners ─────────────────────────────────────────
        SpinnerDateModel fromModel = new SpinnerDateModel();
        SpinnerDateModel toModel   = new SpinnerDateModel();
        fromSpinner = new JSpinner(fromModel);
        toSpinner   = new JSpinner(toModel);
        styleSpinner(fromSpinner);
        styleSpinner(toSpinner);

        // Default range: last 7 days
        java.util.Date today = new java.util.Date();
        toModel.setValue(today);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -6);
        fromModel.setValue(cal.getTime());

        // ── Table models ──────────────────────────────────────────
        topItemsModel = new DefaultTableModel(
                new Object[]{"#", "Item Name", "Category", "Qty Sold", "Revenue (Rs.)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        categoryModel = new DefaultTableModel(
                new Object[]{"Category", "Items Sold", "Revenue (Rs.)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // ── Assemble UI ───────────────────────────────────────────
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildBody(),    BorderLayout.CENTER);

        // Load data immediately
        generateReport();
    }

    // ─────────────────────────────────────────────────────────────────
    // Header: title + date range + button
    // ─────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setOpaque(false);

        header.add(UIUtils.createTitleBlock(
                "Sales & Reports",
                "Admin analytics – daily revenue, top items, category breakdown"),
                BorderLayout.WEST);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.add(label("From:"));
        controls.add(fromSpinner);
        controls.add(label("  To:"));
        controls.add(toSpinner);

        JButton generateBtn = UIUtils.createButton("Generate Report", UIUtils.PRIMARY);
        generateBtn.addActionListener(e -> generateReport());
        controls.add(generateBtn);

        header.add(controls, BorderLayout.EAST);
        return header;
    }

    // ─────────────────────────────────────────────────────────────────
    // Body: summary cards + chart + tables
    // ─────────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        // Summary cards row
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setOpaque(false);
        cards.add(summaryCard("Total Revenue",    revenueValue,  UIUtils.WARNING));
        cards.add(summaryCard("Total Orders",     ordersValue,   UIUtils.SUCCESS));
        cards.add(summaryCard("Avg Order Value",  avgOrderValue, UIUtils.PRIMARY));
        cards.add(summaryCard("Today's Revenue",  dailyRevValue, new Color(105, 89, 205)));

        // Chart panel
        JPanel chartWrapper = new JPanel(new BorderLayout());
        chartWrapper.setBackground(Color.WHITE);
        chartWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        chartWrapper.add(barChart, BorderLayout.CENTER);

        // Tables row
        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 14, 0));
        tablesRow.setOpaque(false);
        tablesRow.add(tableCard("Top 5 Best-Selling Items", topItemsModel));
        tablesRow.add(tableCard("Revenue by Category",      categoryModel));

        // Stack everything
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);
        center.add(chartWrapper, BorderLayout.CENTER);
        center.add(tablesRow,    BorderLayout.SOUTH);

        body.add(cards,  BorderLayout.NORTH);
        body.add(center, BorderLayout.CENTER);
        return body;
    }

    // ─────────────────────────────────────────────────────────────────
    // Generate / refresh all data
    // ─────────────────────────────────────────────────────────────────
    public void generateReport() {
        LocalDate from = toLocalDate(fromSpinner);
        LocalDate to   = toLocalDate(toSpinner);

        if (from.isAfter(to)) {
            MessageUtils.showValidationError(this, "\"From\" date must be before or equal to \"To\" date.");
            return;
        }

        try {
            // Summary cards
            BigDecimal[] summary = service.getRangeSummary(from, to);
            revenueValue.setText("Rs. " + fmt(summary[0]));
            ordersValue.setText(summary[1].toPlainString());
            avgOrderValue.setText("Rs. " + fmt(summary[2]));

            BigDecimal todayRev = service.getDailyRevenue(LocalDate.now());
            dailyRevValue.setText("Rs. " + fmt(todayRev));

            // Bar chart
            List<Object[]> dailyRows = service.getRevenueByDateRange(from, to);
            List<String>     chartLabels = new ArrayList<>();
            List<BigDecimal> chartValues = new ArrayList<>();
            for (Object[] row : dailyRows) {
                chartLabels.add((String) row[0]);
                chartValues.add((BigDecimal) row[1]);
            }
            String chartTitle = "Daily Revenue  (" + from + "  →  " + to + ")";
            barChart.setData(chartLabels, chartValues, chartTitle);

            // Top items table
            topItemsModel.setRowCount(0);
            List<Object[]> topItems = service.getTopSellingItems(from, to, 5);
            int rank = 1;
            for (Object[] row : topItems) {
                topItemsModel.addRow(new Object[]{
                    rank++,
                    row[0],                          // item_name
                    row[1],                          // category
                    row[2],                          // qty_sold
                    fmt((BigDecimal) row[3])         // revenue
                });
            }

            // Category table
            categoryModel.setRowCount(0);
            List<Object[]> catRows = service.getRevenueByCategory(from, to);
            for (Object[] row : catRows) {
                categoryModel.addRow(new Object[]{
                    row[0],                          // category
                    row[1],                          // total_sold
                    fmt((BigDecimal) row[2])         // revenue
                });
            }

        } catch (SQLException ex) {
            MessageUtils.showDatabaseError(this, ex);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Helper builders
    // ─────────────────────────────────────────────────────────────────
    private JPanel summaryCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = UIUtils.createCardPanel();
        card.setLayout(new BorderLayout(6, 6));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 12));
        titleLbl.setForeground(new Color(92, 105, 125));
        valueLabel.setForeground(accent);

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel tableCard(String title, DefaultTableModel model) {
        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 15));
        titleLbl.setForeground(UIUtils.TEXT);

        JTable table = new JTable(model);
        UIUtils.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(300, 160));

        card.add(titleLbl,              BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JLabel metricLabel() {
        JLabel lbl = new JLabel("—");
        lbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 22));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        return lbl;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 13));
        return lbl;
    }

    private void styleSpinner(JSpinner spinner) {
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(120, 32));
        UIUtils.styleTextField(editor.getTextField());
    }

    /** Convert JSpinner value to LocalDate */
    private LocalDate toLocalDate(JSpinner spinner) {
        java.util.Date d = (java.util.Date) spinner.getValue();
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /** Format BigDecimal to 2 decimal places */
    private String fmt(BigDecimal val) {
        if (val == null) return "0.00";
        return val.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
