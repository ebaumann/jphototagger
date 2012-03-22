package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jdesktop.swingx.JXList;

import org.jphototagger.domain.metadata.search.SavedSearch;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class EditSavedSearchController implements ActionListener, KeyListener {

    public EditSavedSearchController() {
        listen();
    }

    private void listen() {
        SavedSearchesPopupMenu.INSTANCE.getItemEdit().addActionListener(this);
        JXList savedSearchesList = GUI.getSavedSearchesList();
        savedSearchesList.addKeyListener(this);
        savedSearchesList.addMouseListener(mouseListener);
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

    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (MouseEventUtil.isDoubleClick(e)) {
                Object source = e.getSource();
                if (!(source instanceof JXList)) {
                    return;
                }
                JXList list = (JXList) source;
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Object element = list.getElementAt(index);
                    if (!(element instanceof SavedSearch)) {
                        return;
                    }
                    showAdvancedSearchDialog((SavedSearch) element);
                }
            }
        }
    };
}
