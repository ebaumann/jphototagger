/*
 * @(#)TransferHandlerMiscMetadataTree.java    Created on 2010-03-22
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

package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.program.data.ColumnData;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.helper.MiscMetadataHelper;

import java.awt.datatransfer.Transferable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TransferHandlerMiscMetadataTree extends TransferHandler {
    private static final long         serialVersionUID = -260820309332646425L;
    private static final List<Column> XMP_COLS         = XmpColumns.get();

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree      tree     = (JTree) c;
        TreePath[] selPaths = tree.getSelectionPaths();

        if (selPaths != null) {
            List<ColumnData> colData =
                new ArrayList<ColumnData>(selPaths.length);

            for (TreePath selPath : selPaths) {
                Object lpc = selPath.getLastPathComponent();

                if (lpc instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) lpc;

                    if (MiscMetadataHelper.isParentUserObjectAColumnOf(node,
                            XMP_COLS)) {
                        Object   nodeUserObject   = node.getUserObject();
                        TreeNode parent           = node.getParent();
                        Object   parentUserObject =
                            ((DefaultMutableTreeNode) parent).getUserObject();

                        colData.add(new ColumnData((Column) parentUserObject,
                                                   nodeUserObject));
                    }
                }
            }

            if (!colData.isEmpty()) {
                return new TransferableObject(colData, Flavor.COLUMN_DATA);
            }
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
