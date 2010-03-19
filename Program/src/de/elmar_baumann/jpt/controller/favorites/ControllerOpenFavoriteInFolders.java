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
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
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
 * @author  Elmar Baumann
 */
public final class ControllerOpenFavoriteInFolders
        implements ActionListener, KeyListener {
    private final PopupMenuFavorites popupMenu           =
        PopupMenuFavorites.INSTANCE;
    private final AppPanel           appPanel            =
        GUI.INSTANCE.getAppPanel();
    private final JTabbedPane        tabbedPaneSelection =
        appPanel.getTabbedPaneSelection();
    private final Component tabTreeDirectories =
        appPanel.getTabSelectionDirectories();
    private final JTree treeDirectories         = appPanel.getTreeDirectories();
    private final JTree treeFavoriteDirectories = appPanel.getTreeFavorites();

    public ControllerOpenFavoriteInFolders() {
        listen();
    }

    private void listen() {
        popupMenu.getItemOpenInFolders().addActionListener(this);
        treeFavoriteDirectories.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_O)
                &&!treeFavoriteDirectories.isSelectionEmpty()) {
            selectDirectory();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (treeFavoriteDirectories.getSelectionCount() >= 0) {
            selectDirectory();
        }
    }

    private void selectDirectory() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreePath path = popupMenu.getTreePath();

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
                treeFavoriteDirectories.clearSelection();
                tabbedPaneSelection.setSelectedComponent(tabTreeDirectories);
                ModelFactory.INSTANCE.getModel(
                    TreeModelAllSystemDirectories.class).expandToFile(
                    dir, true);
            }
        });
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
