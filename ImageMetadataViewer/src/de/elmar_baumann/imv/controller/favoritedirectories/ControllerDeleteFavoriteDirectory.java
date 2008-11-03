package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private CheckDirectoriesRemoved removeChecker;
    private static final int removeCheckIntervalSeconds = 3;

    public ControllerDeleteFavoriteDirectory() {
        popup.addActionListenerDelete(this);
        checkForRemoves();
    }

    private void checkForRemoves() {
        removeChecker = new CheckDirectoriesRemoved();
        removeChecker.setPriority(1);
        removeChecker.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            deleteFavorite();
        }
    }

    private void deleteFavorite() {
        FavoriteDirectory favorite = popup.getFavoriteDirectory();
        if (deleteConfirmed(favorite.getFavoriteName())) {
            model.deleteFavorite(favorite);
        }
    }

    private boolean deleteConfirmed(String favoriteName) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete"));
        Object[] params = {favoriteName};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("ControllerDeleteFavoriteDirectory.ConfirmMessage.Delete.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        removeChecker.setStop(true);
    }

    private class CheckDirectoriesRemoved extends Thread {

        private boolean stop = false;

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    Thread.sleep(removeCheckIntervalSeconds * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ControllerDeleteFavoriteDirectory.class.getName()).log(Level.SEVERE, null, ex);
                }
                int size = model.getSize();
                for (int i = 0; i < size; i++) {
                    if (!FileUtil.existsDirectory(((FavoriteDirectory) model.get(i)).getDirectoryName())) {
                        model.deleteFavorite((FavoriteDirectory) model.get(i));
                    }
                }
            }
        }
    }
}
