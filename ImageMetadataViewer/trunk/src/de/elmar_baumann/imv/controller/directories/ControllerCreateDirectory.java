package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuDirectories;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.tree.TreeModel;

/**
 * Listens to {@link PopupMenuDirectories#getItemCreateDirectory()} and
 * creates a directory when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerCreateDirectory implements ActionListener {

    PopupMenuDirectories popup = PopupMenuDirectories.INSTANCE;

    public ControllerCreateDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemCreateDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        createDirectory();
    }

    private void createDirectory() {
        TreeModel model =
                GUI.INSTANCE.getAppPanel().getTreeDirectories().getModel();
        if (model instanceof TreeModelAllSystemDirectories) {
            ((TreeModelAllSystemDirectories) model).createNewDirectory(
                    TreeFileSystemDirectories.getNodeOfLastPathComponent(
                    popup.getTreePath()));
        }
    }
}
