package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.componentutil.TreeUtil;
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
 * @version 2008/09/23
 */
public final class MouseListenerFavorites extends MouseAdapter {

    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        int x = e.getX();
        int y = e.getY();
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isItemSelected = TreeUtil.isSelectedItemPosition(e);
            boolean isFavoriteItemSelected = false;
            boolean isFileDirectory = false;
            if (isItemSelected) {
                Object o = tree.getSelectionPath().getLastPathComponent();
                TreePath path = tree.getPathForLocation(x, y);
                popupMenu.setTreePath(path);
                if (path != null && o.equals(path.getLastPathComponent()) &&
                        o instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                    Object userObject = node.getUserObject();
                    DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode) node.getParent();
                    TreeNode root = (TreeNode) tree.getModel().getRoot();
                    isFileDirectory = userObject instanceof File;
                    if (root.equals(parent) &&
                            userObject instanceof FavoriteDirectory) {
                        isFavoriteItemSelected = true;
                        popupMenu.setFavoriteDirectory(
                                (FavoriteDirectory) userObject);
                    }
                }
            }
            popupMenu.getItemDeleteFavorite().setEnabled(isFavoriteItemSelected);
            popupMenu.getItemUpdateFavorite().setEnabled(isFavoriteItemSelected);
            popupMenu.getItemMoveUp().setEnabled(isFavoriteItemSelected);
            popupMenu.getItemMoveDown().setEnabled(isFavoriteItemSelected);
            popupMenu.getItemOpenInFolders().setEnabled(isItemSelected);
            popupMenu.getItemAddFilesystemFolder().setEnabled(
                    isFavoriteItemSelected || isFileDirectory);
            popupMenu.getItemRenameFilesystemFolder().setEnabled(isFileDirectory);
            popupMenu.getItemDeleteFilesystemFolder().setEnabled(isFileDirectory);
            popupMenu.show(tree, x, y);
        }
    }
}
