package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteThumbnailsFromDatabase implements ActionListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuPanelThumbnails popupMenu = PopupMenuPanelThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerDeleteThumbnailsFromDatabase() {
        listen();
    }

    private void listen() {
        popupMenu.addActionListenerDeleteThumbnail(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        deleteSelectedThumbnails();
    }

    private void deleteSelectedThumbnails() {
        if (deleteConfirmed()) {
            List<String> files = FileUtil.getAsFilenames(
                    thumbnailsPanel.getSelectedFiles());
            int countFiles = files.size();
            int countDeleted = db.deleteImageFiles(files);
            if (countDeleted != countFiles) {
                errorMessageDeleteImageFiles(countFiles, countDeleted);
            }
            repaint(files);
            thumbnailsPanel.repaint();
        }
    }

    private void repaint(List<String> filenames) {
        List<String> deleted = new ArrayList<String>(filenames.size());
        for (String filename : filenames) {
            if (!db.existsFilename(filename)) {
                deleted.add(filename);
            }
        }
        thumbnailsPanel.remove(FileUtil.getAsFiles(deleted));
    }

    private boolean deleteConfirmed() {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles"));
        Object[] params = {thumbnailsPanel.getSelectionCount()};
        return JOptionPane.showConfirmDialog(
                null,
                msg.format(params),
                Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void errorMessageDeleteImageFiles(int countFiles, int countDeleted) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles"));
        Object[] params = {countFiles, countDeleted};
        JOptionPane.showMessageDialog(
                null,
                msg.format(params),
                Bundle.getString("ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles.Title"),
                JOptionPane.ERROR_MESSAGE,
                AppIcons.getMediumAppIcon());
    }
}
