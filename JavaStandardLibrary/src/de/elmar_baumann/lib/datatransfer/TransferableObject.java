package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transferable for Objects of an arbitrary type. The data flavor is
 * <code>java.awt.datatransfer.DataFlavor.strinFlavor</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class TransferableObject implements Transferable {

    private Object data;
    private DataFlavor[] flavors = new DataFlavor[1];
    
    {
        flavors[0] = DataFlavor.stringFlavor;
    }

    public TransferableObject(Object data) {
        super();
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
}
