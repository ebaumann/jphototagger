package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppColors;
import de.elmar_baumann.imv.app.AppIcons;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class ListCellRendererSavedSearches extends DefaultListCellRenderer {

    private static final Icon ICON = AppIcons.getIcon("icon_search.png"); // NOI18N
    private int popupHighLightRow = -1;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        label.setIcon(ICON);
        if (index == popupHighLightRow) {
            label.setOpaque(true);
            label.setForeground(AppColors.COLOR_FOREGROUND_POPUP_HIGHLIGHT_LIST);
            label.setBackground(AppColors.COLOR_BACKGROUND_POPUP_HIGHLIGHT_LIST);
        }
        return label;
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
