package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class RenameSavedSearchController implements ActionListener, KeyListener {

    public RenameSavedSearchController() {
        listen();
    }

    private void listen() {
        SavedSearchesPopupMenu.INSTANCE.getItemRename().addActionListener(this);
        GUI.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (isRename(evt) && !GUI.getSavedSearchesList().isSelectionEmpty()) {
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
        rename(SavedSearchesPopupMenu.INSTANCE.getSavedSearch());
    }

    private void rename(SavedSearch savedSearch) {
        SavedSearchesUtil.rename(savedSearch);
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
