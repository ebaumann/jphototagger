package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-08
 */
public final class MouseListenerImageCollections extends MouseAdapter {

    private final PopupMenuImageCollections popupMenu =
            PopupMenuImageCollections.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        int index = ListUtil.getItemIndex(e);
        popupMenu.setItemIndex(index);
        if (MouseEventUtil.isPopupTrigger(e)) {
            JList list = (JList) e.getSource();
            boolean isItem = index >= 0;
            boolean isSpecialCollection = isSpecialCollection(list, index);
            popupMenu.getItemDelete().setEnabled(isItem && !isSpecialCollection);
            popupMenu.getItemRename().setEnabled(isItem && !isSpecialCollection);
            popupMenu.show(list, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ((JList) e.getSource()).setCursor(Cursor.getDefaultCursor());
    }

    private boolean isSpecialCollection(JList list, int index) {
        if (index < 0) return false;
        Object o = list.getModel().getElementAt(index);
        if (o != null) {
            return ListModelImageCollections.isSpecialCollection(o.toString());
        }
        return false;
    }
}
