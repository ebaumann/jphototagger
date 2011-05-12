package org.jphototagger.program.controller.directories;

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
import org.jphototagger.lib.awt.EventQueueUtil;

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
        listenToActionsOf(PopupMenuDirectories.INSTANCE.getItemRenameDirectory());
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

        return evt.getSource() == PopupMenuDirectories.INSTANCE.getItemRenameDirectory();
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
                EventQueueUtil.invokeInDispatchThread(new Runnable() {
                    @Override
                    public void run() {
                        node.setUserObject(newDir);
                        TreeFileSystemDirectories.updateInTreeModel(
                            ModelFactory.INSTANCE.getModel(TreeModelAllSystemDirectories.class), node);
                    }
                });
            }
        }
    }
}
