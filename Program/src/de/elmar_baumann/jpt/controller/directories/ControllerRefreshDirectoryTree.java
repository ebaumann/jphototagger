/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;

/**
 * Listens to {@link PopupMenuDirectories#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the tree
 * when <code>F5</code> was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-30
 */
public final class ControllerRefreshDirectoryTree
        implements ActionListener, KeyListener {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    public ControllerRefreshDirectoryTree() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        refresh();
    }

    private void refresh() {
        TreeModelAllSystemDirectories model =
                (TreeModelAllSystemDirectories) GUI.INSTANCE.getAppPanel().
                getTreeDirectories().getModel();
        model.update();
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
