package org.jphototagger.program.module.keywords.list;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.app.ui.ListCellRendererExt;

/**
 * @author Elmar Baumann
 */
public final class KeywordsListCellRenderer extends ListCellRendererExt {

    private static final Icon ICON = AppLookAndFeel.getIcon("icon_keyword.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        int tempSelRow = getTempSelectionRow();
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
