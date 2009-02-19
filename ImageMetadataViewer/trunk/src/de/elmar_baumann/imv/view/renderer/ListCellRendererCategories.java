package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.AppIcons;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Rendert Kategorien.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ListCellRendererCategories extends DefaultListCellRenderer {

    private static final Icon icon = AppIcons.getIcon("icon_category.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(icon);
        return label;
    }
}
