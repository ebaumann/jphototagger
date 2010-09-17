/*
 * @(#)ControllerRenameDirectory.java    Created on 2009-06-19
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

package org.jphototagger.program.controller.directories;

import java.awt.EventQueue;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.FileSystemDirectories;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemRenameDirectory()} and
 * renames a directory when the action fires.
 *
 * Also listenes to the {@link JTree}'s key events and renames the selected
 * directory when the keys <code>Ctrl+R</code> or <code>F2</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameDirectory extends ControllerDirectory {
    public ControllerRenameDirectory() {
        listenToActionsOf(
            PopupMenuDirectories.INSTANCE.getItemRenameDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuDirectories.INSTANCE.getItemRenameDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        File dir = getDirOfNode(node);

        if (dir != null) {
            final File newDir = FileSystemDirectories.rename(dir);

            if (newDir != null) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        node.setUserObject(newDir);
                        TreeFileSystemDirectories.updateInTreeModel(
                            ModelFactory.INSTANCE.getModel(
                                TreeModelAllSystemDirectories.class), node);
                    }
                });
            }
        }
    }
}
