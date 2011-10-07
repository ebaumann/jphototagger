package org.jphototagger.program.module.search;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jdesktop.swingx.JXList;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;
import org.jphototagger.program.module.search.SavedSearchesPopupMenu;

/**
 * Do not use this class as a template for other implementations! Instead extend
 * a popup menu from {@code org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public final class SavedSearchesMouseListener extends MouseAdapter {

    private final SavedSearchesPopupMenu popupMenu = SavedSearchesPopupMenu.INSTANCE;

    @Override
    public void mousePressed(MouseEvent evt) {
        if (MouseEventUtil.isPopupTrigger(evt)) {
            int index = ListUtil.getItemIndex(evt);
            JXList list = (JXList) evt.getSource();
            boolean isItem = index >= 0;

            if (isItem) {
                int modelIndex = list.convertIndexToModel(index);
                Object element = list.getModel().getElementAt(modelIndex);

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
