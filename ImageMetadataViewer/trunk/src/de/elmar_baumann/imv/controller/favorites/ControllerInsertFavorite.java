package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link PopupMenuFavorites} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerInsertFavorite implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final PopupMenuDirectories popupDirectories =
            PopupMenuDirectories.INSTANCE;

    public ControllerInsertFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemInsertFavorite().addActionListener(
                this);
        popupDirectories.getItemAddToFavorites().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        insertFavorite(getDirectoryName(e.getSource()));
    }

    private String getDirectoryName(Object o) {
        String directoryName = null;
        boolean isAddToFavorites =
                popupDirectories.getItemAddToFavorites().equals(o);
        if (isAddToFavorites) {
            directoryName = popupDirectories.getDirectoryName();
        }
        return directoryName;
    }

    private void insertFavorite(final String directoryName) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FavoriteDirectoryPropertiesDialog dialog =
                        new FavoriteDirectoryPropertiesDialog();
                if (directoryName != null) {
                    dialog.setDirectoryName(directoryName);
                    dialog.setEnabledButtonChooseDirectory(false);
                }
                dialog.setVisible(true);
                if (dialog.accepted()) {
                    TreeModelFavoriteDirectories model =
                            (TreeModelFavoriteDirectories) appPanel.
                            getTreeFavoriteDirectories().
                            getModel();
                    model.insertFavorite(new FavoriteDirectory(
                            dialog.getFavoriteName(), dialog.getDirectoryName(),
                            -1));
                }
            }
        });
    }
}
