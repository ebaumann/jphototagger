package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;

/**
 * Transferable for objects of an arbitrary type. The data flavor returned by
 * {@link #getTransferDataFlavors()} is
 * {@link java.awt.datatransfer.DataFlavor#stringFlavor}.
 *
 * <em>The objects have to implement the Interface {@link Serializable}!</em>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public final class TransferableObject implements Transferable {

    private final Object data;
    private final DataFlavor[] flavors = new DataFlavor[1];

    {
        flavors[0] = DataFlavor.stringFlavor;
    }

    /**
     * Creates a new instance of this class.
     *
     * @param data data object returned by
     *             {@link #getTransferData(java.awt.datatransfer.DataFlavor)}.
     *             The object has to implement the Interface
     *             {@link Serializable}!
     */
    public TransferableObject(Object data) {
        super();
        this.data = data;
    }

    /**
     * Returns {@link java.awt.datatransfer.DataFlavor#stringFlavor}.
     *
     * @return string flavor
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Returns true.
     *
     * @param  flavor data flavor
     * @return true
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    /**
     * Returns the data set via constructor.
     *
     * @param  flavor data flavor
     * @return data object
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return data;
    }
}
