package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
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
public class ControllerUpdateFavoriteDirectory extends Controller
    implements ActionListener {

    private PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();

    public ControllerUpdateFavoriteDirectory() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionListenerUpdate(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            updateFavorite();
        }
    }

    private void updateFavorite() {
        FavoriteDirectory favorite = popup.getFavoriteDirectory();
        FavoriteDirectoryPropertiesDialog dialog = new FavoriteDirectoryPropertiesDialog();
        dialog.setFavoriteName(favorite.getFavoriteName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.isAccepted()) {
            model.replaceFavorite(favorite, new FavoriteDirectory(
                dialog.getFavoriteName(),
                dialog.getDirectoryName(),
                favorite.getIndex()));
        }
    }
}
