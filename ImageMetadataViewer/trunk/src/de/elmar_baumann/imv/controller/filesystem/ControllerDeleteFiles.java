package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.tasks.FileSystemDeleteImageFiles;
import de.elmar_baumann.imv.types.DeleteOption;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.EnumSet;
import java.util.List;

/**
 * Deletes files from the filesystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public final class ControllerDeleteFiles implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.INSTANCE;

    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerFileSystemDeleteFiles(this);
        GUI.INSTANCE.getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteSelectedFiles();
        thumbnailsPanel.repaint();
    }

    private void deleteSelectedFiles() {
        List<File> deletedImageFiles = FileSystemDeleteImageFiles.delete(
            thumbnailsPanel.getSelectedFiles(), EnumSet.of(
            DeleteOption.CONFIRM_DELETE,
            DeleteOption.MESSAGES_ON_FAILURES));

        if (deletedImageFiles.size() > 0) {
            db.deleteImageFiles(FileUtil.getAsFilenames(deletedImageFiles));
            thumbnailsPanel.remove(deletedImageFiles);
        } else {
            AppLog.logWarning(ControllerDeleteFiles.class, Bundle.getString("ControllerDeleteFiles.ErrorMessage.NoImagesDeleted"));
        }
    }
}
