/*
 * @(#)ControllerRefreshDirectoryTree.java    Created on 2009-06-30
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

import org.jphototagger.lib.model.TreeModelAllSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the tree
 * when <code>F5</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerRefreshDirectoryTree extends ControllerDirectory {
    public ControllerRefreshDirectoryTree() {
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemRefresh());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_F5;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuDirectories.INSTANCE.getItemRefresh();
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        ModelFactory.INSTANCE.getModel(
            TreeModelAllSystemDirectories.class).update();
    }
}
