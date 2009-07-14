package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
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
 * Listens to {@link PopupMenuFavorites#getItemRenameFilesystemFolder()} and
 * renames a directory in the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and renames the selected
 * file system directory if the keys <code>Strg+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerFavoritesRenameFilesystemFolder
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeFavorites();

    public ControllerFavoritesRenameFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemRenameFilesystemFolder().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRename(e) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                renameDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(
                popup.getTreePath()));
    }

    private boolean isRename(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameDirectory(DefaultMutableTreeNode node) {
        File dir = getFile(node);
        if (dir != null) {
            File newDir = FileSystemDirectories.rename(dir);
            if (newDir != null) {
                node.setUserObject(newDir);
                TreeFileSystemDirectories.updateInTreeModel(
                        GUI.INSTANCE.getAppPanel().getTreeFavorites().
                        getModel(), node);
            }
        }
    }

    private File getFile(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof File) {
            return (File) userObject;
        } else if (userObject instanceof FavoriteDirectory) {
            return new File(((FavoriteDirectory) userObject).getDirectoryName());
        }
        return null;
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
