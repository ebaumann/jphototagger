package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.DirectoryTreeModelFile;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final DataFlavor stringFlavor = DataFlavor.stringFlavor;
    private static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
    static final String filenamesDelimiter = TransferHandlerThumbnailsPanel.delimiter;

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        if (!transferSupport.isDataFlavorSupported(stringFlavor) &&
            !transferSupport.isDataFlavorSupported(fileListFlavor)) {
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
        if (targetDirectory != null) {
            int dropAction = transferSupport.getUserDropAction();
            try {
                if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    handleDroppedDirectories(dropAction, TransferUtil.getFileList(transferSupport), targetDirectory);
                } else if (transferSupport.isDataFlavorSupported(TransferUtil.getUriListFlavor())) {
                    handleDroppedDirectories(dropAction, TransferUtil.getFileListFromUriList(transferSupport), targetDirectory);
                } else if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    handleDroppedString(transferSupport, targetDirectory);
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    private boolean confirmFileAction(String messageFormat, int size, String absolutePath) {
        MessageFormat msg = new MessageFormat(messageFormat);
        Object[] params = {size, absolutePath};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            "Frage",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void handleDroppedDirectories(
        int dropAction, List<File> sourceFiles, File targetDirectory) {
        String msgFormatCopy = Bundle.getString("TransferHandlerTreeDirectories.ConfirmMessage.Copy");
        String msgFormatMove = Bundle.getString("TransferHandlerTreeDirectories.ConfirmMessage.Move");
        if (dropAction == COPY && confirmFileAction(msgFormatCopy, sourceFiles.size(),
            targetDirectory.getAbsolutePath())) {
            copyFiles(targetDirectory, sourceFiles);
            refresh();
        } else if (dropAction == MOVE && confirmFileAction(msgFormatMove,
            sourceFiles.size(), targetDirectory.getAbsolutePath())) {
            moveFiles(targetDirectory, sourceFiles);
            refresh();
        }
    }

    private void handleDroppedString(TransferSupport transferSupport, File targetDirectory) {
        try {
            Transferable transferable = transferSupport.getTransferable();
            String data = (String) transferable.getTransferData(stringFlavor);
            int dropAction = transferSupport.getUserDropAction();
            handleDroppedDirectories(dropAction,
                FileUtil.getAsFiles(ArrayUtil.stringTokenToArray(
                data, filenamesDelimiter)), targetDirectory);
        } catch (Exception ex) {
            Logger.getLogger(TransferHandlerTreeDirectories.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void copyFiles(File targetDirectory, List<File> sourceFiles) {
        CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        dialog.setVisible(true);
    }

    private void moveFiles(File targetDirectory, List<File> sourceFiles) {
        MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
        dialog.setTargetDirectory(targetDirectory);
        dialog.setSourceFiles(sourceFiles);
        dialog.setVisible(true);
    }

    private File getTargetDirectory(TransferSupport transferSupport) {
        TreePath path = ((JTree.DropLocation) transferSupport.getDropLocation()).getPath();
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent instanceof DirectoryTreeModelFile) {
            return (DirectoryTreeModelFile) lastPathComponent;
        }
        return null;
    }

    private void refresh() {
        Panels.getInstance().getAppPanel().getPanelThumbnails().refresh();
    }
}
