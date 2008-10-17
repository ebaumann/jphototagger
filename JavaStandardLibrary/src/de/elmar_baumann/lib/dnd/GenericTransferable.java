package de.elmar_baumann.lib.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class GenericTransferable implements Transferable {

public GenericTransferable(Object data) {
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
    private Object data;
    private DataFlavor[] flavors = new DataFlavor[1];{
        flavors[0] = DataFlavor.stringFlavor;
    }
}
