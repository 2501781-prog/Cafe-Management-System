package com.cafe.gui;

/**
 * @author GulNaaz
 * Module 4: Billing & Reports
 */

import com.cafe.utils.UIUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * BarChartPanel – a lightweight bar chart drawn with Java2D.
 *
 * No external charting library needed.
 * Call setData() to refresh the chart with new values.
 */
public class BarChartPanel extends JPanel {

    private List<String> labels = new ArrayList<>();
    private List<BigDecimal> values = new ArrayList<>();
    private String title = "Daily Revenue";

    private static final Color BAR_COLOR_TOP    = new Color(59, 130, 246);   // blue-500
    private static final Color BAR_COLOR_BOTTOM = new Color(29,  78, 216);   // blue-700
    private static final Color GRID_COLOR       = new Color(226, 232, 240);
    private static final int   PADDING          = 50;
    private static final int   BAR_GAP          = 8;

    public BarChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 260));
    }

    /** Replace chart data and repaint. */
    public void setData(List<String> labels, List<BigDecimal> values, String title) {
        this.labels = new ArrayList<>(labels);
        this.values = new ArrayList<>(values);
        this.title  = title;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Title
        g2.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 14));
        g2.setColor(UIUtils.TEXT);
        g2.drawString(title, PADDING, 22);

        if (values.isEmpty()) {
            g2.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 13));
            g2.setColor(UIUtils.MUTED);
            g2.drawString("No data for selected range", w / 2 - 80, h / 2);
            return;
        }

        int chartTop    = 36;
        int chartBottom = h - PADDING;
        int chartLeft   = PADDING + 10;
        int chartRight  = w - 20;
        int chartHeight = chartBottom - chartTop;
        int chartWidth  = chartRight - chartLeft;

        // Find max value for scaling
        BigDecimal maxVal = values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);
        if (maxVal.compareTo(BigDecimal.ZERO) == 0) maxVal = BigDecimal.ONE;

        // Horizontal grid lines (5 lines)
        g2.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 10));
        g2.setColor(UIUtils.MUTED);
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            int y = chartBottom - (int) ((double) i / gridLines * chartHeight);
            g2.setColor(GRID_COLOR);
            g2.drawLine(chartLeft, y, chartRight, y);
            // Y-axis label
            BigDecimal labelVal = maxVal.multiply(BigDecimal.valueOf(i)).divide(BigDecimal.valueOf(gridLines));
            g2.setColor(UIUtils.MUTED);
            g2.drawString(formatShort(labelVal), 4, y + 4);
        }

        // Bars
        int n = values.size();
        int totalBarWidth = chartWidth - (n - 1) * BAR_GAP;
        int barWidth = Math.max(8, totalBarWidth / n);

        for (int i = 0; i < n; i++) {
            BigDecimal val = values.get(i);
            double ratio = val.doubleValue() / maxVal.doubleValue();
            int barHeight = (int) (ratio * chartHeight);
            int x = chartLeft + i * (barWidth + BAR_GAP);
            int y = chartBottom - barHeight;

            // Gradient bar
            GradientPaint gp = new GradientPaint(x, y, BAR_COLOR_TOP, x, chartBottom, BAR_COLOR_BOTTOM);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, barWidth, barHeight, 4, 4);

            // Value label on top of bar
            if (barHeight > 18) {
                g2.setFont(new Font(UIUtils.FONT_FAMILY, Font.BOLD, 9));
                g2.setColor(Color.WHITE);
                String valStr = formatShort(val);
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (barWidth - fm.stringWidth(valStr)) / 2;
                g2.drawString(valStr, tx, y + 12);
            }

            // X-axis label
            g2.setFont(new Font(UIUtils.FONT_FAMILY, Font.PLAIN, 9));
            g2.setColor(UIUtils.MUTED);
            String lbl = labels.get(i);
            // Shorten date labels: "2024-05-25" → "25"
            if (lbl.length() == 10 && lbl.charAt(4) == '-') {
                lbl = lbl.substring(8);
            }
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (barWidth - fm.stringWidth(lbl)) / 2;
            g2.drawString(lbl, tx, chartBottom + 14);
        }
    }

    /** Format large numbers compactly: 1500 → "1.5K", 1000000 → "1M" */
    private String formatShort(BigDecimal val) {
        double d = val.doubleValue();
        if (d >= 1_000_000) return String.format("%.1fM", d / 1_000_000);
        if (d >= 1_000)     return String.format("%.1fK", d / 1_000);
        return String.format("%.0f", d);
    }
}
