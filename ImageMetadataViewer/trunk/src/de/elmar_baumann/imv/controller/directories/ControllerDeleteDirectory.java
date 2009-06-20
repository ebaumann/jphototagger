package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuTreeDirectories;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Listens to {@link PopupMenuTreeDirectories#getItemDeleteDirectory()} and
 * deletes a directory when the action fires.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/19
 */
public final class ControllerDeleteDirectory implements ActionListener {

    PopupMenuTreeDirectories popup = PopupMenuTreeDirectories.INSTANCE;

    public ControllerDeleteDirectory() {
        listen();
    }

    private void listen() {
        popup.getItemDeleteDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String directoryName = popup.getDirectoryName();
        if (directoryName != null) {
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                if (confirmDelete(directoryName)) {
                    try {
                        List<File> imageFiles = ImageFilteredDirectory.
                                getImageFilesOfDirAndSubDirs(directory);
                        if (FileUtil.deleteDirectory(directory)) {
                            DatabaseImageFiles.INSTANCE.deleteImageFiles(
                                    FileUtil.getAsFilenames(imageFiles));
                        } else {
                            errorMessageDelete(directoryName);
                        }
                    } catch (Exception ex) {
                        AppLog.logWarning(ControllerDeleteDirectory.class,
                                ex);
                    }
                }
            }
        }
    }

    private boolean confirmDelete(String directoryName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("ControllerDeleteDirectory.ConfirmMessage",
                directoryName),
                Bundle.getString(
                "ControllerDeleteDirectory.ConfirmMessage.Title"),
                JOptionPane.YES_NO_OPTION) ==
                JOptionPane.YES_OPTION;
    }

    private void errorMessageDelete(String directoryName) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString("ControllerDeleteDirectory.ErrorMessage",
                directoryName),
                Bundle.getString(
                "ControllerDeleteDirectory.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
