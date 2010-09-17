/*
 * @(#)ControllerInsertFavorite.java    Created on 2008-09-23
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
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.view.dialogs.FavoritePropertiesDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.io.File;

import javax.swing.JTree;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to the {@link PopupMenuFavorites} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new favorite if
 * the keys <code>Ctrl+I</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerInsertFavorite
        implements ActionListener, KeyListener {
    public ControllerInsertFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemInsertFavorite().addActionListener(
            this);
        PopupMenuDirectories.INSTANCE.getItemAddToFavorites().addActionListener(
            this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_I)) {
            insertFavorite(null);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        insertFavorite(getDirectory(evt.getSource()));
    }

    private File getDirectory(Object o) {
        File    directory = null;
        boolean isAddToFavorites =
            PopupMenuDirectories.INSTANCE.getItemAddToFavorites().equals(o);

        if (isAddToFavorites) {
            directory = PopupMenuDirectories.INSTANCE.getDirectory();
        }

        return directory;
    }

    private void insertFavorite(final File directory) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                FavoritePropertiesDialog dlg = new FavoritePropertiesDialog();

                if (directory != null) {
                    dlg.setDirectory(directory);
                    dlg.setEnabledButtonChooseDirectory(false);
                }

                dlg.setVisible(true);

                if (dlg.isAccepted()) {
                    TreeModelFavorites model = ModelFactory.INSTANCE.getModel(
                                                   TreeModelFavorites.class);
                    Favorite favorite = new Favorite();

                    favorite.setName(dlg.getFavoriteName());
                    favorite.setDirectory(dlg.getDirectory());
                    model.insert(favorite);
                }
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
