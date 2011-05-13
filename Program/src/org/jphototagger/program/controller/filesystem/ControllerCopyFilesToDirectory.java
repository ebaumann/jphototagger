package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.helper.FilesystemDatabaseUpdater;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author Elmar Baumann
 */
public final class ControllerCopyFilesToDirectory implements ActionListener {
    public ControllerCopyFilesToDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemCopyToDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        copySelectedFiles();
    }

    private void copySelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (!selFiles.isEmpty()) {
            CopyToDirectoryDialog dlg = new CopyToDirectoryDialog();

            dlg.setSourceFiles(selFiles);
            dlg.addFileSystemActionListener(new FilesystemDatabaseUpdater(true));
            dlg.setVisible(true);
        } else {
            AppLogger.logWarning(ControllerCopyFilesToDirectory.class,
                                 "ControllerCopyFilesToDirectory.Error.NoImagesSelected");
        }
    }
}
