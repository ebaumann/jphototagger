package org.jphototagger.program.view.renderer;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.api.component.DisplayNameProvider;
import org.jphototagger.program.app.AppLookAndFeel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileFiltersListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 5485874791973375371L;
    private static final Icon ICON = AppLookAndFeel.ICON_FILTER;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
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
