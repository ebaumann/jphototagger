package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.app.MessageDisplayer.CancelButton;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the {@link PopupMenuFavorites} and deletes a
 * selected favorite directory when the delete item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and deletes the selected
 * favorite if the <code>DEL</code> key was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class ControllerDeleteFavorite
        implements ActionListener, KeyListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeFavorites();
    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;

    public ControllerDeleteFavorite() {
        listen();
    }

    private void listen() {
        popupMenu.getItemDeleteFavorite().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) node).getUserObject();
                if (userObject instanceof FavoriteDirectory) {
                    deleteFavorite((FavoriteDirectory) userObject);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteFavorite(popupMenu.getFavoriteDirectory());
    }

    private void deleteFavorite(final FavoriteDirectory favoriteDirectory) {
        if (confirmDelete(favoriteDirectory.getFavoriteName())) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    TreeModelFavorites model =
                            (TreeModelFavorites) appPanel.getTreeFavorites().
                            getModel();
                    model.deleteFavorite(favoriteDirectory);
                }
            });
        }
    }

    private boolean confirmDelete(String favoriteName) {
        return MessageDisplayer.confirm(tree,
                "ControllerDeleteFavorite.Confirm.Delete", // NOI18N
                CancelButton.SHOW, favoriteName).equals(
                MessageDisplayer.ConfirmAction.YES);
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
