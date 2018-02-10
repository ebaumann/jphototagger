package org.jphototagger.program.module.search;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class SavedSearchesListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON = Icons.getIcon("icon_search.png");
    private static final long serialVersionUID = 1L;
    private int tempSelRow = -1;

    public SavedSearchesListCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = index == tempSelRow;
        label.setForeground((isTempSelRow || (isSelected && !tempSelExists))
                ? AppLookAndFeel.getListSelectionForeground()
                : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (isSelected && !tempSelExists))
                ? AppLookAndFeel.getListSelectionBackground()
                : AppLookAndFeel.getListBackground());
        label.setIcon(ICON);
        return label;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
