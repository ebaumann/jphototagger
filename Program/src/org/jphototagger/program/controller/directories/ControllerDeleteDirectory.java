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
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemDeleteDirectory());
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

        return evt.getSource() == PopupMenuDirectories.INSTANCE.getItemDeleteDirectory();
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
                            ModelFactory.INSTANCE.getModel(TreeModelAllSystemDirectories.class), node);
                    }
                });
            }
        }
    }
}
