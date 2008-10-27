package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.datatransfer.DataFlavor;
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
        ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        return thumbnailsPanel.getContent().equals(Content.Directory) &&
            (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
            transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor));
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
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
        JTree treeDirectories = Panels.getInstance().getAppPanel().getTreeDirectories();
        List<File> sourceFiles = TransferUtil.getFiles(transferSupport.getTransferable(),
            TransferHandlerThumbnailsPanel.delimiter);
        File targetDirectory = ViewUtil.getTargetDirectory(treeDirectories);
        if (targetDirectory != null & sourceFiles.size() > 0) {
            TransferHandlerTreeDirectories.handleDroppedFiles(
                transferSupport.getDropAction(), sourceFiles, targetDirectory);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
}
