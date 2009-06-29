package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.io.FileSystemDirectories;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Listens to {@link PopupMenuTreeDirectories#getItemCreateDirectory()} and
 * creates a directory when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerCreateDirectory implements ActionListener {

    PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.INSTANCE;

    public ControllerCreateDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemCreateDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String directoryName = popup.getDirectoryName();
        if (directoryName != null) {
            FileSystemDirectories.createSubDirectory(new File(directoryName));
        }
    }
}
