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
 * Listens to {@link PopupMenuDirectories#getItemDeleteDirectory()} and
 * deletes a directory when the action fires.
 *
 * Also listens to the directorie's {@link JTree} key events and deletes the
 * selected directory if the delete key was typed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-19
 */
public final class ControllerDeleteDirectory
        implements ActionListener, KeyListener {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    public ControllerDeleteDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteDirectory().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE && tree.getSelectionCount() > 0) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                deleteDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(
                popup.getTreePath()));
    }

    private void deleteDirectory(DefaultMutableTreeNode node) {
        File dir = node == null
                   ? null
                   : TreeFileSystemDirectories.getFile(node);
        if (dir != null) {
            if (FileSystemDirectories.delete(dir)) {
                TreeFileSystemDirectories.removeFromTreeModel(
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
