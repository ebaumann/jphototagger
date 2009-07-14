package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemRenameDirectory()} and
 * renames a directory when the action fires.
 *
 * Also listenes to the {@link JTree}'s key events and renames the selected
 * directory when the keys <code>Ctrl+R</code> or <code>F2</code> were pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerRenameDirectory
        implements ActionListener, KeyListener {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    public ControllerRenameDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemRenameDirectory().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRename(e) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                renameDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(
                popup.getTreePath()));
    }

    private boolean isRename(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameDirectory(DefaultMutableTreeNode node) {
        File dir = node == null
                   ? null
                   : TreeFileSystemDirectories.getFile(node);
        if (dir != null) {
            File newDir = FileSystemDirectories.rename(dir);
            if (newDir != null) {
                node.setUserObject(newDir);
                TreeFileSystemDirectories.updateInTreeModel(
                        GUI.INSTANCE.getAppPanel().getTreeDirectories().getModel(),
                        node);
            }
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
