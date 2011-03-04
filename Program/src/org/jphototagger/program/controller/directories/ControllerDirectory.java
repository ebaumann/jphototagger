package org.jphototagger.program.controller.directories;

import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuDirectories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Base class for directory controllers.
 *
 * @author Elmar Baumann
 */
abstract class ControllerDirectory extends Controller {
    protected abstract void action(DefaultMutableTreeNode node);

    ControllerDirectory() {
        listenToKeyEventsOf(GUI.getDirectoriesTree());
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        DefaultMutableTreeNode node =
            TreeFileSystemDirectories.getNodeOfLastPathComponent(PopupMenuDirectories.INSTANCE.getTreePath());

        if (node != null) {
            action(node);
        }
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        JTree tree = GUI.getDirectoriesTree();

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
