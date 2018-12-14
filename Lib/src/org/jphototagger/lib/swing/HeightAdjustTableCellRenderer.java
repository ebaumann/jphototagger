package org.jphototagger.lib.swing;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.jphototagger.resources.UiFactory;

/**
 * @author Elmar Baumann
 */
public final class HeightAdjustTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int preferredHeight = label.getPreferredSize().height;
        if (table.getRowHeight() < preferredHeight) {
            table.setRowHeight(preferredHeight + UiFactory.scale(4));
        }

        return label;
    }
}
