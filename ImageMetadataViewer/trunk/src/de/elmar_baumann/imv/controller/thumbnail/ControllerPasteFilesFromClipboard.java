package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * Listens to {@link AppFrame#getMenuItemPasteFromClipboard()} and on action
 * performed this class pastes the images in the clipboard into the current
 * directory.
 *
 * Enables the menu items based on the content (when it's a single directory).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-27
 */
public final class ControllerPasteFilesFromClipboard
        implements ActionListener, MenuListener, ThumbnailsPanelListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();
    private final JMenuItem menuItemPaste =
            GUI.INSTANCE.getAppFrame().getMenuItemPasteFromClipboard();

    public ControllerPasteFilesFromClipboard() {
        listen();
    }

    private void listen() {
        menuItemPaste.addActionListener(this);
        thumbnailsPanel.addThumbnailsPanelListener(this);
        GUI.INSTANCE.getAppFrame().getMenuEdit().addMenuListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getContent().canInsertImagesFromFileSystem()) {
            insertFiles(getDirectory());
            menuItemPaste.setEnabled(false);
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
    public void thumbnailsSelectionChanged() {
        // ignore
    }

    @Override
    public void thumbnailsChanged() {
        menuItemPaste.setEnabled(canPasteFiles());
    }

    private boolean canPasteFiles() {
        return thumbnailsPanel.getContent().canInsertImagesFromFileSystem() &&
                TransferUtil.systemClipboardMaybeContainFiles();
    }

    @Override
    public void menuSelected(MenuEvent e) {
        menuItemPaste.setEnabled(canPasteFiles());
    }

    @Override
    public void menuDeselected(MenuEvent e) {
        // ignore
    }

    @Override
    public void menuCanceled(MenuEvent e) {
        // ignore
    }
}
