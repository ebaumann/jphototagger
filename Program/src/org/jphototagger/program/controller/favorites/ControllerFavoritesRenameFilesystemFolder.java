package org.jphototagger.program.controller.favorites;

import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.io.FileSystemDirectories;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to {@link PopupMenuFavorites#getItemRenameFilesystemFolder()} and
 * renames a directory in the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and renames the selected
 * file system directory if the keys <code>Strg+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerFavoritesRenameFilesystemFolder implements ActionListener, KeyListener {
    public ControllerFavoritesRenameFilesystemFolder() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemRenameFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JTree tree = GUI.getFavoritesTree();

        if (isRename(evt) &&!tree.isSelectionEmpty()) {
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
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(PopupMenuFavorites.INSTANCE.getTreePath()));
            }
        });
    }

    private boolean isRename(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameDirectory(DefaultMutableTreeNode node) {
        File dir = getFile(node);

        if (dir != null) {
            File newDir = FileSystemDirectories.rename(dir);

            if (newDir != null) {
                TreeModelFavorites model = ModelFactory.INSTANCE.getModel(TreeModelFavorites.class);

                node.setUserObject(newDir);
                TreeFileSystemDirectories.updateInTreeModel(model, node);
                ControllerFactory.INSTANCE.getController(ControllerRefreshFavorites.class).refresh();
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
