/*
 * @(#)ControllerDirectory.java    Created on 2010-01-19
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
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Base class for directory controllers.
 *
 * @author  Elmar Baumann
 */
abstract class ControllerDirectory extends Controller {
    protected abstract void action(DefaultMutableTreeNode node);

    ControllerDirectory() {
        listenToKeyEventsOf(ViewUtil.getDirectoriesTree());
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        DefaultMutableTreeNode node =
            TreeFileSystemDirectories.getNodeOfLastPathComponent(
                PopupMenuDirectories.INSTANCE.getTreePath());

        if (node != null) {
            action(node);
        }
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        JTree tree = ViewUtil.getDirectoriesTree();

        if (!tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();

            if (node instanceof DefaultMutableTreeNode) {
                action((DefaultMutableTreeNode) node);
            }
        }
    }

    protected File getDirOfNode(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        File dir = TreeFileSystemDirectories.getFile(node);

        if ((dir != null) && dir.isDirectory()) {
            return dir;
        }

        return null;
    }
}
