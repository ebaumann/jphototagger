/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.search;

import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.helper.ModifySavedSearches;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuSavedSearches;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;

/**
 * Deletes a saved search when the
 * {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and deletes a selected saved
 * search when the <code>Del</code> key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
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
                ModifySavedSearches.delete((SavedSearch) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ModifySavedSearches.delete(actionPopup.getSavedSearch());
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
