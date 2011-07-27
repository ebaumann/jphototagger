package org.jphototagger.program.controller.favorites;

import org.jphototagger.domain.Favorite;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to the {@link PopupMenuFavorites} and moves in the list
 * up or down the selected favorite directory when the special menu item was
 * clicked.
 *
 * @author Elmar Baumann
 */
public final class ControllerMoveFavorite implements ActionListener {
    public ControllerMoveFavorite() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemMoveUp().addActionListener(this);
        PopupMenuFavorites.INSTANCE.getItemMoveDown().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean moveUp = PopupMenuFavorites.INSTANCE.getItemMoveUp().equals(evt.getSource());

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
            TreePath path = PopupMenuFavorites.INSTANCE.getTreePath();

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
                ModelFactory.INSTANCE.getModel(TreeModelFavorites.class).moveUpFavorite(dir);
            }
        }

        private void moveDown(Favorite dir) {
            if (dir != null) {
                ModelFactory.INSTANCE.getModel(TreeModelFavorites.class).moveDownFavorite(dir);
            }
        }
    }
}
