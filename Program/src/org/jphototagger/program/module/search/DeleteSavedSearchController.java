package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class DeleteSavedSearchController implements ActionListener, KeyListener {

    public DeleteSavedSearchController() {
        listen();
    }

    private void listen() {
        SavedSearchesPopupMenu.INSTANCE.getItemDelete().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JXList list = GUI.getSavedSearchesList();

        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();

            if (value instanceof SavedSearch) {
                delete((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        delete(SavedSearchesPopupMenu.INSTANCE.getSavedSearch());
    }

    private void delete(SavedSearch savedSearch) {
        SavedSearchesUtil.delete(savedSearch);
        SavedSearchesUtil.focusAppPanelList();
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
