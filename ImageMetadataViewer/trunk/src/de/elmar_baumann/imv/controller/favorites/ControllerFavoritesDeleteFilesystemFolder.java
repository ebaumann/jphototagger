package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuFavorites#getItemDeleteFilesystemFolder()} and
 * deletes a directory in the file system when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/30
 */
public final class ControllerFavoritesDeleteFilesystemFolder implements ActionListener {

    PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerFavoritesDeleteFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteFilesystemFolder().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteDirectory();
    }

    private void deleteDirectory() {
        DefaultMutableTreeNode node = TreeFileSystemDirectories.
                getNodeOfLastPathComponent(popup.getTreePath());
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
}
