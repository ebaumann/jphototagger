package de.elmar_baumann.imv.datatransfer;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Creates a {@link Transferable} with selected list items as string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDragListItemsString extends TransferHandler {

    /**
     * Every exported string starts with this prefix
     */
    public static final String PREFIX =
            TransferHandlerDragListItemsString.class.getName();
    /**
     * Delimiter between transferred strings
     */
    public static final String DELIMITER = "|";

    /**
     * Returns all selected items in a string started with {@link #PREFIX} and
     * delimited by {@link #DELIMITER}.
     * 
     * @param  c component
     * @return   transferrable
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            JList list = (JList) c;
            Object[] selValues = list.getSelectedValues();
            if (selValues != null) {
                return new StringSelection(toString(selValues));
            }
        }
        return null;
    }

    private String toString(Object[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append(PREFIX);
        for (Object o : values) {
            sb.append(DELIMITER +
                    (o == null
                    ? ""
                    : o.toString()));
        }
        return sb.toString();
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
