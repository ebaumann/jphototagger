package org.jphototagger.program.module.directories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.bushe.swing.event.EventBus;

import org.jphototagger.api.file.event.DirectoryRenamedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.swing.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.filesystem.FileSystemDirectories;

/**
 * Listens to {@code DirectoriesPopupMenu#getItemRenameDirectory()} and
 * renames a directory when the action fires.
 *
 * Also listenes to the {@code JTree}'s key events and renames the selected
 * directory when the keys <code>Ctrl+R</code> or <code>F2</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class RenameDirectoryController extends DirectoryController {

    public RenameDirectoryController() {
        listenToActionsOf(DirectoriesPopupMenu.INSTANCE.getItemRenameDirectory());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == DirectoriesPopupMenu.INSTANCE.getItemRenameDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        final File oldDir = getDirOfNode(node);
        if (oldDir != null) {
            final File newDir = FileSystemDirectories.rename(oldDir);
            if (newDir != null) {
                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        node.setUserObject(newDir);
                        TreeFileSystemDirectories.updateInTreeModel(
                                ModelFactory.INSTANCE.getModel(AllSystemDirectoriesTreeModel.class), node);
                        EventBus.publish(new DirectoryRenamedEvent(this, oldDir, newDir));
                    }
                });
            }
        }
    }
}
