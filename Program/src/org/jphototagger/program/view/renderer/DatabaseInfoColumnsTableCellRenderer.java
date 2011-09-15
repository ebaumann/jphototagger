package org.jphototagger.program.view.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 *
 * @author Elmar Baumann
 */
public final class DatabaseInfoColumnsTableCellRenderer implements TableCellRenderer {

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
