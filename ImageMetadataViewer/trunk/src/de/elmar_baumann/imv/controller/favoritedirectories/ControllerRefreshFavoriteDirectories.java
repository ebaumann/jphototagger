package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/28
 */
public final class ControllerRefreshFavoriteDirectories implements
        ActionListener {

    private PopupMenuTreeFavoriteDirectories popup =
            PopupMenuTreeFavoriteDirectories.INSTANCE;

    public ControllerRefreshFavoriteDirectories() {
        listen();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (popup.isRefresh(e.getSource())) {
            TreeModelFavoriteDirectories model =
                    (TreeModelFavoriteDirectories) GUI.INSTANCE.getAppPanel().
                    getTreeFavoriteDirectories().getModel();
            model.update();
        }
    }

    private void listen() {
        popup.addActionListenerRefresh(this);
    }
}
