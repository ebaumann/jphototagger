/*
 * @(#)ControllerMoveFavorite.java    2009-06-15
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

package de.elmar_baumann.jpt.controller.favorites;

import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuFavorites} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author  Elmar Baumann
 */
public final class ControllerMoveFavorite implements ActionListener {
    private final AppPanel           appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree              tree     = appPanel.getTreeFavorites();
    private final PopupMenuFavorites popup    = PopupMenuFavorites.INSTANCE;

    public ControllerMoveFavorite() {
        listen();
    }

    private void listen() {
        popup.getItemMoveUp().addActionListener(this);
        popup.getItemMoveDown().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean moveUp = popup.getItemMoveUp().equals(e.getSource());

        SwingUtilities.invokeLater(new MoveDir(moveUp));
    }

    private class MoveDir implements Runnable {
        private boolean up;

        public MoveDir(boolean up) {
            this.up = up;
        }

        @Override
        public void run() {
            if (up) {
                moveUp(getFavoriteDirectory());
            } else {
                moveDown(getFavoriteDirectory());
            }
        }

        private Favorite getFavoriteDirectory() {
            TreePath selPath = tree.getSelectionPath();

            if (selPath != null) {
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) selPath.getLastPathComponent();
                Object userObject = node.getUserObject();

                if (userObject instanceof Favorite) {
                    return (Favorite) userObject;
                }
            }

            return null;
        }

        private void moveUp(Favorite dir) {
            if (dir != null) {
                ModelFactory.INSTANCE.getModel(
                    TreeModelFavorites.class).moveUpFavorite(dir);
            }
        }

        private void moveDown(Favorite dir) {
            if (dir != null) {
                ModelFactory.INSTANCE.getModel(
                    TreeModelFavorites.class).moveDownFavorite(dir);
            }
        }
    }
}
