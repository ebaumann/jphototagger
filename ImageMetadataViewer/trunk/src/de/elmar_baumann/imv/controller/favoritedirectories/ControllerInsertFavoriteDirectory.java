package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to the {@link PopupMenuTreeFavoriteDirectories} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerInsertFavoriteDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final PopupMenuTreeDirectories popupDirectories =
            PopupMenuTreeDirectories.INSTANCE;

    public ControllerInsertFavoriteDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuTreeFavoriteDirectories.INSTANCE.addActionListenerInsert(this);
        popupDirectories.addActionListenerAddToFavoriteDirectories(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        insertFavorite(getDirectoryName(e.getSource()));
    }

    private String getDirectoryName(Object o) {
        String directoryName = null;
        if (popupDirectories.isAddToFavoriteDirectoriesItem(o)) {
            directoryName = popupDirectories.getDirectoryName();
        }
        return directoryName;
    }

    private void insertFavorite(String directoryName) {
        FavoriteDirectoryPropertiesDialog dialog =
                new FavoriteDirectoryPropertiesDialog();
        if (directoryName != null) {
            dialog.setDirectoryName(directoryName);
            dialog.setEnabledButtonChooseDirectory(false);
        }
        dialog.setVisible(true);
        if (dialog.accepted()) {
            TreeModelFavoriteDirectories model =
                    (TreeModelFavoriteDirectories) appPanel.
                    getTreeFavoriteDirectories().
                    getModel();
            model.insertFavorite(new FavoriteDirectory(
                    dialog.getFavoriteName(), dialog.getDirectoryName(), -1));
        }
    }
}
