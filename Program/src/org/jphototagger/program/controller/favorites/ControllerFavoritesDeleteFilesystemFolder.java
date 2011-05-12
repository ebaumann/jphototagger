package org.jphototagger.program.controller.favorites;

import org.jphototagger.lib.io.TreeFileSystemDirectories;
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
 * Listens to {@link PopupMenuFavorites#getItemDeleteFilesystemFolder()} and
 * deletes a directory in the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and deletes the selected
 * file system directory if the <code>DEL</code> key was pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerFavoritesDeleteFilesystemFolder implements ActionListener, KeyListener {
    public ControllerFavoritesDeleteFilesystemFolder() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemDeleteFilesystemFolder().addActionListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        JTree tree = GUI.getFavoritesTree();

        if ((evt.getKeyCode() == KeyEvent.VK_DELETE) &&!tree.isSelectionEmpty()) {
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
                deleteDirectory(
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(PopupMenuFavorites.INSTANCE.getTreePath()));
            }
        });
    }

    private void deleteDirectory(DefaultMutableTreeNode node) {
        File dir = (node == null)
                   ? null
                   : TreeFileSystemDirectories.getFile(node);

        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                TreeFileSystemDirectories.removeFromTreeModel(ModelFactory.INSTANCE.getModel(TreeModelFavorites.class),
                        node);
            }
        }
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
