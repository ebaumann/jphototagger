package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 * Listen to key events in the {@link ImageFileThumbnailsPanel} and when
 * {@link KeyEventUtil#isPaste(java.awt.event.KeyEvent)} is true and
 * the thumbnail panel's content
 * {@link Content#canInsertImagesFromFileSystem()} is true image files from
 * the clipboard are pasted into the directory of the displayed thumbnails.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-27
 */
public final class ControllerPasteFilesFromClipboard implements KeyListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerPasteFilesFromClipboard() {
        listen();
    }

    private void listen() {
        thumbnailsPanel.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isPaste(e) &&
                thumbnailsPanel.getContent().canInsertImagesFromFileSystem()) {
            insertFiles(getDirectory());
        }
    }

    private File getDirectory() {
        Content content = thumbnailsPanel.getContent();
        if (content.equals(Content.DIRECTORY)) {
            return ViewUtil.getSelectedFile(
                    GUI.INSTANCE.getAppPanel().getTreeDirectories());
        } else if (content.equals(Content.FAVORITE)) {
            return ViewUtil.getSelectedFile(
                    GUI.INSTANCE.getAppPanel().getTreeFavorites());
        }
        return null;
    }

    private void insertFiles(final File file) {
        if (file == null || !file.isDirectory()) return;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                List<File> files =
                        ClipboardUtil.getFilesFromSystemClipboard("\n"); // NOI18N
                TransferHandlerTreeDirectories.handleDroppedFiles(
                        getEstimatedTransferHandlerAction(), files, file);
                emptyClipboard();
                thumbnailsPanel.refresh();
            }

            public int getEstimatedTransferHandlerAction() {
                Integer action =
                        thumbnailsPanel.getFileAction().getTransferHandlerAction();
                return action == null
                       ? TransferHandler.COPY
                       : action;
            }

            private void emptyClipboard() {
                ClipboardUtil.copyToSystemClipboard(new ArrayList<File>(), null);
            }
        });
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
