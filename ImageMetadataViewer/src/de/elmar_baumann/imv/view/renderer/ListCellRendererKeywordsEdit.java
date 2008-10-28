package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/28
 */
public class ListCellRendererKeywordsEdit extends DefaultListCellRenderer {

    private static final ImageIcon icon = IconUtil.getImageIcon(
        AppSettings.getDefaultAppIconPath() + "/icon_keyword_list.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(icon);
        return label;
    }
}
