package de.elmar_baumann.imv.controller.directories;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.types.FileAction;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.event.KeyEventUtil;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;
import javax.swing.JTree;
import javax.swing.TransferHandler;

/**
 * Listens to keyboard actions whithin the directories tree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ControllerDirectoryCopyFiles extends Controller implements KeyListener {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private ImageFileThumbnailsPanel thumbnailsPanel = appPanel.getPanelThumbnails();
    private JTree treeDirectories = appPanel.getTreeDirectories();

    public ControllerDirectoryCopyFiles() {
        treeDirectories.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        handleKeyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void handleKeyPressed(KeyEvent e) {
        if (isControl() && thumbnailsPanel.getContent().equals(Content.Directory) &&
            KeyEventUtil.isInsert(e)) {
            List<File> sourceFiles = ClipboardUtil.getFilesFromSystemClipboard("\n");
            File targetDirectory = ViewUtil.getTargetDirectory(treeDirectories);
            if (sourceFiles.size() > 0 && targetDirectory != null) {
                insertFiles(sourceFiles, targetDirectory);
            }
        }
    }

    private void insertFiles(List<File> sourceFiles, File targetDirectory) {
        FileAction action = thumbnailsPanel.getFileAction();
        int dropAction = action.equals(FileAction.Copy)
            ? TransferHandler.COPY : TransferHandler.MOVE;
        TransferHandlerTreeDirectories.handleDroppedFiles(
            dropAction, sourceFiles, targetDirectory);
        if (action.equals(FileAction.Cut)) {
            thumbnailsPanel.setFileAction(FileAction.Undefined);
        }
    }
}
