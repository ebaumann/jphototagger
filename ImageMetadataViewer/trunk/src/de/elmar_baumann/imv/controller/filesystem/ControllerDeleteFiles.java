package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.controller.imagecollection.ControllerDeleteFromImageCollection;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.DeleteImageFiles;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.DeleteOption;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.EnumSet;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Listens to key events of {@link ImageFileThumbnailsPanel} and when the
 * <code>DEL</code> key was pressed deletes the selected files from the
 * file system if the panel's content is <em>not</em>
 * {@link Content#IMAGE_COLLECTION}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-12
 * @see     ControllerDeleteFromImageCollection
 */
public final class ControllerDeleteFiles implements ActionListener, KeyListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final PopupMenuThumbnails popupMenu =
            PopupMenuThumbnails.INSTANCE;

    public ControllerDeleteFiles() {
        listen();
    }

    private void listen() {
        popupMenu.getItemFileSystemDeleteFiles().addActionListener(this);
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delete();
    }

    private void delete() {
        if (thumbnailsPanel.getSelectionCount() > 0 &&
                thumbnailsPanel.getContent().canDeleteImagesFromFileSystem()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    deleteSelectedFiles();
                }
            });
        }
    }

    private void deleteSelectedFiles() {
        List<File> deletedImageFiles = DeleteImageFiles.delete(
                thumbnailsPanel.getSelectedFiles(), EnumSet.of(
                DeleteOption.CONFIRM_DELETE,
                DeleteOption.MESSAGES_ON_FAILURES));
        if (deletedImageFiles.size() > 0) {
            db.deleteImageFiles(FileUtil.getAsFilenames(deletedImageFiles));
            thumbnailsPanel.remove(deletedImageFiles);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
