package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Kontrolliert die Aktion: Lösche selektierte Thumbnails,
 * ausgelöst von
 * {@link org.jphototagger.program.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author Elmar Baumann
 */
public final class ControllerDeleteThumbnailsFromDatabase implements ActionListener {
    public ControllerDeleteThumbnailsFromDatabase() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemDeleteImageFromDatabase().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        deleteSelectedThumbnails();
    }

    private void deleteSelectedThumbnails() {
        if (confirmDelete()) {
            EventQueueUtil.invokeLater(new Runnable() {
                @Override
                public void run() {
                    List<File> selFiles = GUI.getSelectedImageFiles();
                    int countFiles = selFiles.size();
                    int countDeleted = DatabaseImageFiles.INSTANCE.delete(selFiles);

                    if (countDeleted != countFiles) {
                        errorMessageDeleteImageFiles(countFiles, countDeleted);
                    }

                    repaint(selFiles);
                    GUI.getThumbnailsPanel().repaint();
                }
            });
        }
    }

    private void repaint(final List<File> files) {
        List<File> deleted = new ArrayList<File>(files.size());
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

        for (File file : files) {
            if (!db.exists(file)) {
                deleted.add(file);
            }
        }

        GUI.getThumbnailsPanel().removeFiles(deleted);
    }

    private boolean confirmDelete() {
        return MessageDisplayer.confirmYesNo(null,
                "ControllerDeleteThumbnailsFromDatabase.Confirm.DeleteSelectedFiles",
                GUI.getThumbnailsPanel().getSelectionCount());
    }

    private void errorMessageDeleteImageFiles(int countFiles, int countDeleted) {
        MessageDisplayer.error(null, "ControllerDeleteThumbnailsFromDatabase.Error.DeleteSelectedFiles", countFiles,
                               countDeleted);
    }
}
