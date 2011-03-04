package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerSavedSearches extends MouseAdapter {
    private final PopupMenuSavedSearches popupMenu = PopupMenuSavedSearches.INSTANCE;

    @Override
    public void mousePressed(MouseEvent evt) {
        if (MouseEventUtil.isPopupTrigger(evt)) {
            int index = ListUtil.getItemIndex(evt);
            JList list = (JList) evt.getSource();
            boolean isItem = index >= 0;

            if (isItem) {
                Object element = list.getModel().getElementAt(index);

                if (element instanceof SavedSearch) {
                    SavedSearch savedSearch = (SavedSearch) element;

                    popupMenu.setSavedSearch(savedSearch);
                }
            }

            popupMenu.getItemEdit().setEnabled(isItem);
            popupMenu.getItemDelete().setEnabled(isItem);
            popupMenu.getItemRename().setEnabled(isItem);
            popupMenu.show(list, evt.getX(), evt.getY());
        }
    }
}
