package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class DeleteThumbnailsFromRepositoryController implements ActionListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public DeleteThumbnailsFromRepositoryController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemDeleteImageFromRepository().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        deleteSelectedThumbnails();
    }

    private void deleteSelectedThumbnails() {
        if (confirmDelete()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

                @Override
                public void run() {
                    List<File> selFiles = GUI.getSelectedImageFiles();
                    int countFiles = selFiles.size();
                    int countDeleted = repo.deleteImageFiles(selFiles);

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
        List<File> deleted = new ArrayList<>(files.size());

        for (File file : files) {
            if (!repo.existsImageFile(file)) {
                deleted.add(file);
            }
        }

        GUI.getThumbnailsPanel().removeFiles(deleted);
    }

    private boolean confirmDelete() {
        String message = Bundle.getString(DeleteThumbnailsFromRepositoryController.class, "DeleteThumbnailsFromRepositoryController.Confirm.DeleteSelectedFiles",
                GUI.getThumbnailsPanel().getSelectionCount());

        return MessageDisplayer.confirmYesNo(null, message);
    }

    private void errorMessageDeleteImageFiles(int countFiles, int countDeleted) {
        String message = Bundle.getString(DeleteThumbnailsFromRepositoryController.class, "DeleteThumbnailsFromRepositoryController.Error.DeleteSelectedFiles",
                countFiles, countDeleted);
        MessageDisplayer.error(null, message);
    }
}
