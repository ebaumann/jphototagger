package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Favoritenverzeichnis entfernen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerDeleteFavoriteDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final PopupMenuTreeFavoriteDirectories popupMenu =
            PopupMenuTreeFavoriteDirectories.INSTANCE;

    public ControllerDeleteFavoriteDirectory() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerDelete(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteFavorite();
    }

    private void deleteFavorite() {
        FavoriteDirectory favorite = popupMenu.getFavoriteDirectory();
        if (confirmDelete(favorite.getFavoriteName())) {
            TreeModelFavoriteDirectories model =
                    (TreeModelFavoriteDirectories) appPanel.
                    getTreeFavoriteDirectories().
                    getModel();
            model.deleteFavorite(favorite);
        }
    }

    private boolean confirmDelete(String favoriteName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString(
                "ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete",
                favoriteName),
                Bundle.getString(
                "ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
