package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.resource.GUI;

/**
 *
 * @author Elmar Baumann
 */
public final class EditSavedSearchController implements ActionListener, KeyListener {

    public EditSavedSearchController() {
        listen();
    }

    private void listen() {
        SavedSearchesPopupMenu.INSTANCE.getItemEdit().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getSavedSearchesList();

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_E) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof SavedSearch) {
                showAdvancedSearchDialog((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showAdvancedSearchDialog(SavedSearchesPopupMenu.INSTANCE.getSavedSearch());
    }

    private void showAdvancedSearchDialog(SavedSearch savedSearch) {
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel().setSavedSearch(savedSearch);
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
