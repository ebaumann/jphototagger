package org.jphototagger.program.controller.search;

import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;
import org.jphototagger.program.resource.GUI;

/**
 * Edits a selected saved search when the
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and edits a selected saved
 * search when the keys <code>Ctrl+E</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerEditSavedSearch
        implements ActionListener, KeyListener {
    public ControllerEditSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemEdit().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JList list = GUI.getSavedSearchesList();

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_E)
                &&!list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof SavedSearch) {
                showAdvancedSearchDialog((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showAdvancedSearchDialog(
            PopupMenuSavedSearches.INSTANCE.getSavedSearch());
    }

    private void showAdvancedSearchDialog(SavedSearch savedSearch) {
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel().setSavedSearch(
            savedSearch);
        ComponentUtil.show(AdvancedSearchDialog.INSTANCE);
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
