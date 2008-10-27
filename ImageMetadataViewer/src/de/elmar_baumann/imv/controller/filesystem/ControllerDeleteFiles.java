package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Deletes files from the filesystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/12
 */
public class ControllerDeleteFiles extends Controller implements ActionListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    PopupMenuPanelThumbnails popup = PopupMenuPanelThumbnails.getInstance();

    public ControllerDeleteFiles() {
        listenToActionSources();
    }

    private void listenToActionSources() {
        popup.addActionListenerFileSystemDeleteFiles(this);
        Panels.getInstance().getAppFrame().getMenuItemDelete().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Content content = thumbnailsPanel.getContent();
        if (isControl() && (content.equals(Content.Directory) ||
            content.equals(Content.FavoriteDirectory) ||
            popup.isDeleteFiles(e.getSource()))) {
            deleteSelectedFiles();
        }
    }

    private void deleteSelectedFiles() {
        if (accepted()) {
            int countDeleted = 0;
            List<File> files = thumbnailsPanel.getSelectedFiles();
            List<File> deletedFiles = new ArrayList<File>(files.size());
            for (File file : files) {
                if (file.delete()) {
                    deletedFiles.add(file);
                    countDeleted++;
                } else {
                    errorMessageDelete(file);
                }
            }
            if (countDeleted > 0) {
                db.deleteImageFiles(FileUtil.getAsFilenames(deletedFiles));
                thumbnailsPanel.remove(deletedFiles);
            }
        }
    }

    private void errorMessageDelete(File file) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteFiles.ErrorMessage.Delete"));
        Object[] params = {file.getAbsolutePath()};
        String message = msg.format(params);
        Logger.getLogger(ControllerDeleteFiles.class.getName()).log(Level.WARNING, message);
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(message, this));
    }

    private boolean accepted() {
        return JOptionPane.showConfirmDialog(
            null,
            Bundle.getString("ControllerDeleteFiles.ConfirmMessage.Delete"),
            Bundle.getString("ControllerDeleteFiles.ConfirmMessage.Delete.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }
}
