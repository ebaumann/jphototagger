package de.elmar_baumann.imv.dnd;

import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.dnd.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.JComponent;
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
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        //return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        ImageFileThumbnailsPanel thumbnailsPanel = (ImageFileThumbnailsPanel)c;
            //Panels.getInstance().getAppPanel().getPanelThumbnails(); // c is the baseclass
        List<String> filenames = FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        return TransferUtil.getStringListTransferable(filenames, delimiter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
}
