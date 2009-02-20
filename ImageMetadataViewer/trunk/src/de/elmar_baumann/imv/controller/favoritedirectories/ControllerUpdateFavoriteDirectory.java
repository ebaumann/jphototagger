package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Favoritenverzeichnis aktualisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerUpdateFavoriteDirectory implements ActionListener {

    private final PopupMenuListFavoriteDirectories popupMenu = PopupMenuListFavoriteDirectories.getInstance();
    private final AppPanel appPanel = Panels.getInstance().getAppPanel();
    private final ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();

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
        FavoriteDirectoryPropertiesDialog dialog = new FavoriteDirectoryPropertiesDialog();
        dialog.setFavoriteName(favorite.getFavoriteName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            model.replaceFavorite(favorite, new FavoriteDirectory(
                    dialog.getFavoriteName(),
                    dialog.getDirectoryName(),
                    favorite.getIndex()));
        }
    }
}
