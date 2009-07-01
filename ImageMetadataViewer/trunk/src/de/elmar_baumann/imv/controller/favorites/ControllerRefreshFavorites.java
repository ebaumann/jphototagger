package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/28
 */
public final class ControllerRefreshFavorites implements
        ActionListener {

    private PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerRefreshFavorites() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (popup.getItemRefresh().equals(e.getSource())) {
            TreeModelFavorites model =
                    (TreeModelFavorites) GUI.INSTANCE.getAppPanel().
                    getTreeFavorites().getModel();
            model.update();
        }
    }
}
