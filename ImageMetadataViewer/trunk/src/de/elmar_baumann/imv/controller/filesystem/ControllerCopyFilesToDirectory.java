package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCopyFilesToDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerCopyFilesToDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemCopyToDirectory().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        copySelectedFiles();
    }

    private void copySelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.setVisible(true);
        } else {
            AppLog.logWarning(ControllerCopyFilesToDirectory.class,
                    Bundle.getString(
                    "ControllerCopyFilesToDirectory.ErrorMessage.NoImagesSelected")); // NOI18N
        }
    }
}
