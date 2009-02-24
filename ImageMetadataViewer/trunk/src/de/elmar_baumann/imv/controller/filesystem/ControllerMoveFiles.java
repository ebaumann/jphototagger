package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.FileSystemAction;
import de.elmar_baumann.imv.event.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Renames files in the file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public final class ControllerMoveFiles implements ActionListener, FileSystemActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    public ControllerMoveFiles() {
        listen();
    }

    private void listen() {
        PopupMenuPanelThumbnails.INSTANCE.addActionListenerFileSystemMoveFiles(this);
        ListenerProvider.INSTANCE.addFileSystemActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveSelectedFiles();
    }

    private void moveSelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.setVisible(true);
        } else {
            AppLog.logWarning(ControllerMoveFiles.class, Bundle.getString("ControllerMoveFiles.ErrorMessaga.NoImagesSelected"));
        }
    }

    @Override
    public void actionPerformed(FileSystemAction action, File src, File target) {
        if (!src.getName().toLowerCase().endsWith(".xmp")) {
            db.updateRenameImageFilename(src.getAbsolutePath(), target.getAbsolutePath());
        }
    }

    @Override
    public void actionFailed(FileSystemAction action, FileSystemError error, File src, File target) {
    }
}
