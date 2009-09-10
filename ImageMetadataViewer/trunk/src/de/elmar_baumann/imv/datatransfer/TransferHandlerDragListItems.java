package de.elmar_baumann.imv.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import de.elmar_baumann.lib.datatransfer.TransferableObject;

/**
 * Creates a {@link Transferable} with selected list items as string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDragListItems extends TransferHandler {

    private final DataFlavor[] dataFlavors;

    public TransferHandlerDragListItems(DataFlavor... dataFlavors) {
        this.dataFlavors = new DataFlavor[dataFlavors.length];
        System.arraycopy(dataFlavors, 0, this.dataFlavors, 0, dataFlavors.length);
    }

    /**
     * Returns all selected items.
     * 
     * @param  c component
     * @return   transferrable
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(
                ((JList) c).getSelectedValues(), dataFlavors);
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
