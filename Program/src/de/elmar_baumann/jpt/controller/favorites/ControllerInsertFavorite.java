/*
 * @(#)ControllerInsertFavorite.java    2008-09-23
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
import de.elmar_baumann.jpt.view.dialogs.FavoritePropertiesDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link PopupMenuFavorites} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new favorite if
 * the keys <code>Ctrl+I</code> were pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerInsertFavorite
        implements ActionListener, KeyListener {
    private final AppPanel             appPanel         =
        GUI.INSTANCE.getAppPanel();
    private final JTree                tree             =
        appPanel.getTreeFavorites();
    private final PopupMenuDirectories popupDirectories =
        PopupMenuDirectories.INSTANCE;

    public ControllerInsertFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemInsertFavorite().addActionListener(
            this);
        popupDirectories.getItemAddToFavorites().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_I)) {
            insertFavorite(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        insertFavorite(getDirectoryName(e.getSource()));
    }

    private String getDirectoryName(Object o) {
        String  directoryName    = null;
        boolean isAddToFavorites =
            popupDirectories.getItemAddToFavorites().equals(o);

        if (isAddToFavorites) {
            directoryName = popupDirectories.getDirectoryName();
        }

        return directoryName;
    }

    private void insertFavorite(final String directoryName) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FavoritePropertiesDialog dialog =
                    new FavoritePropertiesDialog();

                if (directoryName != null) {
                    dialog.setDirectoryName(directoryName);
                    dialog.setEnabledButtonChooseDirectory(false);
                }

                dialog.setVisible(true);

                if (dialog.accepted()) {
                    TreeModelFavorites model = ModelFactory.INSTANCE.getModel(
                                                   TreeModelFavorites.class);

                    model.insert(new Favorite(dialog.getFavoriteName(),
                                              dialog.getDirectoryName(), -1));
                }
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
