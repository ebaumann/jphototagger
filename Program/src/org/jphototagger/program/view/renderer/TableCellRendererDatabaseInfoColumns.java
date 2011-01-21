package org.jphototagger.program.view.renderer;

import org.jphototagger.program.database.metadata.Column;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Rendert die Tabellenzellen der Datenbankinformation über die Anzahl der
 * Datensätze bezogen auf eine bestimmte Tabellenspalte.
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererDatabaseInfoColumns
        implements TableCellRenderer {
    private static final String PADDING_LEFT = "  ";

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel cellLabel = new JLabel();

        if (column == 0) {
            FormatterLabelTableColumn.setLabelText(cellLabel, (Column) value);
        } else {
            cellLabel.setText(PADDING_LEFT + value.toString());
        }

        return cellLabel;
    }
}
