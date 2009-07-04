package de.elmar_baumann.imv.controller.search;

import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;

/**
 * Edits a selected saved search when the
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and edits a selected saved
 * search when the keys <code>Ctrl+E</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerEditSafedSearch
        implements ActionListener, KeyListener {

    private final PopupMenuSavedSearches actionPopup =
            PopupMenuSavedSearches.INSTANCE;
    private final JList list = GUI.INSTANCE.getAppPanel().getListSavedSearches();

    public ControllerEditSafedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemEdit().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_E) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();
            if (value instanceof SavedSearch) {
                showAdvancedSearchDialog((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        showAdvancedSearchDialog(actionPopup.getSavedSearch());
    }

    private void showAdvancedSearchDialog(SavedSearch savedSearch) {
        AdvancedSearchDialog dialog = AdvancedSearchDialog.INSTANCE;
        dialog.setSavedSearch(savedSearch);
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
