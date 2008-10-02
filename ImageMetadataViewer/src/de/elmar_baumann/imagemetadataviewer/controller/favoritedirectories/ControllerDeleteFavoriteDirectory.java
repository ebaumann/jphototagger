package de.elmar_baumann.imagemetadataviewer.controller.favoritedirectories;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.controller.Controller;
import de.elmar_baumann.imagemetadataviewer.data.FavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.popupmenus.PopupMenuListFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Favoritenverzeichnis entfernen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public class ControllerDeleteFavoriteDirectory extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();
    private PopupMenuListFavoriteDirectories popup = PopupMenuListFavoriteDirectories.getInstance();

    public ControllerDeleteFavoriteDirectory() {
        listenToActionSource();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            deleteFavorite();
        }
    }

    private void deleteFavorite() {
        FavoriteDirectory favorite = popup.getFavoriteDirectory();
        if (confirmDelete(favorite.getFavoriteName())) {
            model.deleteFavorite(favorite);
        }
    }

    private boolean confirmDelete(String favoriteName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete"));
        Object[] params = {favoriteName};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getSmallAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void listenToActionSource() {
        popup.addActionListenerDelete(this);
    }
}
