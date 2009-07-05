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
import javax.swing.TransferHandler;

/**
 * Listens to keyboard actions whithin the directories tree and copies or
 * moves files to directories when the keys related to a copy or move action.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public final class ControllerDirectoryPasteFiles implements KeyListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ImageFileThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();
    private final JTree treeDirectories = appPanel.getTreeDirectories();

    public ControllerDirectoryPasteFiles() {
        listen();
    }

    private void listen() {
        treeDirectories.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (KeyEventUtil.isPaste(e)) {
            copyOrMovePastedFiles();
        }
    }

    private void copyOrMovePastedFiles() {
        if (thumbnailsPanel.getContent().equals(Content.DIRECTORY) &&
                thumbnailsPanel.getFileAction() == FileAction.COPY ||
                thumbnailsPanel.getFileAction() == FileAction.CUT) {
            insertFilesIntoSelectedDirectory();
        }
    }

    private void insertFilesIntoSelectedDirectory() {
        List<File> sourceFiles = ClipboardUtil.getFilesFromSystemClipboard("\n");
        File targetDirectory = ViewUtil.getSelectedFile(treeDirectories);
        if (sourceFiles.size() > 0 && targetDirectory != null) {
            copyOrMoveFiles(sourceFiles, targetDirectory);
        }
    }

    private void copyOrMoveFiles(List<File> sourceFiles, File targetDirectory) {
        FileAction action = thumbnailsPanel.getFileAction();
        boolean isValidAction = action == FileAction.COPY ||
                action == FileAction.CUT;
        assert isValidAction : action;
        if (isValidAction) {
            int fileAction = action.equals(FileAction.COPY)
                             ? TransferHandler.COPY
                             : TransferHandler.MOVE;
            TransferHandlerTreeDirectories.handleDroppedFiles(
                    fileAction, sourceFiles, targetDirectory);
            thumbnailsPanel.setFileAction(FileAction.UNDEFINED);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // nothing to do
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nothing to do
    }
}
