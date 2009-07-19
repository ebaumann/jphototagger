package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-08
 */
public final class MouseListenerImageCollections extends MouseAdapter {

    private final PopupMenuImageCollections popupMenu = PopupMenuImageCollections.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        JList list = (JList) e.getSource();
        int x = e.getX();
        int y = e.getY();
        int index = list.locationToIndex(new Point(x, y));
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isItem = index >= 0 && index == list.getSelectedIndex();
            popupMenu.setImageCollectionName(isItem
                ? list.getSelectedValue().toString()
                : null);
            popupMenu.getItemDelete().setEnabled(isItem);
            popupMenu.getItemRename().setEnabled(isItem);
            popupMenu.show(list, x, y);
        } else {
            if (index >= 0) {
                list.setSelectedIndex(index);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ( (JList) e.getSource()).setCursor(Cursor.getDefaultCursor());
    }
}
