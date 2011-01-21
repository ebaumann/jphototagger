package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.helper.FavoritesHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;
import org.jphototagger.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to the {@link PopupMenuFavorites} and let's edit the selected
 * favorite directory: Rename or set's a different directory when the
 * special menu item was clicked.
 *
 * Also listens to the {@link JTree}'s key events and let's edit the selected
 * file favorite directory if the keys <code>Strg+E</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerUpdateFavorite extends ControllerFavorite {
    public ControllerUpdateFavorite() {
        listenToActionsOf(PopupMenuFavorites.INSTANCE.getItemUpdateFavorite());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_E);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource()
               == PopupMenuFavorites.INSTANCE.getItemUpdateFavorite();
    }

    @Override
    protected void action(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        FavoritesHelper.updateFavorite(favorite);
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {

        // ignore
    }
}
