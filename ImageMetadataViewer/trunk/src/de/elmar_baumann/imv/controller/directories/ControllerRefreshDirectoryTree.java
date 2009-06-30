package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import de.elmar_baumann.lib.model.TreeModelAllSystemDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listens to {@link PopupMenuTreeDirectories#getItemRefresh()} and
 * refreshes the directory tree when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/30
 */
public final class ControllerRefreshDirectoryTree implements ActionListener {

    PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.INSTANCE;

    public ControllerRefreshDirectoryTree() {
        listen();
    }

    private void listen() {
        popup.getItemRefresh().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreeModelAllSystemDirectories model =
                (TreeModelAllSystemDirectories) GUI.INSTANCE.getAppPanel().
                getTreeDirectories().getModel();
        model.update();
    }
}
