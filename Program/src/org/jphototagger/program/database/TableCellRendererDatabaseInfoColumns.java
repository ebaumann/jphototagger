package org.jphototagger.program.database;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.program.view.renderer.FormatterLabelTableColumn;

/**
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererDatabaseInfoColumns implements TableCellRenderer {

    private static final String PADDING_LEFT = "  ";
    private final JLabel cellLabel = new JLabel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (column == 0) {
            FormatterLabelTableColumn.setLabelText(cellLabel, (MetaDataValue) value);
        } else {
            cellLabel.setText(PADDING_LEFT + value.toString());
        }

        return cellLabel;
    }
}
