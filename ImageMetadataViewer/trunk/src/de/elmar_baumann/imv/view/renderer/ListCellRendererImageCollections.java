package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppTexts;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public final class ListCellRendererImageCollections extends DefaultListCellRenderer {

    private static final Icon ICON =
            AppIcons.getIcon("icon_imagecollection.png"); // NOI18N
    private static final Icon ICON_PREV_IMPORT =
            AppIcons.getIcon("icon_card.png");
    private static final Color COLOR_FOREGROUND_PREV_IMPORT = Color.BLUE;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        boolean isPrevImport = isPrevImport(value);
        if (isPrevImport) {
            label.setForeground(COLOR_FOREGROUND_PREV_IMPORT);
        }
        label.setIcon(isPrevImport
                      ? ICON_PREV_IMPORT
                      : ICON);
        return label;
    }

    private boolean isPrevImport(Object value) {
        return value == null
               ? false
               : value.toString().equalsIgnoreCase(
                AppTexts.DISPLAY_NAME_ITEM_IMAGE_COLLECTIONS_PREV_IMPORT);
    }
}
