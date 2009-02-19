package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.template.Pair;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Deletes files from the filesystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public final class ControllerDeleteFiles implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        popup.addActionListenerFileSystemDeleteFiles(this);
        Panels.getInstance().getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteSelectedFiles();
        thumbnailsPanel.repaint();
    }

    private void deleteSelectedFiles() {
        if (confirmDelete()) {
            int countDeleted = 0;
            List<File> imageFiles = thumbnailsPanel.getSelectedFiles();
            List<Pair<File, File>> imageFilesWithSidecarFiles = XmpMetadata.getImageFilesWithSidecarFiles(imageFiles);
            List<File> deletedImageFiles = new ArrayList<File>(imageFiles.size());
            for (File imageFile : imageFiles) {
                if (imageFile.delete()) {
                    deleteSidecarFile(imageFile, imageFilesWithSidecarFiles);
                    deletedImageFiles.add(imageFile);
                    countDeleted++;
                } else {
                    errorMessageDelete(imageFile);
                }
            }
            if (countDeleted > 0) {
                db.deleteImageFiles(FileUtil.getAsFilenames(deletedImageFiles));
                thumbnailsPanel.remove(deletedImageFiles);
            }
        }
    }

    private void deleteSidecarFile(File imageFile, List<Pair<File, File>> imageFilesWithSidecarFiles) {
        for (Pair<File, File> filePair : imageFilesWithSidecarFiles) {
            if (filePair.getFirst().equals(imageFile)) {
                File sidecarFile = filePair.getSecond();
                if (!sidecarFile.delete()) {
                    errorMessageDelete(sidecarFile);
                }
            }
        }
    }

    private void errorMessageDelete(File file) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteFiles.ErrorMessage.Delete"));
        Object[] params = {file.getAbsolutePath()};
        String message = msg.format(params);
        AppLog.logWarning(ControllerDeleteFiles.class, message);
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("ControllerDeleteFiles.ConfirmMessage.Delete"),
                Bundle.getString("ControllerDeleteFiles.ConfirmMessage.Delete.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }
}
