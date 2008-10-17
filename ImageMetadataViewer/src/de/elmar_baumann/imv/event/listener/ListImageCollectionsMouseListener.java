package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuListImageCollections;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/08
 */
public class ListImageCollectionsMouseListener extends MouseAdapter {

    private PopupMenuListImageCollections popup = PopupMenuListImageCollections.getInstance();

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof JList) {
            JList list = (JList) e.getSource();
            int x = e.getX();
            int y = e.getY();
            int index = list.locationToIndex(new Point(x, y));
            if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
                boolean isItem = index >= 0 && index == list.getSelectedIndex();
                    popup.setImageCollectionName(isItem
                    ? list.getSelectedValue().toString()
                    : null);
                popup.setEnabledDelete(isItem);
                popup.setEnabledRename(isItem);
                popup.show(list, x, y);
            } else {
                if (index >= 0) {
                    list.setSelectedIndex(index);
                }
            }
        }
    }
}
