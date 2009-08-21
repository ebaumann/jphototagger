package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.event.listener.ThumbnailsPanelListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JTree;
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
        implements ActionListener, KeyListener, MenuListener,
        ThumbnailsPanelListener {

    private final ThumbnailsPanel thumbnailsPanel =
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
        thumbnailsPanel.addKeyListener(this);
        GUI.INSTANCE.getAppPanel().getTreeDirectories().addKeyListener(this);
        GUI.INSTANCE.getAppPanel().getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (menuItemPaste.isEnabled()) return;
        if (KeyEventUtil.isControl(e, KeyEvent.VK_V) && canPasteFiles()) {
            Object source = e.getSource();
            if (source == thumbnailsPanel) {
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

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
