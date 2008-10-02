package de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories;

import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuListFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Favoriten-Item nach oben verschieben.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerMoveFavoriteItemUp extends Controller
    implements ActionListener {

    PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();

    public ControllerMoveFavoriteItemUp() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionLitenerMoveUp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            moveItemUp();
        }
    }

    private void moveItemUp() {
        model.moveUpFavorite(popup.getFavoriteDirectory());
    }
}
