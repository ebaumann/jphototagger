package org.jphototagger.program.view.renderer;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.TableIcons;
import org.jphototagger.program.model.ListModelNoMetadata;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders list items of {@link ListModelNoMetadata}.
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererNoMetadata extends DefaultListCellRenderer {
    private static final long serialVersionUID = -5033440934166574955L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Column) {
            Column column = (Column) value;

            label.setText(column.getDescription());
            label.setIcon(TableIcons.getIcon(column.getTablename()));
        }

        return label;
    }
}
