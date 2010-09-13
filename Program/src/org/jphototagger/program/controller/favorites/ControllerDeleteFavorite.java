/*
 * @(#)ControllerDeleteFavorite.java    Created on 2008-09-23
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

package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.helper.FavoritesHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the {@link PopupMenuFavorites} and deletes a
 * selected favorite directory when the delete item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and deletes the selected
 * favorite if the <code>DEL</code> key was pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteFavorite extends ControllerFavorite {
    public ControllerDeleteFavorite() {
        listenToActionsOf(PopupMenuFavorites.INSTANCE.getItemDeleteFavorite());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuFavorites.INSTANCE.getItemDeleteFavorite();
    }

    @Override
    protected void action(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        FavoritesHelper.deleteFavorite(favorite);
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {

        // ignore
    }
}
