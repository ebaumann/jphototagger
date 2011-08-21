package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.controller.imagecollection.ControllerDeleteFromImageCollection;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.helper.DeleteImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.program.types.DeleteOption;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content is <em>not</em>
 * {@link TypeOfDisplayedImages#IMAGE_COLLECTION}.
 *
 * @author Elmar Baumann
 * @see     ControllerDeleteFromImageCollection
 */
public final class ControllerDeleteFiles implements ActionListener, KeyListener {
    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemDeleteFiles().addActionListener(this);
        GUI.getThumbnailsPanel().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (GUI.getThumbnailsPanel().getContent().equals(TypeOfDisplayedImages.IMAGE_COLLECTION)) {
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

        if ((tnPanel.isAFileSelected()) && tnPanel.getContent().canDeleteImagesFromFileSystem()) {
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
            DatabaseImageFiles.INSTANCE.delete(deletedImageFiles);
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
