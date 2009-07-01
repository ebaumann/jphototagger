package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link PopupMenuFavorites} and deletes a
 * selected favorite directory when the delete item was clicked.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerDeleteFavorite implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;

    public ControllerDeleteFavorite() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDeleteFavorite().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteFavorite();
    }

    private void deleteFavorite() {
        final FavoriteDirectory favoriteDirectory = popupMenu.
                getFavoriteDirectory();
        if (confirmDelete(favoriteDirectory.getFavoriteName())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreeModelFavorites model =
                            (TreeModelFavorites) appPanel.
                            getTreeFavorites().
                            getModel();
                    model.deleteFavorite(favoriteDirectory);
                }
            });
        }
    }

    private boolean confirmDelete(String favoriteName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString(
                "ControllerDeleteFavorite.ConfirmMessage.Delete",
                favoriteName),
                Bundle.getString(
                "ControllerDeleteFavorite.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
