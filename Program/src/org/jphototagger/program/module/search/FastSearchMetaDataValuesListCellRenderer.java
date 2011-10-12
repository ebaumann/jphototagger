package org.jphototagger.program.module.search;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * Renders elements of a {@code FastSearchComboBoxModel}.
 *
 * @author Elmar Baumann
 */
public final class FastSearchMetaDataValuesListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MetaDataValue) {
            MetaDataValue mdValue = (MetaDataValue) value;

            label.setText(mdValue.getDescription());
            label.setIcon(MetaDataValueIcons.getIcon(mdValue));
        } else if ((value != null) && value.equals(FastSearchComboBoxModel.ALL_DEFINED_META_DATA_VALUES)) {
            label.setText(Bundle.getString(FastSearchMetaDataValuesListCellRenderer.class, "FastSearchMetaDataValuesListCellRenderer.Text.AllDefinedValues"));
            label.setIcon(null);
        }

        return label;
    }
}
