package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to {@link PopupMenuDirectories#getItemDeleteDirectory()} and
 * deletes a directory when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerDeleteDirectory implements ActionListener {

    PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;

    public ControllerDeleteDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteDirectory();
    }

    private void deleteDirectory() {
        DefaultMutableTreeNode node = TreeFileSystemDirectories.
                getNodeOfLastPathComponent(popup.getTreePath());
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
}
