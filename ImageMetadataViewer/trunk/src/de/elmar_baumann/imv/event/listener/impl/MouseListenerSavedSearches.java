package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.Point;
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
        JList list = (JList) e.getSource();
        int x = e.getX();
        int y = e.getY();
        int index = list.locationToIndex(new Point(x, y));
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isItem = index >= 0 && index == list.getSelectedIndex();
            Object selectedValue = list.getSelectedValue();
            if (selectedValue instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) selectedValue;
                popupMenu.setSavedSearch(savedSearch);
            }
            popupMenu.getItemEdit().setEnabled(isItem);
            popupMenu.getItemDelete().setEnabled(isItem);
            popupMenu.getItemRename().setEnabled(isItem);
            popupMenu.show(list, x, y);
        } else {
            if (index >= 0) {
                list.setSelectedIndex(index);
            }
        }
    }
}
