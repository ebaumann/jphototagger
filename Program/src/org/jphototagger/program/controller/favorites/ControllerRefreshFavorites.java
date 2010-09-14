/*
 * @(#)ControllerRefreshFavorites.java    Created on 2009-06-28
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
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTree;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the view if
 * the key <code>F5</code> was pressed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerRefreshFavorites
        implements ActionListener, KeyListener {
    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerRefreshFavorites() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
        GUI.INSTANCE.getAppPanel().getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (popup.getItemRefresh().equals(evt.getSource())) {
            refresh();
        }
    }

    public void refresh() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ModelFactory.INSTANCE.getModel(
                    TreeModelFavorites.class).update();
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
