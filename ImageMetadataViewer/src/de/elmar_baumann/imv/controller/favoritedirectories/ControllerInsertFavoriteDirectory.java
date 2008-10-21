package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuListFavoriteDirectories;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Kontrolliert die Aktion: Favoritenverzeichnis einfügen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public class ControllerInsertFavoriteDirectory extends Controller
    implements ActionListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ListModelFavoriteDirectories model = (ListModelFavoriteDirectories) appPanel.getListFavoriteDirectories().getModel();
    private PopupMenuTreeDirectories popupDirectories = PopupMenuTreeDirectories.getInstance();

    public ControllerInsertFavoriteDirectory() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        PopupMenuListFavoriteDirectories.getInstance().addActionListenerInsert(this);
        popupDirectories.addActionListenerAddToFavoriteDirectories(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl()) {
            String directoryName = null;
            if (popupDirectories.isAddToFavoriteDirectories(e.getSource())) {
                directoryName = popupDirectories.getDirectoryName();
            }
            insertFavorite(directoryName);
        }
    }

    private void insertFavorite(String directoryName) {
        FavoriteDirectoryPropertiesDialog dialog = new FavoriteDirectoryPropertiesDialog();
        if (directoryName != null) {
            dialog.setDirectoryName(directoryName);
            dialog.setEnabledButtonChooseDirectory(false);
        }
        dialog.setVisible(true);
        if (dialog.isOk()) {
            model.insertFavorite(new FavoriteDirectory(
                dialog.getFavoriteName(), dialog.getDirectoryName(), -1));
        }
    }
}
