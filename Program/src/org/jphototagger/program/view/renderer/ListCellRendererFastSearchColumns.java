package org.jphototagger.program.view.renderer;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.ColumnIcons;
import org.jphototagger.program.model.ComboBoxModelFastSearch;
import org.jphototagger.program.resource.JptBundle;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders elements of a {@link ComboBoxModelFastSearch}.
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererFastSearchColumns extends DefaultListCellRenderer {
    private static final long serialVersionUID = 8142413010742459250L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Column) {
            Column column = (Column) value;

            label.setText(column.getDescription());
            label.setIcon(ColumnIcons.getIcon(column));
        } else if ((value != null) && value.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS)) {
            label.setText(JptBundle.INSTANCE.getString("ListCellRendererFastSearchColumns.Text.AllDefinedColumns"));
            label.setIcon(null);
        }

        return label;
    }
}
