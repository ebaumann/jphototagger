package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.TreeModel;

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
        TreeModel model =
                GUI.INSTANCE.getAppPanel().getTreeFavorites().getModel();
        if (model instanceof TreeModelFavorites) {
            ((TreeModelFavorites) model).createNewDirectory(
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(
                    popup.getTreePath()));
        }
    }
}
