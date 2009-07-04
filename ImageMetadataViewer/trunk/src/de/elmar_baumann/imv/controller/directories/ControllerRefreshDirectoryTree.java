package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;

/**
 * Listens to {@link PopupMenuTreeDirectories#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/30
 */
public final class ControllerRefreshDirectoryTree
        implements ActionListener, KeyListener {

    PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    JTree tree = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    public ControllerRefreshDirectoryTree() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            refresh();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        refresh();
    }

    private void refresh() {
        TreeModelAllSystemDirectories model =
                (TreeModelAllSystemDirectories) GUI.INSTANCE.getAppPanel().
                getTreeDirectories().getModel();
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
