package org.jphototagger.program.module.nometadata;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class FilesWithoutMetaDataListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MetaDataValue) {
            MetaDataValue metaDataValue = (MetaDataValue) value;

            label.setText(metaDataValue.getDescription());
            label.setIcon(metaDataValue.getCategoryIcon());
        }

        return label;
    }
}
