/*
 * @(#)ControllerOpenFavoriteInFolders.java    Created on 2008-11-05
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

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuFavorites} and opens the
 * selected favorite directory in the folder panel when the special menu item
 * was clicked.
 *
 * Also listens to the {@link JTree}'s key events and opens the selected folder
 * in the directorie's tree if the keys <code>Ctrl+O</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerOpenFavoriteInFolders
        implements ActionListener, KeyListener {
    public ControllerOpenFavoriteInFolders() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemOpenInFolders().addActionListener(
            this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_O)
                &&!GUI.getFavoritesTree().isSelectionEmpty()) {
            selectDirectory();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getFavoritesTree().getSelectionCount() >= 0) {
            selectDirectory();
        }
    }

    private void selectDirectory() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreePath path = PopupMenuFavorites.INSTANCE.getTreePath();

                if (path != null) {
                    File dir =
                        getDir((DefaultMutableTreeNode) path
                            .getLastPathComponent());

                    if ((dir != null) && dir.isDirectory()) {
                        expandTreeToDir(dir);
                    }
                }
            }
            private File getDir(DefaultMutableTreeNode node) {
                Object userObject = node.getUserObject();

                if (userObject instanceof File) {
                    return (File) userObject;
                } else if (userObject instanceof Favorite) {
                    Favorite favoriteDirectory = (Favorite) userObject;

                    return favoriteDirectory.getDirectory();
                }

                return null;
            }
            private void expandTreeToDir(File dir) {
                AppPanel    appPanel = GUI.getAppPanel();
                JTabbedPane tabbedPaneSelection =
                    appPanel.getTabbedPaneSelection();
                Component tabTreeDirectories =
                    appPanel.getTabSelectionDirectories();

                GUI.getFavoritesTree().clearSelection();
                tabbedPaneSelection.setSelectedComponent(tabTreeDirectories);
                ModelFactory.INSTANCE.getModel(
                    TreeModelAllSystemDirectories.class).expandToFile(
                    dir, true);
            }
        });
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
