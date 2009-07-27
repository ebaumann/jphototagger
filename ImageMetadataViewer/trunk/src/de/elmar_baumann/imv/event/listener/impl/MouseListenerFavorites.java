package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Behandelt Mauskereignisse in der Liste für Favoritenverzeichnisse.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class MouseListenerFavorites extends MouseAdapter {

    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            TreePath path = TreeUtil.getTreePath(e);
            boolean isFavorite = false;
            boolean isDirectory = false;
            popupMenu.setTreePath(path);
            if (path != null) {
                Object usrObj = path.getLastPathComponent();
                if (usrObj instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) usrObj;
                    Object userObject = node.getUserObject();
                    DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode) node.getParent();
                    TreeNode root = (TreeNode) ((JTree) e.getSource()).getModel().
                            getRoot();
                    isDirectory = userObject instanceof File;
                    if (root.equals(parent) &&
                            userObject instanceof FavoriteDirectory) {
                        isFavorite = true;
                        popupMenu.setFavoriteDirectory(
                                (FavoriteDirectory) userObject);
                    }
                }
            }
            popupMenu.getItemDeleteFavorite().setEnabled(isFavorite);
            popupMenu.getItemUpdateFavorite().setEnabled(isFavorite);
            popupMenu.getItemMoveUp().setEnabled(isFavorite);
            popupMenu.getItemMoveDown().setEnabled(isFavorite);
            popupMenu.getItemOpenInFolders().setEnabled(
                    isFavorite || isDirectory);
            popupMenu.getItemAddFilesystemFolder().setEnabled(
                    isFavorite || isDirectory);
            popupMenu.getItemRenameFilesystemFolder().setEnabled(isDirectory);
            popupMenu.getItemDeleteFilesystemFolder().setEnabled(isDirectory);
            popupMenu.show((JTree) e.getSource(), e.getX(), e.getY());
        }
    }
}
