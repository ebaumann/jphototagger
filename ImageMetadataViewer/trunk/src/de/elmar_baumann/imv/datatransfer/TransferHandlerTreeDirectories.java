package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.listener.ProgressListener;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.helper.CopyFiles;
import de.elmar_baumann.imv.helper.FilesystemDatabaseUpdater;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-26
 */
public final class TransferHandlerTreeDirectories extends TransferHandler {

    static final String DELIMITER_FILENAMES =
            TransferHandlerPanelThumbnails.DELIMITER;

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        if (!TransferUtil.maybeContainFileData(transferSupport.getTransferable())) {
            return false;
        }
        JTree.DropLocation dropLocation =
                (JTree.DropLocation) transferSupport.getDropLocation();
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
        if (!transferSupport.isDrop()) return false;
        File targetDirectory = getTargetDirectory(transferSupport);
        Transferable transferable = transferSupport.getTransferable();
        List<File> sourceFiles = IoUtil.filterImageFiles(TransferUtil.getFiles(
                transferable, DELIMITER_FILENAMES));
        if (targetDirectory != null && !sourceFiles.isEmpty()) {
            handleDroppedFiles(
                    transferSupport.getUserDropAction(), sourceFiles,
                    targetDirectory);
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
        List<File> imageFiles = IoUtil.filterImageFiles(sourceFiles);
        if (imageFiles.isEmpty()) return;
        if (dropAction == COPY && confirmFileAction(
                "TransferHandlerTreeDirectories.Confirm.Copy", // NOI18N
                imageFiles.size(),
                targetDirectory.getAbsolutePath())) {
            copyFiles(targetDirectory, imageFiles);
        } else if (dropAction == MOVE && confirmFileAction(
                "TransferHandlerTreeDirectories.Confirm.Move", // NOI18N
                imageFiles.size(), targetDirectory.getAbsolutePath())) {
            moveFiles(targetDirectory, imageFiles);
        }
    }

    private static void copyFiles(File targetDirectory, List<File> sourceFiles) {
        CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        dialog.addFileSystemActionListener(new FilesystemDatabaseUpdater());
        addProgressListener(dialog);
        dialog.copy(true, CopyFiles.Options.CONFIRM_OVERWRITE);
    }

    private static void moveFiles(File targetDirectory, List<File> sourceFiles) {
        MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        addProgressListener(dialog);
        dialog.setVisible(true);
    }

    private File getTargetDirectory(TransferSupport transferSupport) {
        TreePath path =
                ((JTree.DropLocation) transferSupport.getDropLocation()).getPath();
        Object selNode = path.getLastPathComponent();
        if (selNode instanceof DefaultMutableTreeNode) {
            Object userObject =
                    ((DefaultMutableTreeNode) selNode).getUserObject();
            if (userObject instanceof File) {
                return (File) userObject;
            } else if (userObject instanceof FavoriteDirectory) {
                return ((FavoriteDirectory) userObject).getDirectory();
            }
            return (File) selNode;
        }
        return null;
    }

    private static boolean confirmFileAction(
            String bundleKey, int size, String absolutePath) {
        return MessageDisplayer.confirm(null, bundleKey,
                MessageDisplayer.CancelButton.HIDE, size, absolutePath).equals(
                MessageDisplayer.ConfirmAction.YES);
    }

    private synchronized static void addProgressListener(
            MoveToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

            @Override
            public void progressStarted(ProgressEvent evt) {
            }

            @Override
            public void progressPerformed(ProgressEvent evt) {
            }

            @Override
            public void progressEnded(ProgressEvent evt) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
        });

    }

    private synchronized static void addProgressListener(
            CopyToDirectoryDialog dialog) {

        dialog.addProgressListener(new ProgressListener() {

            @Override
            public void progressStarted(ProgressEvent evt) {
            }

            @Override
            public void progressPerformed(ProgressEvent evt) {
            }

            @Override
            public void progressEnded(ProgressEvent evt) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
        });

    }
}
