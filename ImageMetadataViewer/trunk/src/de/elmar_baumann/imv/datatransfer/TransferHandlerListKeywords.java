package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for {@link AppPanel#getListKeywords() }.
 * 
 * Creates a {@link Transferable} with selected keywords as content. The
 * transferable is a {@link TransferableObject} instance which supports the data
 * flavor {@link Flavors#KEYWORDS_FLAVOR}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-11
 */
public final class TransferHandlerListKeywords extends TransferHandler {

    /**
     * Returns the keywords in a transferable object.
     *
     * <em>The transferable has to support the data flavor
     * {@link Flavors#KEYWORDS_FLAVOR}!</em>
     *
     * @param  transferable transferable object
     * @return              keywords or null on errors
     */
    public static Object[] getKeywords(Transferable transferable) {
        try {
            return (Object[]) transferable.getTransferData(
                    Flavors.KEYWORDS_FLAVOR);
        } catch (Exception e) {
            AppLog.logSevere(TransferHandlerListKeywords.class, e);
        }
        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        Object[] selValues = list.getSelectedValues();
        return new TransferableObject(selValues, Flavors.KEYWORDS_FLAVOR);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
