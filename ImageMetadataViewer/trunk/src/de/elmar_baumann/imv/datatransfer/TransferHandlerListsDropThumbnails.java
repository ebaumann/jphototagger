package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;
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
 * @version 2008-10-24
 */
public abstract class TransferHandlerListsDropThumbnails extends TransferHandler {

    static final String DELIMITER_ITEMS = "\n"; // NOI18N

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return TransferUtil.maybeContainFileData(
                transferSupport.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return TransferUtil.getSelectedItemStringsTransferable(
                (JList) c, DELIMITER_ITEMS);
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
        List<File> files = null;
        try {
            Transferable transferable = transferSupport.getTransferable();
            files = (List<File>) transferable.getTransferData(
                    DataFlavor.javaFileListFlavor);
        } catch (Exception ex) {
            AppLog.logSevere(TransferHandlerListsDropThumbnails.class, ex);
            return false;
        }
        int listIndex =
                ((JList.DropLocation) transferSupport.getDropLocation()).
                getIndex();
        handleDroppedThumbnails(listIndex, FileUtil.getAsFilenames(files));
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
