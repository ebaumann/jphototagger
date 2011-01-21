package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 *
 * @author Elmar Baumann
 */
public abstract class ControllerFavorite extends Controller {
    protected abstract void action(Favorite favorite);

    protected abstract void action(DefaultMutableTreeNode node);

    ControllerFavorite() {
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
        Object                 o    = node.getUserObject();

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

        Favorite favorite = PopupMenuFavorites.INSTANCE.getFavorite();

        if (favorite != null) {
            action(favorite);
        }
    }

    protected DefaultMutableTreeNode getSelectedNodeFromTree() {
        JTree  tree = GUI.getAppPanel().getTreeFavorites();
        Object node = tree.getSelectionPath().getLastPathComponent();

        if (node instanceof DefaultMutableTreeNode) {
            return (DefaultMutableTreeNode) node;
        }

        return null;
    }
}
