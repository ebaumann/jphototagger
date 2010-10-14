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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.directories;

import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.FileSystemDirectories;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.EventQueue;

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
 * @author Elmar Baumann
 */
public final class ControllerDeleteDirectory extends ControllerDirectory {
    public ControllerDeleteDirectory() {
        listenToActionsOf(
            PopupMenuDirectories.INSTANCE.getItemDeleteDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuDirectories.INSTANCE.getItemDeleteDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        File dir = getDirOfNode(node);

        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        TreeFileSystemDirectories.removeFromTreeModel(
                            ModelFactory.INSTANCE.getModel(
                                TreeModelAllSystemDirectories.class), node);
                    }
                });
            }
        }
    }
}
