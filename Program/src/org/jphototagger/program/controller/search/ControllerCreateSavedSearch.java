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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.search;

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;

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
public final class ControllerCreateSavedSearch extends Controller {
    private final JMenuItem menuItem =
        PopupMenuSavedSearches.INSTANCE.getItemCreate();

    public ControllerCreateSavedSearch() {
        listenToActionsOf(menuItem);
        listenToKeyEventsOf(GUI.INSTANCE.getAppPanel().getListSavedSearches());
    }

    public void displayEmptySearchDialog() {
        AdvancedSearchDialog.INSTANCE.getPanel().empty();
        ControllerFactory.INSTANCE.getController(
            ControllerShowAdvancedSearchDialog.class).showDialog();
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_N;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == menuItem;
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        displayEmptySearchDialog();
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        displayEmptySearchDialog();
    }
}
