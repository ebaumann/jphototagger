package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.lib.image.icon.IconUtil;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Rendert Favoritenverzeichnisse.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public class ListCellRendererFavoriteDirectories extends DefaultListCellRenderer {

    private static final ImageIcon icon = IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_favorite_small.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setIcon(icon);
        return label;
    }
}
