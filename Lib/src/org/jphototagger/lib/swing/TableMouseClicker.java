package org.jphototagger.lib.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * For simple tables without custom table cell editors but having buttons as values: If clicking onto that cell, a click
 * event will be sent to that button. Hint: Implement a custom renderer returning a button for that cell instead of a
 * label or use {@code TableMouseClicker.Renderer}.
 *
 * @author Elmar Baumann
 */
public final class TableMouseClicker {

    private final JTable table;

    public TableMouseClicker(JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }
        this.table = table;
        listen();
    }

    private void listen() {
        table.addMouseListener(tableButtonListener);
    }

    private final MouseListener tableButtonListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();
            int column = table.columnAtPoint(point);
            int row = table.rowAtPoint(point);
            if (row >= 0 && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof AbstractButton) {
                    ((AbstractButton) value).doClick();
                }
            }
        }
    };

    /**
     * If the table cell value is an {@code AbstractButton} the button will be returned.
     */
    public static class Renderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof AbstractButton) {
                return (AbstractButton) value;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }
}
