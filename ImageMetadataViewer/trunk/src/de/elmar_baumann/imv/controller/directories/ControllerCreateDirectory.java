package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;

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
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                String subdirectoryName = getSubDirectoryName();
                if (subdirectoryName != null &&
                        !subdirectoryName.trim().isEmpty()) {
                    File subdirectory = new File(directory, subdirectoryName);
                    if (checkDoesNotExist(subdirectory)) {
                        try {
                            subdirectory.mkdir();
                        } catch (Exception ex) {
                            AppLog.logWarning(ControllerCreateDirectory.class,
                                    ex);
                        }
                    }
                }
            }

        }
    }

    private String getSubDirectoryName() {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "ControllerCreateDirectory.Input.DirectoryName"));
    }

    private boolean checkDoesNotExist(File subdirectory) {
        if (subdirectory.exists()) {
            JOptionPane.showMessageDialog(null,
                    Bundle.getString(
                    "ControllerCreateDirectory.ErrorMessage.DirectoryAlreadyExists",
                    subdirectory.getAbsolutePath()),
                    Bundle.getString(
                    "ControllerCreateDirectory.ErrorMessage.DirectoryAlreadyExists.Title"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }
}
