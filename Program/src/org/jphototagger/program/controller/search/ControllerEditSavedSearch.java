/*
 * @(#)ControllerEditSavedSearch.java    Created on 2008-09-10
 *
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

package org.jphototagger.program.controller.search;

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;
import org.jphototagger.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;

/**
 * Edits a selected saved search when the
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and edits a selected saved
 * search when the keys <code>Ctrl+E</code> were pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerEditSavedSearch
        implements ActionListener, KeyListener {
    private final PopupMenuSavedSearches actionPopup =
        PopupMenuSavedSearches.INSTANCE;
    private final JList list =
        GUI.INSTANCE.getAppPanel().getListSavedSearches();

    public ControllerEditSavedSearch() {
        listen();
    }

    private void listen() {
        actionPopup.getItemEdit().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_E)
                &&!list.isSelectionEmpty()) {
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
        AdvancedSearchDialog.INSTANCE.setSavedSearch(savedSearch);
        ControllerFactory.INSTANCE.getController(
            ControllerShowAdvancedSearchDialog.class).showDialog();
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
