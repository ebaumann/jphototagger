package org.jphototagger.program.view.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.metadata.selections.MetaDataValueIcons;
import org.jphototagger.program.model.ComboBoxModelFastSearch;

/**
 * Renders elements of a {@link ComboBoxModelFastSearch}.
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererFastSearchMetaDataValues extends DefaultListCellRenderer {
    private static final long serialVersionUID = 8142413010742459250L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MetaDataValue) {
            MetaDataValue mdValue = (MetaDataValue) value;

            label.setText(mdValue.getDescription());
            label.setIcon(MetaDataValueIcons.getIcon(mdValue));
        } else if ((value != null) && value.equals(ComboBoxModelFastSearch.ALL_DEFINED_META_DATA_VALUES)) {
            label.setText(Bundle.getString(ListCellRendererFastSearchMetaDataValues.class, "ListCellRendererFastSearchMetaDataValues.Text.AllDefinedValues"));
            label.setIcon(null);
        }

        return label;
    }
}
