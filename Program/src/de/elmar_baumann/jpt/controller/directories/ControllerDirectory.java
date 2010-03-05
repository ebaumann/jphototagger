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
package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Base class for directory controllers.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-19
 */
abstract class ControllerDirectory extends Controller {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree                tree  = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    protected abstract void action(DefaultMutableTreeNode node);

    public ControllerDirectory() {
        listenToKeyEventsOf(tree);
    }

    @Override
    protected void action(ActionEvent evt) {
        DefaultMutableTreeNode node = 
                TreeFileSystemDirectories.getNodeOfLastPathComponent(popup.getTreePath());
        if (node != null) {
            action(node);
        }
    }

    @Override
    protected void action(KeyEvent evt) {
        if (!tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                action((DefaultMutableTreeNode) node);
            }
        }
    }

    protected File getDirOfNode(DefaultMutableTreeNode node) {
        File dir = TreeFileSystemDirectories.getFile(node);
        if (dir != null && dir.isDirectory()) {
            return dir;
        }
        return null;
    }
}
