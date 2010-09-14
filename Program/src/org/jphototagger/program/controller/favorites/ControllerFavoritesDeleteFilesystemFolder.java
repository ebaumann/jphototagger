/*
 * @(#)ControllerFavoritesDeleteFilesystemFolder.java    Created on 2009-06-30
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

import java.awt.EventQueue;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.FileSystemDirectories;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuFavorites#getItemDeleteFilesystemFolder()} and
 * deletes a directory in the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and deletes the selected
 * file system directory if the <code>DEL</code> key was pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerFavoritesDeleteFilesystemFolder
        implements ActionListener, KeyListener {
    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;
    private final JTree              tree =
        GUI.INSTANCE.getAppPanel().getTreeFavorites();

    public ControllerFavoritesDeleteFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteFilesystemFolder().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == KeyEvent.VK_DELETE)
                &&!tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();

            if (node instanceof DefaultMutableTreeNode) {
                deleteDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteDirectory(
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(
                        popup.getTreePath()));
            }
        });
    }

    private void deleteDirectory(DefaultMutableTreeNode node) {
        File dir = (node == null)
                   ? null
                   : TreeFileSystemDirectories.getFile(node);

        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                TreeFileSystemDirectories.removeFromTreeModel(
                    ModelFactory.INSTANCE.getModel(TreeModelFavorites.class),
                    node);
            }
        }
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
