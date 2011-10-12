package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.swing.KeyEventUtil;

/**
 * Listens to the {@code FavoritesPopupMenu} and let's edit the selected
 * favorite directory: Rename or set's a different directory when the
 * special menu item was clicked.
 *
 * Also listens to the {@code JTree}'s key events and let's edit the selected
 * file favorite directory if the keys <code>Strg+E</code> were pressed.
 *
 * @author Elmar Baumann
 */
public final class UpdateFavoriteController extends FavoriteController {

    public UpdateFavoriteController() {
        listenToActionsOf(FavoritesPopupMenu.INSTANCE.getItemUpdateFavorite());
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

        return evt.getSource() == FavoritesPopupMenu.INSTANCE.getItemUpdateFavorite();
    }

    @Override
    protected void action(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        FavoritesUtil.updateFavorite(favorite);
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        // ignore
    }
}
