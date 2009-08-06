package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.TableIcons;
import de.elmar_baumann.imv.model.ListModelNoMetadata;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders list items of {@link ListModelNoMetadata}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class ListCellRendererNoMetadata extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof Column) {
            Column column = (Column) value;
            label.setText(column.getDescription());
            label.setIcon(TableIcons.getIcon(column.getTable()));
        }
        return label;
    }
}
