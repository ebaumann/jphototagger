package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppColors;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppTexts;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
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
public final class ListCellRendererImageCollections extends DefaultListCellRenderer {

    private static final Icon ICON_DEFAULT =
            AppIcons.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Color COLOR_FOREGROUND_PREV_IMPORT = Color.BLUE;
    private static final Map<Object, Icon> ICON_OF_VALUE =
            new HashMap<Object, Icon>();
    private int popupHighLightRow = -1;

    {
        ICON_OF_VALUE.put(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT,
                AppIcons.getIcon("icon_card.png")); // NOI18N
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED,
                AppIcons.getIcon("icon_picked.png")); // NOI18N
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED,
                AppIcons.getIcon("icon_rejected.png")); // NOI18N
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (ListModelImageCollections.isSpecialCollection(value.toString())
                && !isSelected) {
            label.setForeground(COLOR_FOREGROUND_PREV_IMPORT);
        }
        if (index == popupHighLightRow) {
            label.setForeground(AppColors.COLOR_FOREGROUND_POPUP_HIGHLIGHT_LIST);
            label.setBackground(AppColors.COLOR_BACKGROUND_POPUP_HIGHLIGHT_LIST);
            label.setOpaque(true);
        }
        label.setIcon(getIconOfValue(value));
        return label;
    }

    private Icon getIconOfValue(Object value) {
        Icon icon = ICON_OF_VALUE.get(value);
        return icon == null
               ? ICON_DEFAULT
               : icon;
    }

    public void setHighlightIndexForPopup(int index) {
        popupHighLightRow = index;
    }
}
