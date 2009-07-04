package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the {@link PopupMenuFavorites} and opens the
 * selected favorite directory in the folder panel when the special menu item
 * was clicked.
 *
 * Also listens to the {@link JTree}'s key events and opens the selected folder
 * in the directorie's tree if the keys <code>Ctrl+O</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/05
 */
public final class ControllerOpenFavoriteInFolders
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popupMenu =
            PopupMenuFavorites.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JTabbedPane tabbedPaneSelection =
            appPanel.getTabbedPaneSelection();
    private final Component tabTreeDirectories =
            appPanel.getTabSelectionDirectories();
    private final JTree treeDirectories = appPanel.getTreeDirectories();
    private final JTree treeFavoriteDirectories =
            appPanel.getTreeFavorites();

    public ControllerOpenFavoriteInFolders() {
        listen();
    }

    private void listen() {
        popupMenu.getItemOpenInFolders().addActionListener(this);
        treeFavoriteDirectories.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_O) &&
                !treeFavoriteDirectories.isSelectionEmpty()) {
            selectDirectory();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (treeFavoriteDirectories.getSelectionCount() >= 0) {
            selectDirectory();
        }
    }

    private void selectDirectory() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                TreePath selPath = treeFavoriteDirectories.getSelectionPath();
                if (selPath != null) {
                    File dir = null;
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) selPath.
                            getLastPathComponent();
                    Object userObject = node.getUserObject();
                    if (userObject instanceof File) {
                        dir = (File) userObject;
                    } else if (userObject instanceof FavoriteDirectory) {
                        FavoriteDirectory favoriteDirectory =
                                (FavoriteDirectory) userObject;
                        dir = new File(favoriteDirectory.getDirectoryName());
                    }
                    if (dir != null && dir.isDirectory()) {
                        treeFavoriteDirectories.clearSelection();
                        tabbedPaneSelection.setSelectedComponent(
                                tabTreeDirectories);
                        TreeModel m = treeDirectories.getModel();
                        if (m instanceof TreeModelAllSystemDirectories) {
                            ((TreeModelAllSystemDirectories) m).expandToFile(dir,
                                    true);

                        }
                    }
                }
            }
        });
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
