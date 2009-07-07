package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link PopupMenuFavorites} and inserts a
 * new favorite directory when the special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new favorite if
 * the keys <code>Ctrl+I</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class ControllerInsertFavorite
        implements ActionListener, KeyListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeFavorites();
    private final PopupMenuDirectories popupDirectories =
            PopupMenuDirectories.INSTANCE;

    public ControllerInsertFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemInsertFavorite().addActionListener(
                this);
        popupDirectories.getItemAddToFavorites().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_I)) {
            insertFavorite(null);
        }
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
                    TreeModelFavorites model =
                            (TreeModelFavorites) appPanel.getTreeFavorites().
                            getModel();
                    model.insertFavorite(new FavoriteDirectory(
                            dialog.getFavoriteName(), dialog.getDirectoryName(),
                            -1));
                }
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
