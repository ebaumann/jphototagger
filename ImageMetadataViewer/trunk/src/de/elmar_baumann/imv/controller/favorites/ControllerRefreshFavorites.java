package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;

/**
 * Refreshes the favorite directories tree: Adds new folders and removes
 * deleted.
 *
 * Also listens to the {@link JTree}'s key events and refreshes the view if
 * the key <code>F5</code> was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-28
 */
public final class ControllerRefreshFavorites
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;

    public ControllerRefreshFavorites() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
        GUI.INSTANCE.getAppPanel().getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (popup.getItemRefresh().equals(e.getSource())) {
            refresh();
        }
    }

    private void refresh() {
        TreeModelFavorites model =
                (TreeModelFavorites) GUI.INSTANCE.getAppPanel().
                getTreeFavorites().getModel();
        model.update();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
