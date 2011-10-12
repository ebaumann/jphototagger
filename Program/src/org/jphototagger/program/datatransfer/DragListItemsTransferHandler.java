package org.jphototagger.program.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.jdesktop.swingx.JXList;

import org.jphototagger.lib.datatransfer.TransferableObject;

/**
 * Creates a {@code TransferableObject} with selected list items as object array.
 *
 * @author Elmar Baumann
 */
public final class DragListItemsTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;
    private final DataFlavor[] dataFlavors;

    public DragListItemsTransferHandler(DataFlavor... dataFlavors) {
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
        return new TransferableObject(((JXList) c).getSelectedValues(), dataFlavors);
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
