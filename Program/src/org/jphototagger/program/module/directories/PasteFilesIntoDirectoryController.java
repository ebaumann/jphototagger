package org.jphototagger.program.module.directories;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JTree;

import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.lib.datatransfer.TransferUtil.FilenameDelimiter;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.ClipboardUtil;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.FileAction;
import org.jphototagger.program.view.ViewUtil;

/**
 * Listens to keyboard actions whithin the directories tree and copies or
 * moves files to directories when the keys related to a copy or move action.
 *
 * @author Elmar Baumann
 */
public final class PasteFilesIntoDirectoryController implements KeyListener {

    public PasteFilesIntoDirectoryController() {
        listen();
    }

    private void listen() {
        GUI.getDirectoriesTree().addKeyListener(this);
        GUI.getFavoritesTree().addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (KeyEventUtil.isPaste(evt)) {
            Object source = evt.getSource();

            if (source instanceof JTree) {
                copyOrMovePastedFilesTo((JTree) source);
            }
        }
    }

    private void copyOrMovePastedFilesTo(JTree targetTree) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        if (isSingleDirectory(tnPanel.getOriginOfDisplayedThumbnails()) && filesWereCopiedOrCutted(tnPanel.getFileAction())) {
            insertFilesIntoSelectedDirectoryOf(targetTree);
        }
    }

    private void insertFilesIntoSelectedDirectoryOf(JTree targetTree) {
        List<File> sourceFiles = ClipboardUtil.getFilesFromSystemClipboard(FilenameDelimiter.NEWLINE);
        File targetDirectory = ViewUtil.getSelectedFile(targetTree);

        if ((targetDirectory != null) && !sourceFiles.isEmpty()) {
            copyOrMoveFiles(sourceFiles, targetDirectory);
        }
    }

    private void copyOrMoveFiles(List<File> sourceFiles, File targetDirectory) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();
        FileAction action = tnPanel.getFileAction();

        if (filesWereCopiedOrCutted(action)) {
            DirectoryTreeTransferHandler.handleDroppedFiles(action.getTransferHandlerAction(), sourceFiles,
                    targetDirectory);
            tnPanel.setFileAction(FileAction.UNDEFINED);
        }
    }

    private boolean filesWereCopiedOrCutted(FileAction action) {
        return action.equals(FileAction.COPY) || action.equals(FileAction.CUT);
    }

    private boolean isSingleDirectory(OriginOfDisplayedThumbnails content) {
        return content.equals(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY) || content.equals(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY);
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
