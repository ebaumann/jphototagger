package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

/**
 * Handler for <strong>copying</strong> or <strong>moving</strong> a list of
 * thumbnails. The filenames of the thumbnails will be transferred as
 * <code>DataFlavor.stringFlavor</code>, each filename is separated by 
 * {@link #delimiter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public class TransferHandlerThumbnailsPanel extends TransferHandler {

    /**
     * Delimiter between the filenames in the transfered string.
     */
    static final String delimiter = "\n";

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        return canPanelImport((ImageFileThumbnailsPanel) transferSupport.getComponent()) &&
            TransferUtil.maybeContainFileData(transferSupport.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        List<String> filenames = FileUtil.getAsFilenames(
            ((ImageFileThumbnailsPanel) c).getSelectedFiles());
        return TransferUtil.getStringListTransferable(filenames, delimiter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        if (!canImport(transferSupport)) {
            return false;
        }
        List<File> sourceFiles = TransferUtil.getFiles(transferSupport.getTransferable(), delimiter);
        File targetDirectory = null;
        if (isContent(Content.Directory)) {
            JTree treeDirectories = Panels.getInstance().getAppPanel().getTreeDirectories();
            targetDirectory = ViewUtil.getSelectedDirectory(treeDirectories);
        } else if (isContent(Content.FavoriteDirectory)) {
            targetDirectory = ViewUtil.getSelectedDirectoryFromFavoriteDirectories();
        }
        if (targetDirectory != null & sourceFiles.size() > 0) {
            TransferHandlerTreeDirectories.handleDroppedFiles(
                transferSupport.getDropAction(), sourceFiles, targetDirectory);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    private boolean canPanelImport(ImageFileThumbnailsPanel thumbnailsPanel) {
        return thumbnailsPanel.getContent().equals(Content.Directory) ||
            thumbnailsPanel.getContent().equals(Content.FavoriteDirectory);
    }

    private boolean isContent(Content content) {
        ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        return thumbnailsPanel.getContent().equals(content);
    }
}
