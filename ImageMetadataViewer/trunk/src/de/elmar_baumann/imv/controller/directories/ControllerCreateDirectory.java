package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to {@link PopupMenuDirectories#getItemCreateDirectory()} and
 * creates a directory when the action fires.
 *
 * Also listens to the {@link JTree}'s key events and creates a new directory
 * into the selected directory when the keys <code>Ctrl+N</code> was pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-19
 */
public final class ControllerCreateDirectory
        implements ActionListener, KeyListener {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    public ControllerCreateDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemCreateDirectory().addActionListener(this);
        tree.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isControl(e, KeyEvent.VK_N) && !tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                createDirectory((DefaultMutableTreeNode) node);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createDirectory(TreeFileSystemDirectories.getNodeOfLastPathComponent(
                popup.getTreePath()));
    }

    private void createDirectory(DefaultMutableTreeNode node) {
        TreeModel model =
                GUI.INSTANCE.getAppPanel().getTreeDirectories().getModel();
        if (model instanceof TreeModelAllSystemDirectories) {
            ((TreeModelAllSystemDirectories) model).createNewDirectory(node);
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
