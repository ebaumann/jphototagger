/*
 * @(#)ControllerRenameSavedSearch.java    Created on 2008-09-10
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

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JList;

/**
 * Renames a selected saved search when the
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches} fires
 * the appropriate action.
 *
 * Also listens to the {@link JList}'s key events and renames a selected saved
 * search when the keys <code>Ctrl+R</code> or <code>F2</code> were pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerRenameSavedSearch
        implements ActionListener, KeyListener {
    public ControllerRenameSavedSearch() {
        listen();
    }

    private void listen() {
        PopupMenuSavedSearches.INSTANCE.getItemRename().addActionListener(this);
        ViewUtil.getSavedSearchesList().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (isRename(evt)
                &&!ViewUtil.getSavedSearchesList().isSelectionEmpty()) {
            Object value = ViewUtil.getSavedSearchesList().getSelectedValue();

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
        rename(PopupMenuSavedSearches.INSTANCE.getSavedSearch());
    }

    private void rename(SavedSearch savedSearch) {
        SavedSearchesHelper.rename(savedSearch);
        SavedSearchesHelper.focusAppPanelList();
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
