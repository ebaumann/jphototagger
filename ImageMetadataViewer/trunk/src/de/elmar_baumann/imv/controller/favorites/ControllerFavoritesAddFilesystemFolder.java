package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuFavorites#getItemAddFilesystemFolder()} and
 * creates a directory into the file system when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/30
 */
public final class ControllerFavoritesAddFilesystemFolder implements
        ActionListener {

    PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerFavoritesAddFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemAddFilesystemFolder().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createDirectory();
    }

    private void createDirectory() {
        DefaultMutableTreeNode node = FileSystemDirectories.
                getNodeOfLastPathComponent(popup.getTreePath());
        File dir = node == null
                   ? null
                   : FileSystemDirectories.getFile(node);
        if (dir != null) {
            File newDir = FileSystemDirectories.createSubDirectory(dir);
            if (newDir != null) {
                FileSystemDirectories.insertIntoTreeModel(
                        GUI.INSTANCE.getAppPanel().getTreeFavorites().
                        getModel(), node, newDir);
            }
        }
    }
}
