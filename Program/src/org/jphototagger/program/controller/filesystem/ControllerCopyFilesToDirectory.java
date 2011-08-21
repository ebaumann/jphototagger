package org.jphototagger.program.controller.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.CopyToDirectoryDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author Elmar Baumann
 */
public final class ControllerCopyFilesToDirectory implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(ControllerCopyFilesToDirectory.class.getName());

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
            dlg.setVisible(true);
        } else {
            LOGGER.log(Level.WARNING, "Copy images: No images selected!");
        }
    }
}
