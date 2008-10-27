package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class TransferHandlerTreeDirectories extends TransferHandler {

    static final String filenamesDelimiter = TransferHandlerThumbnailsPanel.delimiter;

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        if (!TransferUtil.maybeContainFileData(transferSupport.getTransferable())) {
            return false;
        }
        JTree.DropLocation dropLocation = (JTree.DropLocation) transferSupport.getDropLocation();
        return dropLocation.getPath() != null;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }
        File targetDirectory = getTargetDirectory(transferSupport);
        Transferable transferable = transferSupport.getTransferable();
        List<File> sourceFiles = IoUtil.getImageFiles(TransferUtil.getFiles(transferable, filenamesDelimiter));
        if (targetDirectory != null && !sourceFiles.isEmpty()) {
            handleDroppedFiles(
                transferSupport.getUserDropAction(), sourceFiles, targetDirectory);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    /**
     * Handles dropped files: Asks whether to copy or move and if confirmed
     * copys or moves the files.
     * 
     * @param dropAction
     * @param sourceFiles
     * @param targetDirectory  target directory
     */
    public static void handleDroppedFiles(
        int dropAction, List<File> sourceFiles, File targetDirectory) {
        List<File> imageFiles = IoUtil.getImageFiles(sourceFiles);
        if (imageFiles.isEmpty()) {
            return;
        }
        String msgFormatCopy = Bundle.getString("TransferHandlerTreeDirectories.ConfirmMessage.Copy");
        String msgFormatMove = Bundle.getString("TransferHandlerTreeDirectories.ConfirmMessage.Move");
        if (dropAction == COPY && confirmFileAction(msgFormatCopy, imageFiles.size(),
            targetDirectory.getAbsolutePath())) {
            copyFiles(targetDirectory, imageFiles);
        } else if (dropAction == MOVE && confirmFileAction(msgFormatMove,
            imageFiles.size(), targetDirectory.getAbsolutePath())) {
            moveFiles(targetDirectory, imageFiles);
        }
    }

    private static void copyFiles(File targetDirectory, List<File> sourceFiles) {
        CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        addProgressListener(dialog);
        dialog.setVisible(true);
    }

    private static void moveFiles(File targetDirectory, List<File> sourceFiles) {
        MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        addProgressListener(dialog);
        dialog.setVisible(true);
    }

    private File getTargetDirectory(TransferSupport transferSupport) {
        TreePath path = ((JTree.DropLocation) transferSupport.getDropLocation()).getPath();
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent instanceof File) {
            return (File) lastPathComponent;
        }
        return null;
    }

    private static boolean confirmFileAction(String messageFormat, int size, String absolutePath) {
        MessageFormat msg = new MessageFormat(messageFormat);
        Object[] params = {size, absolutePath};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            Bundle.getString("TransferHandlerTreeDirectories.ConfirmMessage.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private static void addProgressListener(MoveToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

            @Override
            public void progressStarted(ProgressEvent evt) {
            }

            @Override
            public void progressPerformed(ProgressEvent evt) {
            }

            @Override
            public void progressEnded(ProgressEvent evt) {
                Panels.getInstance().getAppPanel().getPanelThumbnails().refresh();
            }
        });

    }

    private static void addProgressListener(CopyToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

            @Override
            public void progressStarted(ProgressEvent evt) {
            }

            @Override
            public void progressPerformed(ProgressEvent evt) {
            }

            @Override
            public void progressEnded(ProgressEvent evt) {
                Panels.getInstance().getAppPanel().getPanelThumbnails().refresh();
            }
        });

    }
}
