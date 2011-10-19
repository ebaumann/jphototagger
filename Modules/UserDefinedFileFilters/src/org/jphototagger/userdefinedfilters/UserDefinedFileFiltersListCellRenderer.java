package org.jphototagger.userdefinedfilters;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.lib.swing.IconUtil;

/**
 * @author Elmar Baumann
 */
class UserDefinedFileFiltersListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;
    private static final Icon ICON = IconUtil.getImageIcon(UserDefinedFileFiltersListCellRenderer.class, "filter.png");

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof UserDefinedFileFilter.Type) {
            UserDefinedFileFilter.Type type = (UserDefinedFileFilter.Type) value;
            label.setText(type.getDisplayName());
        }
        label.setIcon(ICON);
        return label;
    }

}
