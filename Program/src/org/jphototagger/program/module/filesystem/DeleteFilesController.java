package org.jphototagger.program.module.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.image.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.DeleteOption;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Listens to key events of {@code ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content is <em>not</em>
 * {@code OriginOfDisplayedThumbnails#FILES_OF_AN_IMAGE_COLLECTION}.
 *
 * @author Elmar Baumann
 */
public final class DeleteFilesController implements ActionListener, KeyListener {

    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public DeleteFilesController() {
        listen();
    }

    private void listen() {
        ThumbnailsPopupMenu.INSTANCE.getItemFileSystemDeleteFiles().addActionListener(this);
        GUI.getThumbnailsPanel().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (GUI.getThumbnailsPanel().getOriginOfDisplayedThumbnails().equals(OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION)) {
                return;
            }

            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        delete();
    }

    private void delete() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if ((tnPanel.isAFileSelected()) && tnPanel.getOriginOfDisplayedThumbnails().canDeleteImagesFromFileSystem()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    deleteSelectedFiles();
                }
            });
        }
    }

    private void deleteSelectedFiles() {
        List<File> deletedImageFiles = DeleteImageFiles.delete(GUI.getSelectedImageFiles(),
                DeleteOption.CONFIRM_DELETE, DeleteOption.MESSAGES_ON_FAILURES);

        if (!deletedImageFiles.isEmpty()) {
            repo.deleteImageFiles(deletedImageFiles);
            GUI.getThumbnailsPanel().removeFiles(deletedImageFiles);
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }
}
