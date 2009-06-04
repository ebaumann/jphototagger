package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert die Tabellenzellen der Datenbankinformation über die Anzahl der
 * Datensätze bezogen auf eine bestimmte Tabellenspalte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public final class TableCellRendererDatabaseInfoColumns implements TableCellRenderer {

    private static final String paddingLeft = "  "; // NOI18N

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();
        if (column == 0) {
            RendererTableColumn.setLabelText(cellLabel, (Column) value);
        } else {
            cellLabel.setText(paddingLeft + value.toString());
        }
        return cellLabel;
    }
}
