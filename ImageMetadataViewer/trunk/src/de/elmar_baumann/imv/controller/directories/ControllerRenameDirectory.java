package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * Listens to {@link PopupMenuTreeDirectories#getItemRenameDirectory()} and
 * renames a directory when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerRenameDirectory implements ActionListener {

    PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.INSTANCE;

    public ControllerRenameDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemRenameDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String directoryName = popup.getDirectoryName();
        if (directoryName != null) {
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                String newDirectoryName = getNewName(directory.getName());
                if (newDirectoryName != null &&
                        !newDirectoryName.trim().isEmpty()) {
                    File newDirectory = new File(directory.getParentFile(), newDirectoryName);
                    if (checkDoesNotExist(newDirectory)) {
                        try {
                            directory.renameTo(newDirectory);
                        } catch (Exception ex) {
                            AppLog.logWarning(ControllerRenameDirectory.class,
                                    ex);
                        }
                    }
                }
            }

        }
    }

    private String getNewName(String currentName) {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "ControllerRenameDirectory.Input.NewName"), currentName);
    }

    private boolean checkDoesNotExist(File subdirectory) {
        if (subdirectory.exists()) {
            JOptionPane.showMessageDialog(null,
                    Bundle.getString(
                    "ControllerRenameDirectory.ErrorMessage.DirectoryAlreadyExists",
                    subdirectory.getAbsolutePath()),
                    Bundle.getString(
                    "ControllerRenameDirectory.ErrorMessage.DirectoryAlreadyExists.Title"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }
}
