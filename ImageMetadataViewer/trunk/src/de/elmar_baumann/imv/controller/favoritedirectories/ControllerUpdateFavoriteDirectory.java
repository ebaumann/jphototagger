package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link PopupMenuTreeFavoriteDirectories} and renames the
 * the selected favorite directory or set's a different directory when the
 * special menu item was clicked.
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
        final FavoriteDirectory favorite = popupMenu.getFavoriteDirectory();
        FavoriteDirectoryPropertiesDialog dialog =
                new FavoriteDirectoryPropertiesDialog();
        dialog.setFavoriteName(favorite.getFavoriteName());
        dialog.setDirectoryName(favorite.getDirectoryName());
        dialog.setVisible(true);
        if (dialog.accepted()) {
            final String favoriteName = dialog.getFavoriteName();
            final String directoryName = dialog.getDirectoryName();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreeModelFavoriteDirectories model =
                            (TreeModelFavoriteDirectories) appPanel.
                            getTreeFavoriteDirectories().
                            getModel();
                    model.replaceFavorite(favorite, new FavoriteDirectory(
                            favoriteName,
                            directoryName,
                            favorite.getIndex()));
                }
            });
        }
    }
}
