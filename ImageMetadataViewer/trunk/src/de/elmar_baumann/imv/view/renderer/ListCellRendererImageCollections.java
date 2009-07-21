package de.elmar_baumann.imv.view.renderer;

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

    {
        ICON_OF_VALUE.put(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT,
                AppIcons.getIcon("icon_card.png"));
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PICKED,
                AppIcons.getIcon("icon_picked.png"));
        ICON_OF_VALUE.put(AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_REJECTED,
                AppIcons.getIcon("icon_rejected.png"));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (ListModelImageCollections.isSpecialCollection(value.toString())) {
            label.setForeground(COLOR_FOREGROUND_PREV_IMPORT);
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

    private boolean hasItemText(Object value, String text) {
        return value == null
               ? false
               : value.toString().equalsIgnoreCase(text);
    }
}
