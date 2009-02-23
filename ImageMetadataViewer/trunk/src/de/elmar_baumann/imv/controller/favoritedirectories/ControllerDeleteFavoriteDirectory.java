package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import de.elmar_baumann.lib.io.FileUtil;
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
public final class ControllerDeleteFavoriteDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();
    private final PopupMenuListFavoriteDirectories popupMenu = PopupMenuListFavoriteDirectories.INSTANCE;
    private CheckDirectoriesRemoved removeChecker;
    private static final int removeCheckIntervalSeconds = 3;

    public ControllerDeleteFavoriteDirectory() {
        listen();
        startRemoveChecker();
    }

    private void listen() {
        popupMenu.addActionListenerDelete(this);
    }

    private void startRemoveChecker() {
        removeChecker = new CheckDirectoriesRemoved();
        removeChecker.setPriority(1);
        removeChecker.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteFavorite();
    }

    private void deleteFavorite() {
        FavoriteDirectory favorite = popupMenu.getFavoriteDirectory();
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
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        removeChecker.stopCheck();
    }

    private class CheckDirectoriesRemoved extends Thread {

        private boolean stop = false;

        public synchronized void stopCheck() {
            stop = true;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    Thread.sleep(removeCheckIntervalSeconds * 1000);
                } catch (InterruptedException ex) {
                    de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
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
