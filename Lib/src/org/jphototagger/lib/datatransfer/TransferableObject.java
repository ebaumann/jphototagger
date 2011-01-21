package org.jphototagger.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.io.Serializable;

import java.util.Arrays;

/**
 * Transferable for objects of an arbitrary type.
 *
 * <em>The objects have to implement the Interface {@link Serializable}!</em>
 *
 * @author Elmar Baumann
 */
public final class TransferableObject implements Transferable {
    private final Object       data;
    private final DataFlavor[] dataFlavors;

    /**
     * Creates a new instance of this class.
     *
     * @param data        data object returned by
     *               {@link #getTransferData(java.awt.datatransfer.DataFlavor)}.
     *                    The object has to implement the Interface
     *                    {@link Serializable}!
     * @param dataFlavors data flavors supported data flavors of that object
     *                    This class creates too {@link DataFlavor} with
     *                    the class of that object as representation class and
     *                    <code>application/x-java-serialized-object</code> as
     *                    MIME type
     */
    public TransferableObject(Object data, DataFlavor... dataFlavors) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }

        if (dataFlavors == null) {
            throw new NullPointerException("dataFlavors == null");
        }

        this.data        = data;
        this.dataFlavors = new DataFlavor[dataFlavors.length + 1];
        System.arraycopy(dataFlavors, 0, this.dataFlavors, 0,
                         dataFlavors.length);
        this.dataFlavors[dataFlavors.length] = new DataFlavor(data.getClass(),
                null);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(dataFlavors, dataFlavors.length);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor == null) {
            throw new NullPointerException("flavor == null");
        }

        for (DataFlavor dataFlavor : dataFlavors) {
            if (flavor.equals(dataFlavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the data object set via constructor.
     *
     * @param  flavor data flavor
     * @return        data object
     */
    @Override
    public Object getTransferData(DataFlavor flavor) {
        return data;
    }
}
