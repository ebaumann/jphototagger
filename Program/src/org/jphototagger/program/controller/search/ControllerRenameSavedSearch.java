package org.jphototagger.program.controller.search;

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameSavedSearch implements ActionListener, KeyListener {
    public ControllerRenameSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemRename().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (isRename(evt) &&!GUI.getSavedSearchesList().isSelectionEmpty()) {
            Object value = GUI.getSavedSearchesList().getSelectedValue();

            if (value instanceof SavedSearch) {
                rename((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        rename();
    }

    private boolean isRename(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    private void rename() {
        rename(PopupMenuSavedSearches.INSTANCE.getSavedSearch());
    }

    private void rename(SavedSearch savedSearch) {
        SavedSearchesHelper.rename(savedSearch);
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
