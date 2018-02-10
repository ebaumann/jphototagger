package org.jphototagger.program.module.thumbnails;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class FileFiltersListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;
    private static final Icon ICON = Icons.ICON_FILTER;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof DisplayNameProvider) {
            DisplayNameProvider displayNameProvider = (DisplayNameProvider) value;
            String displayName = displayNameProvider.getDisplayName();
            label.setText(displayName);
        }
        label.setIcon(ICON);
        return label;
    }
}
