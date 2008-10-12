package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
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
public class ControllerDeleteFiles extends Controller implements ActionListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelImageFileThumbnails();
    Database db = Database.getInstance();

    public ControllerDeleteFiles() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerFileSystemDeleteFiles(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            deleteFiles();
        }
    }

    private void deleteFiles() {
        if (accepted()) {
            int count = 0;
            List<String> filenames = thumbnailsPanel.getSelectedFilenames();
            List<String> deletedFiles = new ArrayList<String>(filenames.size());
            for (String filename : filenames) {
                File file = new File(filename);
                if (file.delete()) {
                    deletedFiles.add(filename);
                    count++;
                } else {
                    MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteFiles.ErrorMessage.Delete"));
                    Object[] params = {filename};
                    ErrorListeners.getInstance().notifyErrorListener(
                        new ErrorEvent(msg.format(params), this));
                }
            }
            if (count > 0) {
                db.deleteImageFiles(deletedFiles);
                thumbnailsPanel.removeFilenames(deletedFiles);
            }
        }
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
