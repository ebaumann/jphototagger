/*
 * @(#)ControllerMoveFavorite.java    Created on 2009-06-15
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
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuFavorites} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author Elmar Baumann
 */
public final class ControllerMoveFavorite implements ActionListener {
    public ControllerMoveFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemMoveUp().addActionListener(this);
        PopupMenuFavorites.INSTANCE.getItemMoveDown().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean moveUp =
            PopupMenuFavorites.INSTANCE.getItemMoveUp().equals(evt.getSource());

        EventQueue.invokeLater(new MoveDir(moveUp));
    }

    private class MoveDir implements Runnable {
        private boolean up;

        MoveDir(boolean up) {
            this.up = up;
        }

        @Override
        public void run() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (up) {
                        moveUp(getFavoriteDirectory());
                    } else {
                        moveDown(getFavoriteDirectory());
                    }
                }
            });
        }

        private Favorite getFavoriteDirectory() {
            TreePath path = PopupMenuFavorites.INSTANCE.getTreePath();

            if (path != null) {
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) path.getLastPathComponent();
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
