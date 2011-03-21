package org.jphototagger.program.controller.search;

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jdesktop.swingx.JXList;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerDeleteSavedSearch implements ActionListener, KeyListener {
    public ControllerDeleteSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemDelete().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getSavedSearchesList();

        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) &&!list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof SavedSearch) {
                delete((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        delete(PopupMenuSavedSearches.INSTANCE.getSavedSearch());
    }

    private void delete(SavedSearch savedSearch) {
        SavedSearchesHelper.delete(savedSearch);
        SavedSearchesHelper.focusAppPanelList();
    }

    @Override
    public void keyTyped(KeyEvent evt) {

        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {

        // ignore
    }
}
