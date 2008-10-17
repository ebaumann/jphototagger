package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListSavedSearches;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * Beobachtet Mausklicks im JTree mit gespeicherten Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/31
 */
public class ListSavedSearchesMouseListener extends MouseAdapter {

    private PopupMenuListSavedSearches popup = PopupMenuListSavedSearches.getInstance();

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof JList) {
            JList list = (JList) e.getSource();
            int x = e.getX();
            int y = e.getY();
            int index = list.locationToIndex(new Point(x, y));
            if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
                boolean isItem = index >= 0 && index == list.getSelectedIndex();
                Object selected = list.getSelectedValue();
                if (selected instanceof SavedSearch) {
                    SavedSearch data = (SavedSearch) selected;
                    popup.setSavedSearch(data);
                }
                popup.setEnabledDelete(isItem);
                popup.setEnabledEdit(isItem);
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
