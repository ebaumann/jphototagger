package org.jphototagger.program.controller.favorites;

import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelFavorites;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuFavorites;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTree;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the view if
 * the key <code>F5</code> was pressed.
 *
 * @author Elmar Baumann
 */
public final class ControllerRefreshFavorites implements ActionListener, KeyListener {
    public ControllerRefreshFavorites() {
        listen();
    }

    private void listen() {
        PopupMenuFavorites.INSTANCE.getItemRefresh().addActionListener(this);
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
        if (PopupMenuFavorites.INSTANCE.getItemRefresh().equals(evt.getSource())) {
            refresh();
        }
    }

    public void refresh() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                ModelFactory.INSTANCE.getModel(TreeModelFavorites.class).update();
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
