package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.io.ImageUtil;
import de.elmar_baumann.imv.io.IoUtil;
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

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        if (!Flavors.filesTransfered(transferSupport.getTransferable())) {
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
        List<File> sourceFiles =
                TransferUtil.getFiles(transferSupport.getTransferable(), "");
        if (targetDirectory != null && !sourceFiles.isEmpty()) {
            handleDroppedFiles(
                    transferSupport.getUserDropAction(), sourceFiles,
                    targetDirectory);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore, moving removes files from source directory
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
        if (dropAction == COPY) {
            ImageUtil.copyImageFiles(imageFiles, targetDirectory, true);
        } else if (dropAction == MOVE) {
            ImageUtil.moveImageFiles(imageFiles, targetDirectory, true);
        }
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
}
