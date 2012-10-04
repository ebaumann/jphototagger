package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.factory.ModelFactory;

/**
 * Listens to the {@code FavoritesPopupMenu} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author Elmar Baumann
 */
public final class MoveFavoriteController implements ActionListener {

    public MoveFavoriteController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemMoveUp().addActionListener(this);
        FavoritesPopupMenu.INSTANCE.getItemMoveDown().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean moveUp = FavoritesPopupMenu.INSTANCE.getItemMoveUp().equals(evt.getSource());

        EventQueueUtil.invokeInDispatchThread(new MoveDir(moveUp));
    }

    private class MoveDir implements Runnable {

        private boolean up;

        MoveDir(boolean up) {
            this.up = up;
        }

        @Override
        public void run() {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    if (up) {
                        moveUp(getFavoriteDirectory());
                    } else {
                        moveDown(getFavoriteDirectory());
                    }
                }
            });
        }

        private Favorite getFavoriteDirectory() {
            TreePath path = FavoritesPopupMenu.INSTANCE.getTreePath();

            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();

                if (userObject instanceof Favorite) {
                    return (Favorite) userObject;
                }
            }

            return null;
        }

        private void moveUp(Favorite dir) {
            if (dir != null) {
                ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class).moveUpFavorite(dir);
            }
        }

        private void moveDown(Favorite dir) {
            if (dir != null) {
                ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class).moveDownFavorite(dir);
            }
        }
    }
}
