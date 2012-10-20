package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesKeys;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.filesystem.FileSystemDirectories;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Listens to {@code FavoritesPopupMenu#getItemDeleteFilesystemFolder()} and
 * deletes a directory in the file system when the action fires.
 *
 * Also listens to the {@code JTree}'s key events and deletes the selected
 * file system directory if the <code>DEL</code> key was pressed.
 *
 * @author Elmar Baumann
 */
public final class DeleteFilesystemFolderFromFavoritesController implements ActionListener, KeyListener {

    public DeleteFilesystemFolderFromFavoritesController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemDeleteFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JTree tree = GUI.getFavoritesTree();
        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                deleteDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                TreePath treePath = FavoritesPopupMenu.INSTANCE.getTreePath();
                deleteDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(treePath));
            }
        });
    }

    private void deleteDirectory(DefaultMutableTreeNode node) {
        if (!isDeleteDirectoriesEnabled()) {
            return;
        }
        File dir = (node == null)
                ? null
                : TreeFileSystemDirectories.getFile(node);
        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                FavoritesTreeModel model = ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class);
                TreeFileSystemDirectories.removeFromTreeModel(model, node);
            }
        }
    }

    static boolean isDeleteDirectoriesEnabled() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                ? prefs.getBoolean(PreferencesKeys.KEY_ENABLE_DELETE_DIRECTORIES)
                : true;
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
