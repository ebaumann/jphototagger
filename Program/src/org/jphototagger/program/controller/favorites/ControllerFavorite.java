/*
 * @(#)ControllerFavorite.java    Created on 2010-01-20
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

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerFavorite extends Controller {
    protected abstract void action(Favorite favorite);

    protected abstract void action(DefaultMutableTreeNode node);

    ControllerFavorite() {
        listenToKeyEventsOf(GUI.getAppPanel().getTreeFavorites());
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (GUI.getAppPanel().getTreeFavorites().isSelectionEmpty()) {
            return;
        }

        DefaultMutableTreeNode node = getSelectedNodeFromTree();
        Object                 o    = node.getUserObject();

        if (o instanceof Favorite) {
            action((Favorite) o);
        }

        action(node);
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        Favorite favorite = PopupMenuFavorites.INSTANCE.getFavorite();

        if (favorite != null) {
            action(favorite);
        }
    }

    protected DefaultMutableTreeNode getSelectedNodeFromTree() {
        JTree  tree = GUI.getAppPanel().getTreeFavorites();
        Object node = tree.getSelectionPath().getLastPathComponent();

        if (node instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) node;
        }

        return null;
    }
}
