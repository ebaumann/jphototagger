package org.jphototagger.program.event.listener;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jdesktop.swingx.JXList;

import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.view.popupmenus.ImageCollectionsPopupMenu;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionsMouseListener extends MouseAdapter {

    private final ImageCollectionsPopupMenu popupMenu = ImageCollectionsPopupMenu.INSTANCE;

    @Override
    public void mousePressed(MouseEvent evt) {
        int index = ListUtil.getItemIndex(evt);

        popupMenu.setItemIndex(index);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            JXList list = (JXList) evt.getSource();
            boolean isItem = index >= 0;
            boolean isSpecialCollection = isSpecialCollection(list, index);

            popupMenu.getItemDelete().setEnabled(isItem && !isSpecialCollection);
            popupMenu.getItemRename().setEnabled(isItem && !isSpecialCollection);
            popupMenu.show(list, evt.getX(), evt.getY());
        }
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        ((JXList) evt.getSource()).setCursor(Cursor.getDefaultCursor());
    }

    private boolean isSpecialCollection(JXList list, int index) {
        if (index < 0) {
            return false;
        }

        int modelIndex = list.convertIndexToModel(index);
        Object o = list.getModel().getElementAt(modelIndex);

        if (o != null) {
            return ImageCollection.isSpecialCollection(o.toString());
        }

        return false;
    }
}
