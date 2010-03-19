/*
 * @(#)TransferHandlerDragListItems.java    Created on 2009-08-02
 *
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

package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.lib.datatransfer.TransferableObject;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Creates a {@link TransferableObject} with selected list items as object array.
 *
 * @author  Elmar Baumann
 */
public final class TransferHandlerDragListItems extends TransferHandler {
    private static final long  serialVersionUID = 2228155163708066205L;
    private final DataFlavor[] dataFlavors;

    public TransferHandlerDragListItems(DataFlavor... dataFlavors) {
        this.dataFlavors = new DataFlavor[dataFlavors.length];
        System.arraycopy(dataFlavors, 0, this.dataFlavors, 0,
                         dataFlavors.length);
    }

    /**
     * Returns all selected items.
     *
     * @param  c component
     * @return   transferrable
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        return new TransferableObject(((JList) c).getSelectedValues(),
                                      dataFlavors);
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
