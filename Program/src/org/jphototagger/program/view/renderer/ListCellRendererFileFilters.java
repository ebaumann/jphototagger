package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.app.AppLookAndFeel;

import java.awt.Component;

import java.io.FileFilter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererFileFilters extends DefaultListCellRenderer {
    private static final long serialVersionUID = 5485874791973375371L;
    private static final Icon ICON = AppLookAndFeel.ICON_FILTER;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof FileFilter) {
            String displayName = AppFileFilters.getDisplaynameOf((FileFilter) value);

            if (displayName != null) {
                label.setText(displayName);
            }
        }

        label.setIcon(ICON);

        return label;
    }
}
