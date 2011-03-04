package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.ColumnData;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.XmpColumns;
import org.jphototagger.program.helper.MiscMetadataHelper;

import java.awt.datatransfer.Transferable;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final long serialVersionUID = -260820309332646425L;
    private static final List<Column> XMP_COLS = XmpColumns.get();

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        if (!Flavor.hasFiles(support.getTransferable())) {
            return false;
        }

        DefaultMutableTreeNode dropNode = TransferUtil.getTreeDropNode(support);

        return (dropNode != null) && MiscMetadataHelper.isParentUserObjectAColumnOf(dropNode, XMP_COLS);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        DefaultMutableTreeNode dropNode = TransferUtil.getTreeDropNode(support);

        if (dropNode == null) {
            return false;
        }

        Pair<Column, String> colValue = MiscMetadataHelper.getColValueFrom(dropNode);

        if (colValue == null) {
            return false;
        }

        List<File> imageFiles = Support.getImageFiles(support);
        String value = colValue.getSecond();

        if (!imageFiles.isEmpty() && confirmImport(value, imageFiles.size())) {
            ColumnData cd = new ColumnData(colValue.getFirst(), value);

            MiscMetadataHelper.saveToImageFiles(Collections.singletonList(cd), imageFiles);

            return true;
        }

        return false;
    }

    private boolean confirmImport(Object value, int fileCount) {
        return MessageDisplayer.confirmYesNo(null, "TransferHandlerMiscMetadataTree.Confirm.Import", value, fileCount);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree t = (JTree) c;
        TreePath[] selPaths = t.getSelectionPaths();

        if (selPaths != null) {
            List<ColumnData> colData = new ArrayList<ColumnData>(selPaths.length);

            for (TreePath selPath : selPaths) {
                Object lpc = selPath.getLastPathComponent();

                if (lpc instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) lpc;

                    if (MiscMetadataHelper.isParentUserObjectAColumnOf(node, XMP_COLS)) {
                        Object nodeUserObject = node.getUserObject();
                        TreeNode parent = node.getParent();
                        Object parentUserObject = ((DefaultMutableTreeNode) parent).getUserObject();

                        colData.add(new ColumnData((Column) parentUserObject, nodeUserObject));
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
