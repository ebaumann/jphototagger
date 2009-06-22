package de.elmar_baumann.imv.controller.favoritedirectories;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.TreeModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuTreeFavoriteDirectories} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/15
 */
public final class ControllerMoveFavoriteDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTree tree = appPanel.getTreeFavoriteDirectories();
    private final PopupMenuTreeFavoriteDirectories popup =
            PopupMenuTreeFavoriteDirectories.INSTANCE;

    public ControllerMoveFavoriteDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuTreeFavoriteDirectories.INSTANCE.addActionListenerInsert(this);
        popup.addActionListenerMoveUp(this);
        popup.addActionListenerMoveDown(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (popup.isMoveUp(source)) {
            moveUp(getFavoriteDirectory());
        } else if (popup.isMoveDown(source)) {
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
