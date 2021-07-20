package org.jphototagger.program.module.favorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.resource.GUI;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * Also listens to the {@code JTree}'s key events and refreshes the view if
 * the key <code>F5</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class RefreshFavoritesController implements ActionListener, KeyListener {

    public RefreshFavoritesController() {
        listen();
    }

    private void listen() {
        FavoritesPopupMenu.INSTANCE.getItemRefresh().addActionListener(this);
        GUI.getAppPanel().getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (FavoritesPopupMenu.INSTANCE.getItemRefresh().equals(evt.getSource())) {
            refresh();
        }
    }

    public void refresh() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                ModelFactory.INSTANCE.getModel(FavoritesTreeModel.class).update();
            }
        });
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
