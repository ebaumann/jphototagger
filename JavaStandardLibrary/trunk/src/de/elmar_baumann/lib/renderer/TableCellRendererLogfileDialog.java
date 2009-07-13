package de.elmar_baumann.lib.renderer;

import de.elmar_baumann.lib.resource.LogLevelIcons;
import de.elmar_baumann.lib.resource.Bundle;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renders the {@link java.util.logging.Level} icons displayed in the GUI of
 * {@link de.elmar_baumann.lib.dialog.LogfileDialog}. Also formats dates and
 * selected table rows.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/03
 */
public final class TableCellRendererLogfileDialog implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();
        if (value instanceof Level) {
            cellLabel.setIcon(LogLevelIcons.getIcon((Level) value));
        } else if (value instanceof Date) {
            renderDate(cellLabel, (Date) value);
        } else {
            cellLabel.setText(value.toString());
        }
        renderSelection(cellLabel, isSelected);
        return cellLabel;
    }

    private void renderSelection(JLabel cellLabel, boolean isSelected) {
        cellLabel.setForeground(Color.BLACK);
        cellLabel.setBackground(isSelected ? new Color(251, 225, 146)
            : Color.WHITE);
        cellLabel.setOpaque(true);
    }

    private void renderDate(JLabel cellLabel, Date date) {
        SimpleDateFormat format =
            new SimpleDateFormat(Bundle.getString("TableCellRendererLogfileDialog.DateFormat")); // NOI18N
        cellLabel.setText(format.format(date));
    }
}
