package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.UserDefinedFileFilter;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererUserDefinedFileFilterType extends DefaultListCellRenderer {
    private static final long serialVersionUID = 3377131281051796463L;
    private static final Icon ICON = AppLookAndFeel.ICON_FILTER;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof UserDefinedFileFilter.Type) {
            UserDefinedFileFilter.Type type = (UserDefinedFileFilter.Type) value;

            label.setText(type.getDisplayName());
        }

        label.setIcon(ICON);

        return label;
    }
}
