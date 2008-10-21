package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
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
        popup.addActionLitenerMoveUp(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            model.moveUpFavorite(popup.getFavoriteDirectory());
        }
    }
}
