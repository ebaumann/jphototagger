package org.jphototagger.program.event.listener.impl;

import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;
import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;

import java.io.File;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Do not use this class as template for implemention! Instead extend
 * {@link org.jphototagger.lib.event.listener.PopupMenuTree} as e.g.
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuMiscMetadata} does.
 *
 * @author Elmar Baumann
 */
public final class MouseListenerFavorites extends MouseListenerTree {
    private final PopupMenuFavorites popupMenu = PopupMenuFavorites.INSTANCE;

    public MouseListenerFavorites() {
        listenExpandAllSubItems(popupMenu.getItemExpandAllSubitems(), true);
        listenCollapseAllSubItems(popupMenu.getItemCollapseAllSubitems(), true);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);

        if (MouseEventUtil.isPopupTrigger(evt)) {
            TreePath path        = TreeUtil.getTreePath(evt);
            boolean  isFavorite  = false;
            boolean  isDirectory = false;

            popupMenu.setTreePath(path);

            if (path != null) {
                Object usrObj = path.getLastPathComponent();

                if (usrObj instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) usrObj;
                    Object                 userObject = node.getUserObject();
                    DefaultMutableTreeNode parent     =
                        (DefaultMutableTreeNode) node.getParent();
                    TreeNode root =
                        (TreeNode) ((JTree) evt.getSource()).getModel().getRoot();

                    isDirectory = userObject instanceof File;

                    if (root.equals(parent)
                            && (userObject instanceof Favorite)) {
                        isFavorite = true;
                        popupMenu.setFavoriteDirectory((Favorite) userObject);
                    }
                }
            }

            popupMenu.getItemDeleteFavorite().setEnabled(isFavorite);
            popupMenu.getItemUpdateFavorite().setEnabled(isFavorite);
            popupMenu.getItemMoveUp().setEnabled(isFavorite);
            popupMenu.getItemMoveDown().setEnabled(isFavorite);
            popupMenu.getItemOpenInFolders().setEnabled(isFavorite
                    || isDirectory);
            popupMenu.getItemAddFilesystemFolder().setEnabled(isFavorite
                    || isDirectory);
            popupMenu.getItemRenameFilesystemFolder().setEnabled(isDirectory);
            popupMenu.getItemDeleteFilesystemFolder().setEnabled(isDirectory);
            popupMenu.show((JTree) evt.getSource(), evt.getX(), evt.getY());
        }
    }
}
