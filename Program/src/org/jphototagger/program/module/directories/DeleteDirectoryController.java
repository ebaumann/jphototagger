package org.jphototagger.program.module.directories;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.lib.swing.AllSystemDirectoriesTreeModel;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.filesystem.FileSystemDirectories;
import org.openide.util.Lookup;

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
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == DirectoriesPopupMenu.INSTANCE.getItemDeleteDirectory();
    }

    @Override
    protected void action(final DefaultMutableTreeNode node) {
        if (!isDeleteDirectoriesEnabled()) {
            return;
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

    private boolean isDeleteDirectoriesEnabled() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                : true;
    }
}
