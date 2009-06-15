package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Favoritenverzeichnis aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerUpdateFavoriteDirectory implements ActionListener {

    private final PopupMenuTreeFavoriteDirectories popupMenu =
            PopupMenuTreeFavoriteDirectories.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();

    public ControllerUpdateFavoriteDirectory() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerUpdate(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateFavorite();
    }

    private void updateFavorite() {
        FavoriteDirectory favorite = popupMenu.getFavoriteDirectory();
        FavoriteDirectoryPropertiesDialog dialog =
                new FavoriteDirectoryPropertiesDialog();
        dialog.setFavoriteName(favorite.getFavoriteName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            TreeModelFavoriteDirectories model =
                    (TreeModelFavoriteDirectories) appPanel.
                    getTreeFavoriteDirectories().
                    getModel();
            model.replaceFavorite(favorite, new FavoriteDirectory(
                    dialog.getFavoriteName(),
                    dialog.getDirectoryName(),
                    favorite.getIndex()));
        }
    }
}
