package org.jphototagger.program.module.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author Elmar Baumann
 */
public final class CopyFilesToDirectoryController implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(CopyFilesToDirectoryController.class.getName());

    public CopyFilesToDirectoryController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemFileSystemCopyToDirectory().addActionListener(this);
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
