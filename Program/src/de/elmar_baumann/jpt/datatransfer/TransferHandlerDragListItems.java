/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.datatransfer;

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
