package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;

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
public final class ListCellRendererSavedSearches
        extends DefaultListCellRenderer {
    private static final Icon ICON             =
        AppLookAndFeel.getIcon("icon_search.png");
    private static final long serialVersionUID = 3108457488446314020L;
    private int               tempSelRow       = -1;

    public ListCellRendererSavedSearches() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                           index, isSelected, cellHasFocus);
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow  = index == tempSelRow;

        label.setForeground((isTempSelRow || (isSelected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionForeground()
                            : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (isSelected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionBackground()
                            : AppLookAndFeel.getListBackground());
        label.setIcon(ICON);

        return label;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
