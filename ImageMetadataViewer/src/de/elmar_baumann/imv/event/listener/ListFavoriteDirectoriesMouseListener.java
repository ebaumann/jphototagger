package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * Behandelt Mauskereignisse in der Liste für Favoritenverzeichnisse.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public class ListFavoriteDirectoriesMouseListener extends MouseAdapter {

    private PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();

    @Override
    public void mousePressed(MouseEvent e) {
        JList list = (JList) e.getSource();
        int x = e.getX();
        int y = e.getY();
        int index = list.locationToIndex(new Point(x, y));
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isItem = index >= 0 && index == list.getSelectedIndex();
            popup.setFavoriteDirectory(isItem
                ? (FavoriteDirectory) list.getSelectedValue()
                : null);
            popup.setEnabledDelete(isItem);
            popup.setEnabledUpdate(isItem);
            popup.setEnabledOpenInFolders(isItem);
            popup.show(list, x, y);
        } else {
            if (index >= 0) {
                list.setSelectedIndex(index);
            }
        }
    }
}
