package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.model.ComboBoxModelFastSearch;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Renders elements of a {@link ComboBoxModelFastSearch}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-31
 */
public final class ListCellRendererFastSearchColumns extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof Column) {
            label.setText(((Column) value).getDescription());
        } else if (value.equals(ComboBoxModelFastSearch.ALL_DEFINED_COLUMNS)) {
            label.setText(Bundle.getString(
                    "ListCellRendererFastSearchColumns.Text.AllDefinedColumns")); // NOI18N
        } else {
            assert false : "Undefined value: " + value; // NOI18N
        }
        return label;
    }
}
