package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuFavorites#getItemDeleteFilesystemFolder()} and
 * deletes a directory in the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and deletes the selected
 * file system directory if the <code>DEL</code> key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-30
 */
public final class ControllerFavoritesDeleteFilesystemFolder
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeFavorites();

    public ControllerFavoritesDeleteFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteFilesystemFolder().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                deleteDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(
                popup.getTreePath()));
    }

    private void deleteDirectory(DefaultMutableTreeNode node) {
        File dir = node == null
                   ? null
                   : TreeFileSystemDirectories.getFile(node);
        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                TreeFileSystemDirectories.removeFromTreeModel(
                        GUI.INSTANCE.getAppPanel().getTreeFavorites().
                        getModel(), node);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
