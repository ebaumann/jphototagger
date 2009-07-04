package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.SearchEvent;
import de.elmar_baumann.imv.event.listener.SearchListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.SavedSearchesModifier;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;

/**
 * Creates a saved search when the
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and creates a new saved
 * search when the keys <code>Ctrl+N</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerCreateSavedSearch
        implements ActionListener, KeyListener, SearchListener {

    public ControllerCreateSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemCreate().addActionListener(
                this);
        ListenerProvider.INSTANCE.addSearchListener(this);
        GUI.INSTANCE.getAppPanel().getListSavedSearches().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_N) {
            showAdvancedSearchDialog();
        }
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.SAVE)) {
            SavedSearchesModifier.insert(evt.getSafedSearch(), evt.
                    isForceOverwrite());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog();
    }

    private void showAdvancedSearchDialog() {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.INSTANCE;
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
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
