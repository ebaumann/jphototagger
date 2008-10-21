package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Favoriten-Item nach unten verschieben.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class ControllerMoveFavoriteItemDown extends Controller
    implements ActionListener {

    private PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();
    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();

    public ControllerMoveFavoriteItemDown() {
        listenToActionSource();
    }

    private void listenToActionSource() {
        popup.addActionLitenerMoveDown(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            moveItemDown();
        }
    }

    private void moveItemDown() {
        model.moveDownFavorite(popup.getFavoriteDirectory());
    }
}
