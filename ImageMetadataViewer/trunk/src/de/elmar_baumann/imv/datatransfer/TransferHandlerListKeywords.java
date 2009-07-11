package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for {@link AppPanel#getListKeywords() }.
 * 
 * Creates a {@link Transferable} with a keyword as content. The transferable is
 * a {@link TransferableObject} instance which supports the data flavor
 * {@link java.awt.datatransfer.DataFlavor#stringFlavor}. The string in that
 * instance has the prefix {@link #PREFIX} before the keyword to differ from
 * other strings in transferables which also supports that data flavor.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TransferHandlerListKeywords extends TransferHandler {

    /**
     * Prefix before a subject.
     */
    public static final String PREFIX = "Subject:";

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            Object selValue = list.getSelectedValue();
            if (selValue != null) {
                return new TransferableObject(PREFIX + selValue.toString());
            }
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
