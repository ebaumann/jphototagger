/*
 * @(#)ControllerDeleteDirectory.java    Created on 2009-06-19
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

package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.io.FileSystemDirectories;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemDeleteDirectory()} and
 * deletes a directory when the action fires.
 *
 * Also listens to the directorie's {@link JTree} key events and deletes the
 * selected directory if the delete key was typed.
 *
 * @author  Elmar Baumann
 */
public final class ControllerDeleteDirectory extends ControllerDirectory {
    public ControllerDeleteDirectory() {
        listenToActionsOf(
            PopupMenuDirectories.INSTANCE.getItemDeleteDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource()
               == PopupMenuDirectories.INSTANCE.getItemDeleteDirectory();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        File dir = getDirOfNode(node);

        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                TreeFileSystemDirectories.removeFromTreeModel(
                    ModelFactory.INSTANCE.getModel(
                        TreeModelAllSystemDirectories.class), node);
            }
        }
    }
}
