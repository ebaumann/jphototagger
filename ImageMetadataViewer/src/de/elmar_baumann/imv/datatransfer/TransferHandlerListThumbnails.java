package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for a <code>JList</code> which handles
 * <code>java.awt.datatransfer.DataFlavor.stringFlavor</code>.
 * 
 * Specialized classes are implementing 
 * {@link #handleDroppedThumbnails(int, java.util.List)}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public abstract class TransferHandlerListThumbnails extends TransferHandler {

    static final String filenamesDelimiter = TransferHandlerThumbnailsPanel.delimiter;
    static final String itemDelimiter = "\n";

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return TransferUtil.getSelectedItemStringsTransferable(
            (JList) c, itemDelimiter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }
        String data = null;
        try {
            Transferable transferable = transferSupport.getTransferable();
            data = (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, ex);
            return false;
        }
        int listIndex = ((JList.DropLocation) transferSupport.getDropLocation()).getIndex();
        List<String> filenames = ArrayUtil.stringTokenToArray(data, filenamesDelimiter);
        handleDroppedThumbnails(listIndex, filenames);
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    /**
     * Specialized classes are handling the dropped thumbnails. This method ist
     * called from this class during
     * {@link #importData(javax.swing.TransferHandler.TransferSupport)}.
     * 
     * @param itemIndex  Index of the item where the thumbnails were dropped
     *                   or -1 if they were dropped into a blank list area
     * @param filenames  Filenames of the dropped thumbnails
     */
    protected abstract void handleDroppedThumbnails(
        int itemIndex, List<String> filenames);
}
