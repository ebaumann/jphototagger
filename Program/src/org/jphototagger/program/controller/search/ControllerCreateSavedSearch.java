/*
 * @(#)ControllerCreateSavedSearch.java    Created on 2008-09-10
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

import org.jphototagger.program.event.listener.SearchListener;
import org.jphototagger.program.event.SearchEvent;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.ModifySavedSearches;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;

/**
 * Creates a saved search when the
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and creates a new saved
 * search when the keys <code>Ctrl+N</code> were pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerCreateSavedSearch
        implements ActionListener, KeyListener, SearchListener {
    public ControllerCreateSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemCreate().addActionListener(this);
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel()
            .addSearchListener(this);
        GUI.INSTANCE.getAppPanel().getListSavedSearches().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_N) {
            ControllerFactory.INSTANCE.getController(
                ControllerShowAdvancedSearchDialog.class).showDialog();
        }
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.SAVE)) {
            ModifySavedSearches.insert(evt.getSavedSearch(),
                                       evt.isForceOverwrite());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
