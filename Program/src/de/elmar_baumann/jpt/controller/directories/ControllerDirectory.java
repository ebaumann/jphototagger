package de.elmar_baumann.jpt.controller.directories;

import de.elmar_baumann.jpt.controller.Controller;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Base class for directory controllers.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-19
 */
public abstract class ControllerDirectory extends Controller {

    private final PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;
    private final JTree                tree  = GUI.INSTANCE.getAppPanel().getTreeDirectories();

    protected abstract void action(DefaultMutableTreeNode node);

    public ControllerDirectory() {
        listenToKeyEventsOf(tree);
    }

    @Override
    protected void action(ActionEvent evt) {
        DefaultMutableTreeNode node = 
                TreeFileSystemDirectories.getNodeOfLastPathComponent(popup.getTreePath());
        if (node != null) {
            action(node);
        }
    }

    @Override
    protected void action(KeyEvent evt) {
        if (!tree.isSelectionEmpty()) {
            Object node = tree.getSelectionPath().getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                action((DefaultMutableTreeNode) node);
            }
        }
    }

    protected File getDirOfNode(DefaultMutableTreeNode node) {
        File dir = TreeFileSystemDirectories.getFile(node);
        if (dir != null && dir.isDirectory()) {
            return dir;
        }
        return null;
    }
}
