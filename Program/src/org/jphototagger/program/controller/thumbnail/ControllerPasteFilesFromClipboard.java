package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.clipboard.ClipboardUtil;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.datatransfer.TransferHandlerDirectoryTree;
import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import org.jphototagger.program.view.ViewUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to {@link PopupMenuThumbnails#getItemPasteFromClipboard()} and on action
 * performed this class pastes the images in the clipboard into the current
 * directory.
 *
 * Enables the menu items based on the content (when it's a single directory).
 *
 * @author Elmar Baumann
 */
public final class ControllerPasteFilesFromClipboard
        implements ActionListener, KeyListener, MenuListener, ThumbnailsPanelListener {
    public ControllerPasteFilesFromClipboard() {
        listen();
    }

    private void listen() {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        getPasteItem().addActionListener(this);
        tnPanel.addThumbnailsPanelListener(this);
        GUI.getAppFrame().getMenuEdit().addMenuListener(this);
        tnPanel.addKeyListener(this);
    }

    private JMenuItem getPasteItem() {
        return PopupMenuThumbnails.INSTANCE.getItemPasteFromClipboard();
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
        Content content = GUI.getThumbnailsPanel().getContent();

        if (content.equals(Content.DIRECTORY)) {
            return ViewUtil.getSelectedFile(GUI.getDirectoriesTree());
        } else if (content.equals(Content.FAVORITE)) {
            return ViewUtil.getSelectedFile(GUI.getFavoritesTree());
        }

        return null;
    }

    private void insertFiles(final File file) {
        if ((file == null) ||!file.isDirectory()) {
            return;
        }

        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                List<File> files = ClipboardUtil.getFilesFromSystemClipboard(FilenameDelimiter.NEWLINE);

                TransferHandlerDirectoryTree.handleDroppedFiles(getEstimatedTransferHandlerAction(), files, file);
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

    @Override
    public void thumbnailsSelectionChanged() {

        // ignore
    }

    @Override
    public void thumbnailsChanged() {
        EventQueueUtil.invokeLater(new Runnable() {
            @Override
            public void run() {
                getPasteItem().setEnabled(canPasteFiles());
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
