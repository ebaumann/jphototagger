package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererKeywords extends ListCellRendererExt {
    private static final Icon ICON = AppLookAndFeel.getIcon("icon_keyword.png");
    private static final long serialVersionUID = 8358358177217506189L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int tempSelRow = getTempSelRow();
        boolean tempSelRowIsSelected = tempSelRow < 0 ? false : list.isSelectedIndex(tempSelRow);

        setColors(index, isSelected, tempSelRowIsSelected, label);
        label.setIcon(ICON);

        return label;
    }

    // ListItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }
}
