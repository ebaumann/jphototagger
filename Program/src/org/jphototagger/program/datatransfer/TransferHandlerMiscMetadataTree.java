package org.jphototagger.program.datatransfer;

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

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.MetaDataStringValue;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.program.helper.MiscMetadataHelper;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class TransferHandlerMiscMetadataTree extends TransferHandler {

    private static final long serialVersionUID = -260820309332646425L;
    private static final List<MetaDataValue> XMP_COLS = XmpMetaDataValues.get();

    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }

        if (!Flavor.hasFiles(support.getTransferable())) {
            return false;
        }

        DefaultMutableTreeNode dropNode = TransferUtil.getTreeDropNode(support);

        return (dropNode != null) && MiscMetadataHelper.isParentUserObjectAMetaDataValue(dropNode, XMP_COLS);
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

        MetaDataStringValue colValue = MiscMetadataHelper.getColValueFrom(dropNode);

        if (colValue == null) {
            return false;
        }

        List<File> imageFiles = Support.getImageFiles(support);
        String value = colValue.getValue();

        if (!imageFiles.isEmpty() && confirmImport(value, imageFiles.size())) {
            MetaDataValueData cd = new MetaDataValueData(colValue.getMetaDataValue(), value);

            MiscMetadataHelper.saveToImageFiles(Collections.singletonList(cd), imageFiles);

            return true;
        }

        return false;
    }

    private boolean confirmImport(Object value, int fileCount) {
        String message = Bundle.getString(TransferHandlerMiscMetadataTree.class, "TransferHandlerMiscMetadataTree.Confirm.Import", value, fileCount);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree t = (JTree) c;
        TreePath[] selPaths = t.getSelectionPaths();

        if (selPaths != null) {
            List<MetaDataValueData> colData = new ArrayList<MetaDataValueData>(selPaths.length);

            for (TreePath selPath : selPaths) {
                Object lpc = selPath.getLastPathComponent();

                if (lpc instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) lpc;

                    if (MiscMetadataHelper.isParentUserObjectAMetaDataValue(node, XMP_COLS)) {
                        Object nodeUserObject = node.getUserObject();
                        TreeNode parent = node.getParent();
                        Object parentUserObject = ((DefaultMutableTreeNode) parent).getUserObject();

                        colData.add(new MetaDataValueData((MetaDataValue) parentUserObject, nodeUserObject));
                    }
                }
            }

            if (!colData.isEmpty()) {
                return new TransferableObject(colData, Flavor.META_DATA_VALUE);
            }
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }
}
