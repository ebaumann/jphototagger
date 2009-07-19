package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JTree;

/**
 * Listens to keyboard actions whithin the directories tree and copies or
 * moves files to directories when the keys related to a copy or move action.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class ControllerDirectoryPasteFiles implements KeyListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerDirectoryPasteFiles() {
        listen();
    }

    private void listen() {
        appPanel.getTreeDirectories().addKeyListener(this);
        appPanel.getTreeFavorites().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isPaste(e)) {
            Object source = e.getSource();
            if (source instanceof JTree) {
                copyOrMovePastedFiles((JTree) source);
            }
        }
    }

    private void copyOrMovePastedFiles(JTree targetTree) {
        if (isValidContent(thumbnailsPanel.getContent()) &&
                isValidFileAction(thumbnailsPanel.getFileAction())) {
            insertFilesIntoSelectedDirectory(targetTree);
        }
    }

    private void insertFilesIntoSelectedDirectory(JTree targetTree) {
        List<File> sourceFiles = ClipboardUtil.getFilesFromSystemClipboard("\n"); // NOI18N
        File targetDirectory = ViewUtil.getSelectedFile(targetTree);
        if (sourceFiles.size() > 0 && targetDirectory != null) {
            copyOrMoveFiles(sourceFiles, targetDirectory);
        }
    }

    private void copyOrMoveFiles(List<File> sourceFiles, File targetDirectory) {
        FileAction action = thumbnailsPanel.getFileAction();
        assert isValidFileAction(action) : action;
        if (isValidFileAction(action)) {
            TransferHandlerTreeDirectories.handleDroppedFiles(
                    action.getTransferHandlerAction(), sourceFiles,
                    targetDirectory);
            thumbnailsPanel.setFileAction(FileAction.UNDEFINED);
        }
    }

    private boolean isValidFileAction(FileAction action) {
        return action.equals(FileAction.COPY) || action.equals(FileAction.CUT);
    }

    private boolean isValidContent(Content content) {
        return content.equals(Content.DIRECTORY) ||
                content.equals(Content.FAVORITE);
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
