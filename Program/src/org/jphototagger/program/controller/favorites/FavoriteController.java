package org.jphototagger.program.controller.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.FavoritesPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class FavoriteController extends Controller {

    protected abstract void action(Favorite favorite);

    protected abstract void action(DefaultMutableTreeNode node);

    FavoriteController() {
        listenToKeyEventsOf(GUI.getAppPanel().getTreeFavorites());
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        if (GUI.getAppPanel().getTreeFavorites().isSelectionEmpty()) {
            return;
        }

        DefaultMutableTreeNode node = getSelectedNodeFromTree();
        Object o = node.getUserObject();

        if (o instanceof Favorite) {
            action((Favorite) o);
        }

        action(node);
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        Favorite favorite = FavoritesPopupMenu.INSTANCE.getFavorite();

        if (favorite != null) {
            action(favorite);
        }
    }

    protected DefaultMutableTreeNode getSelectedNodeFromTree() {
        JTree tree = GUI.getAppPanel().getTreeFavorites();
        Object node = tree.getSelectionPath().getLastPathComponent();

        if (node instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) node;
        }

        return null;
    }
}
