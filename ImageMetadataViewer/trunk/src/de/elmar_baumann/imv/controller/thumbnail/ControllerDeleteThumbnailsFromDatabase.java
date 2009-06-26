package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/10
 */
public final class ControllerDeleteThumbnailsFromDatabase implements
        ActionListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuPanelThumbnails popupMenu =
            PopupMenuPanelThumbnails.INSTANCE;
    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.
            getAppPanel().getPanelThumbnails();

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
        if (confirmDelete()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
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
            });
        }
    }

    private void repaint(final List<String> filenames) {
        List<String> deleted = new ArrayList<String>(filenames.size());
        for (String filename : filenames) {
            if (!db.existsFilename(filename)) {
                deleted.add(filename);
            }
        }
        thumbnailsPanel.remove(FileUtil.getAsFiles(deleted));
    }

    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString(
                "ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles",
                thumbnailsPanel.getSelectionCount()),
                Bundle.getString(
                "ControllerDeleteThumbnailsFromDatabase.ConfirmMessage.DeleteSelectedFiles.Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    private void errorMessageDeleteImageFiles(int countFiles, int countDeleted) {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString(
                "ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles",
                countFiles, countDeleted),
                Bundle.getString(
                "ControllerDeleteThumbnailsFromDatabase.ErrorMessage.DeleteSelectedFiles.Title"),
                JOptionPane.ERROR_MESSAGE);
    }
}
