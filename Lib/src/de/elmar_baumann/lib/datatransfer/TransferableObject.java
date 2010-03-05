/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Transferable for objects of an arbitrary type.
 *
 * <em>The objects have to implement the Interface {@link Serializable}!</em>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class TransferableObject implements Transferable {

    private final Object data;
    private final DataFlavor[] dataFlavors;

    /**
     * Creates a new instance of this class.
     *
     * @param data        data object returned by
     *                    {@link #getTransferData(java.awt.datatransfer.DataFlavor)}.
     *                    The object has to implement the Interface
     *                    {@link Serializable}!
     * @param dataFlavors data flavors supported data flavors of that object
     *                    This class creates too {@link DataFlavor} with
     *                    the class of that object as representation class and
     *                    <code>application/x-java-serialized-object</code> as
     *                    MIME type
     */
    public TransferableObject(Object data, DataFlavor... dataFlavors) {
        this.data = data;
        this.dataFlavors = new DataFlavor[dataFlavors.length + 1];
        System.arraycopy(dataFlavors, 0, this.dataFlavors, 0, dataFlavors.length);
        this.dataFlavors[dataFlavors.length] =
                new DataFlavor(data.getClass(), null);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return Arrays.copyOf(dataFlavors, dataFlavors.length);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor dataFlavor : dataFlavors) {
            if (flavor.equals(dataFlavor)) return true;
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
