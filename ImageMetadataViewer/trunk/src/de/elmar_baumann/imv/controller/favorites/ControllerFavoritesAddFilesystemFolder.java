package de.elmar_baumann.imv.controller.favorites;

import de.elmar_baumann.imv.model.TreeModelFavorites;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuFavorites;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link PopupMenuFavorites#getItemAddFilesystemFolder()} and
 * creates a directory into the file system when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and inserts a new directory
 * into the selected file system directory if the keys <code>Ctrl+N</code> were
 * pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-30
 */
public final class ControllerFavoritesAddFilesystemFolder
        implements ActionListener, KeyListener {

    private final PopupMenuFavorites popup = PopupMenuFavorites.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeFavorites();

    public ControllerFavoritesAddFilesystemFolder() {
        listen();
    }

    private void listen() {
        popup.getItemAddFilesystemFolder().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode) node;
                createDirectory(new TreePath(pathNode.getPath()));
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createDirectory(popup.getTreePath());
    }

    private void createDirectory(TreePath path) {
        TreeModel model =
                GUI.INSTANCE.getAppPanel().getTreeFavorites().getModel();
        if (model instanceof TreeModelFavorites) {
            ((TreeModelFavorites) model).createNewDirectory(
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(path));
        }
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
