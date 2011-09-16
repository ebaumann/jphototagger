package org.jphototagger.program.controller.directories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.model.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.FileSystemDirectories;
import org.jphototagger.program.view.popupmenus.DirectoriesPopupMenu;

/**
 * Listens to {@code DirectoriesPopupMenu#getItemDeleteDirectory()} and
 * deletes a directory when the action fires.
 *
 * Also listens to the directorie's {@code JTree} key events and deletes the
 * selected directory if the delete key was typed.
 *
 * @author Elmar Baumann
 */
public final class DeleteDirectoryController extends DirectoryController {

    public DeleteDirectoryController() {
        listenToActionsOf(DirectoriesPopupMenu.INSTANCE.getItemDeleteDirectory());
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

        return evt.getSource() == DirectoriesPopupMenu.INSTANCE.getItemDeleteDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        if (node == null) {
            throw new NullPointerException("node == null");
        }

        File dir = getDirOfNode(node);

        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        TreeFileSystemDirectories.removeFromTreeModel(
                                ModelFactory.INSTANCE.getModel(AllSystemDirectoriesTreeModel.class), node);
                    }
                });
            }
        }
    }
}
