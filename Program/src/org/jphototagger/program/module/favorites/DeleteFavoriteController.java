package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.domain.favorites.Favorite;

/**
 * Listens to the {@code FavoritesPopupMenu} and deletes a
 * selected favorite directory when the delete item was clicked.
 *
 * Also listens to the {@code JTree}'s key events and deletes the selected
 * favorite if the <code>DEL</code> key was pressed.
 *
 * @author Elmar Baumann
 */
public final class DeleteFavoriteController extends FavoriteController {

    public DeleteFavoriteController() {
        listenToActionsOf(FavoritesPopupMenu.INSTANCE.getItemDeleteFavorite());
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        return evt.getKeyCode() == KeyEvent.VK_DELETE;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        return evt.getSource() == FavoritesPopupMenu.INSTANCE.getItemDeleteFavorite();
    }

    @Override
    protected void action(Favorite favorite) {
        FavoritesUtil.deleteFavorite(favorite);
    }

    @Override
    protected void action(DefaultMutableTreeNode node) {
        // ignore
    }
}
