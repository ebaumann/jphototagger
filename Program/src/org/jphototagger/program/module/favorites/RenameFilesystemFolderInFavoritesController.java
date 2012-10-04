package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.file.event.DirectoryRenamedEvent;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.module.filesystem.FileSystemDirectories;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to {@code FavoritesPopupMenu#getItemRenameFilesystemFolder()} and
 * renames a directory in the file system when the action fires.
 *
 * Also listens to the {@code JTree}'s key events and renames the selected
 * file system directory if the keys <code>Strg+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class RenameFilesystemFolderInFavoritesController implements ActionListener, KeyListener {

    public RenameFilesystemFolderInFavoritesController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemRenameFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JTree tree = GUI.getFavoritesTree();
        if (isRename(evt) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                renameDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                renameDirectory(
                        TreeFileSystemDirectories.getNodeOfLastPathComponent(FavoritesPopupMenu.INSTANCE.getTreePath()));
            }
        });
    }

    private boolean isRename(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameDirectory(DefaultMutableTreeNode node) {
        File oldDir = getFile(node);
        if (oldDir != null) {
            File newDir = FileSystemDirectories.rename(oldDir);
            if (newDir != null) {
                FavoritesTreeModel model = ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class);
                node.setUserObject(newDir);
                TreeFileSystemDirectories.updateInTreeModel(model, node);
                ControllerFactory.INSTANCE.getController(RefreshFavoritesController.class).refresh();
                EventBus.publish(new DirectoryRenamedEvent(this, oldDir, newDir));
            }
        }
    }

    private File getFile(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof File) {
            return (File) userObject;
        } else if (userObject instanceof Favorite) {
            return ((Favorite) userObject).getDirectory();
        }
        return null;
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
