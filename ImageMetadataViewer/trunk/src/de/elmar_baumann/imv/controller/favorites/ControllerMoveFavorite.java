package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuFavorites} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/15
 */
public final class ControllerMoveFavorite implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeFavoriteDirectories();
    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerMoveFavorite() {
        listen();
    }

    private void listen() {
        popup.getItemMoveUp().addActionListener(this);
        popup.getItemMoveDown().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean moveUp = popup.getItemMoveUp().equals(e.getSource());
        SwingUtilities.invokeLater(new MoveDir(moveUp));
    }

    private class MoveDir implements Runnable {

        private boolean up;

        public MoveDir(boolean up) {
            this.up = up;
        }

        @Override
        public void run() {
            if (up) {
                moveUp(getFavoriteDirectory());
            } else {
                moveDown(getFavoriteDirectory());
            }
        }

        private FavoriteDirectory getFavoriteDirectory() {
            TreePath selPath = tree.getSelectionPath();
            if (selPath != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.
                        getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof FavoriteDirectory) {
                    return (FavoriteDirectory) userObject;
                }
            }
            return null;
        }

        private void moveUp(FavoriteDirectory dir) {
            if (dir != null) {
                TreeModelFavoriteDirectories model =
                        (TreeModelFavoriteDirectories) tree.getModel();
                model.moveUpFavorite(dir);
            }
        }

        private void moveDown(FavoriteDirectory dir) {
            if (dir != null) {
                TreeModelFavoriteDirectories model =
                        (TreeModelFavoriteDirectories) tree.getModel();
                model.moveDownFavorite(dir);
            }
        }
    }
}
