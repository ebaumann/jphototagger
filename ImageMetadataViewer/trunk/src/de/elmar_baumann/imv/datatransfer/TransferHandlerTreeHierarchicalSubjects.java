package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.view.dialogs.HierarchicalSubjectsDialog;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;

/**
 * Handles drops onto the {@link HierarchicalSubjectsDialog}'s tree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TransferHandlerTreeHierarchicalSubjects extends TransferHandler {

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        Transferable transferable = transferSupport.getTransferable();
        if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        try {
            Object o = transferable.getTransferData(DataFlavor.stringFlavor);
            if (o instanceof String) {
                boolean isSubject = ((String) o).startsWith(
                        TransferHandlerListKeywords.PREFIX);
                JTree.DropLocation dropLocation =
                        (JTree.DropLocation) transferSupport.getDropLocation();
                return isSubject && dropLocation.getPath() != null;
            }
        } catch (Exception ex) {
            AppLog.logWarning(TransferHandlerTreeDirectories.class, ex);
        }
        return false;
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
    public boolean importData(TransferSupport transferSupport) {
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
}
