package de.elmar_baumann.imagemetadataviewer.event.listener;

import de.elmar_baumann.imagemetadataviewer.data.FavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuListFavoriteDirectories;
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
        if (e.getSource() instanceof JList) {
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
                popup.show(list, x, y);
                setEnabledMove(isItem, list);
            } else {
                if (index >= 0) {
                    list.setSelectedIndex(index);
                }
            }
        }
    }

    private void setEnabledMove(boolean isItem, JList list) {
        if (isItem) {
            boolean canMoveUp = list.getSelectedIndex() > 0;
            boolean canMoveDown = list.getSelectedIndex() < list.getModel().getSize() - 1;
            popup.setEnabledMoveUp(canMoveUp);
            popup.setEnabledMoveDown(canMoveDown);
        }
    }
}
