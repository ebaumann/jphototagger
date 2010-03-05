/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelFavorites;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuFavorites;

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
 * @version 2009-06-28
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
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (popup.getItemRefresh().equals(e.getSource())) {
            refresh();
        }
    }

    private void refresh() {
        ModelFactory.INSTANCE.getModel(TreeModelFavorites.class).update();
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
