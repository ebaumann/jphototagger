package org.jphototagger.program.controller.thumbnail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jphototagger.api.image.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.datatransfer.DirectoryTreeTransferHandler;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.ViewUtil;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.ThumbnailsPopupMenu;

/**
 * Listens to {@code ThumbnailsPopupMenu#getItemPasteFromClipboard()} and on action
 * performed this class pastes the images in the clipboard into the current
 * directory.
 *
 * Enables the menu items based on the content (when it's a single directory).
 *
 * @author Elmar Baumann
 */
public final class PasteFilesFromClipboardController implements ActionListener, KeyListener, MenuListener {

    public PasteFilesFromClipboardController() {
        listen();
    }

    private void listen() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        getPasteItem().addActionListener(this);
        GUI.getAppFrame().getMenuEdit().addMenuListener(this);
        tnPanel.addKeyListener(this);
        AnnotationProcessor.process(this);
    }

    private JMenuItem getPasteItem() {
        return ThumbnailsPopupMenu.INSTANCE.getItemPasteFromClipboard();
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (!getPasteItem().isEnabled()) {
            return;
        }

        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_V) && canPasteFiles()) {
            Object source = evt.getSource();

            if (source == GUI.getThumbnailsPanel()) {
                insertFiles(getDirectory());
            } else if (isTreeSelection(source)) {
                insertFiles(ViewUtil.getSelectedFile((JTree) source));
            }
        }
    }

    private boolean isTreeSelection(Object source) {
        if (source instanceof JTree) {
            return ((JTree) source).getSelectionCount() > 0;
        }

        return false;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getThumbnailsPanel().getContent().canInsertImagesFromFileSystem()) {
            insertFiles(getDirectory());
            getPasteItem().setEnabled(false);
        }
    }

    private File getDirectory() {
        OriginOfDisplayedThumbnails content = GUI.getThumbnailsPanel().getContent();

        if (content.equals(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY)) {
            return ViewUtil.getSelectedFile(GUI.getDirectoriesTree());
        } else if (content.equals(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY)) {
            return ViewUtil.getSelectedFile(GUI.getFavoritesTree());
        }

        return null;
    }

    private void insertFiles(final File file) {
        if ((file == null) || !file.isDirectory()) {
            return;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                List<File> files = ClipboardUtil.getFilesFromSystemClipboard(FilenameDelimiter.NEWLINE);

                DirectoryTreeTransferHandler.handleDroppedFiles(getEstimatedTransferHandlerAction(), files, file);
                emptyClipboard();
            }

            public int getEstimatedTransferHandlerAction() {
                Integer action = GUI.getThumbnailsPanel().getFileAction().getTransferHandlerAction();

                return (action == null)
                        ? TransferHandler.COPY
                        : action;
            }

            private void emptyClipboard() {
                ClipboardUtil.copyToSystemClipboard(new ArrayList<File>(), null);
            }
        });
    }

    @EventSubscriber(eventClass = ThumbnailsChangedEvent.class)
    public void thumbnailsChanged(final ThumbnailsChangedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                boolean canPasteFiles = canPasteFiles();
                JMenuItem pasteItem = getPasteItem();

                pasteItem.setEnabled(canPasteFiles);
            }
        });
    }

    private boolean canPasteFiles() {
        return GUI.getThumbnailsPanel().getContent().canInsertImagesFromFileSystem()
                && TransferUtil.systemClipboardMaybeContainFiles();
    }

    @Override
    public void menuSelected(MenuEvent evt) {
        getPasteItem().setEnabled(canPasteFiles());
    }

    @Override
    public void menuDeselected(MenuEvent evt) {
        // ignore
    }

    @Override
    public void menuCanceled(MenuEvent evt) {
        // ignore
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
