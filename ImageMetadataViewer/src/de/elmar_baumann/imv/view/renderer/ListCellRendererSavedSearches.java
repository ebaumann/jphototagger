package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
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
public class ListCellRendererSavedSearches extends DefaultListCellRenderer {

    private static final Icon icon = IconUtil.getImageIcon("/de/elmar_baumann/imv/resource/icon_saved_searches_child.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(icon);
        return label;
    }
}
