package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * Beobachtet Mausklicks im JTree mit gespeicherten Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-31
 */
public final class MouseListenerSavedSearches extends MouseAdapter {

    private final PopupMenuSavedSearches popupMenu =
            PopupMenuSavedSearches.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            int index = ListUtil.getItemIndex(e);
            JList list = (JList) e.getSource();
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
            popupMenu.show(list, e.getX(), e.getY());
        }
    }
}
