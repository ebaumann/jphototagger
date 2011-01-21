package org.jphototagger.program.controller.filesystem;

import org.jphototagger.program.controller.imagecollection
    .ControllerDeleteFromImageCollection;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.helper.DeleteImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.types.DeleteOption;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.List;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content is <em>not</em>
 * {@link Content#IMAGE_COLLECTION}.
 *
 * @author Elmar Baumann
 * @see     ControllerDeleteFromImageCollection
 */
public final class ControllerDeleteFiles
        implements ActionListener, KeyListener {
    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemDeleteFiles()
            .addActionListener(this);
        GUI.getThumbnailsPanel().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            if (GUI.getThumbnailsPanel().getContent().equals(
                    Content.IMAGE_COLLECTION)) {
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

        if ((tnPanel.isFileSelected())
                && tnPanel.getContent().canDeleteImagesFromFileSystem()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    deleteSelectedFiles();
                }
            });
        }
    }

    private void deleteSelectedFiles() {
        List<File> deletedImageFiles =
            DeleteImageFiles.delete(GUI.getSelectedImageFiles(),
                                    DeleteOption.CONFIRM_DELETE,
                                    DeleteOption.MESSAGES_ON_FAILURES);

        if (!deletedImageFiles.isEmpty()) {
            DatabaseImageFiles.INSTANCE.delete(deletedImageFiles);
            GUI.getThumbnailsPanel().remove(deletedImageFiles);
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
