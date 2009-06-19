package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeFavoriteDirectories;
import de.elmar_baumann.lib.componentutil.TreeUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public final class TreeFavoriteDirectoriesMouseListener extends MouseAdapter {

    private final PopupMenuTreeFavoriteDirectories popupMenu =
            PopupMenuTreeFavoriteDirectories.INSTANCE;

    @Override
    public void mousePressed(MouseEvent e) {
        JTree tree = (JTree) e.getSource();
        int x = e.getX();
        int y = e.getY();
        if ((e.isPopupTrigger() || e.getModifiers() == 4)) {
            boolean isItemSelected = TreeUtil.isSelectedItemPosition(e);
            boolean isFavoriteItemSelected = false;
            if (isItemSelected) {
                Object o = tree.getSelectionPath().getLastPathComponent();
                TreePath path = tree.getPathForLocation(x, y);
                if (path != null && o.equals(path.getLastPathComponent()) &&
                        o instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
                    Object userObject = node.getUserObject();
                    DefaultMutableTreeNode parent =
                            (DefaultMutableTreeNode) node.getParent();
                    TreeNode root = (TreeNode) tree.getModel().getRoot();
                    if (root.equals(parent) &&
                            userObject instanceof FavoriteDirectory) {
                        isFavoriteItemSelected = true;
                        popupMenu.setFavoriteDirectory(
                                (FavoriteDirectory) userObject);
                    }
                }
            }
            popupMenu.setEnabledDelete(isFavoriteItemSelected);
            popupMenu.setEnabledUpdate(isFavoriteItemSelected);
            popupMenu.setEnabledMoveUp(isFavoriteItemSelected);
            popupMenu.setEnabledMoveDown(isFavoriteItemSelected);
            popupMenu.setEnabledOpenInFolders(isItemSelected);
            popupMenu.show(tree, x, y);
        }
    }
}
