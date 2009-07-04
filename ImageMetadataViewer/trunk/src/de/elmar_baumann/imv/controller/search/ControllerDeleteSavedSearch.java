package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;

/**
 * Deletes a saved search when the
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and deletes a selected saved
 * search when the <code>Del</code> key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteSavedSearch
        implements ActionListener, KeyListener {

    private final PopupMenuSavedSearches actionPopup =
            PopupMenuSavedSearches.INSTANCE;
    private final JList list = GUI.INSTANCE.getAppPanel().getListSavedSearches();

    public ControllerDeleteSavedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemDelete().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();
            if (value instanceof SavedSearch) {
                SavedSearchesModifier.delete((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SavedSearchesModifier.delete(actionPopup.getSavedSearch());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
