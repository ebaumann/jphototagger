package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.model.ListModelImageCollections;
import org.jphototagger.program.view.popupmenus.PopupMenuImageCollections;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import org.jdesktop.swingx.JXList;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerImageCollections extends MouseAdapter {
    private final PopupMenuImageCollections popupMenu = PopupMenuImageCollections.INSTANCE;

    @Override
    public void mousePressed(MouseEvent evt) {
        int index = ListUtil.getItemIndex(evt);

        popupMenu.setItemIndex(index);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            JXList list = (JXList) evt.getSource();
            boolean isItem = index >= 0;
            boolean isSpecialCollection = isSpecialCollection(list, index);

            popupMenu.getItemDelete().setEnabled(isItem &&!isSpecialCollection);
            popupMenu.getItemRename().setEnabled(isItem &&!isSpecialCollection);
            popupMenu.show(list, evt.getX(), evt.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        ((JList) evt.getSource()).setCursor(Cursor.getDefaultCursor());
    }

    private boolean isSpecialCollection(JXList list, int index) {
        if (index < 0) {
            return false;
        }

        int modelIndex = list.convertIndexToModel(index);
        Object o = list.getModel().getElementAt(modelIndex);

        if (o != null) {
            return ListModelImageCollections.isSpecialCollection(o.toString());
        }

        return false;
    }
}
