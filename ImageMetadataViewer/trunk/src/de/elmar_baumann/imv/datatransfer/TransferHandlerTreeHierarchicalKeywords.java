package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Handles drags and drops for the {@link HierarchicalKeywordsDialog}'s tree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TransferHandlerTreeHierarchicalKeywords extends TransferHandler {

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        Transferable transferable = transferSupport.getTransferable();
        if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        if (((JTree.DropLocation) transferSupport.getDropLocation()).getPath() ==
                null) {
            return false;
        }
        if (!TransferHandlerListKeywords.hasKeyword(transferable) &&
                !hasHierarchicalKeyword(transferable)) {
            return false;
        }
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTree) {
            JTree tree = (JTree) c;
            TreePath selPath = tree.getSelectionPath();
            if (selPath != null) {
                Object node = selPath.getLastPathComponent();
                if (node instanceof DefaultMutableTreeNode) {
                    Object userObject =
                            ((DefaultMutableTreeNode) node).getUserObject();
                    if (userObject instanceof HierarchicalKeyword) {
                        return new TransferableObject(node);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        JTree.DropLocation dropLocation =
                (JTree.DropLocation) transferSupport.getDropLocation();
        Object dropObject = dropLocation.getPath().getLastPathComponent();
        TreeModel model = HierarchicalKeywordsDialog.INSTANCE.getPanel().getTree().
                getModel();
        if (dropObject instanceof DefaultMutableTreeNode &&
                model instanceof TreeModelHierarchicalKeywords) {
            DefaultMutableTreeNode dropNode =
                    (DefaultMutableTreeNode) dropObject;
            TreeModelHierarchicalKeywords tm =
                    (TreeModelHierarchicalKeywords) model;
            if (isDragFromListKeywords(transferSupport.getTransferable())) {
                addKeyword(
                        tm, (DefaultMutableTreeNode) dropObject, transferSupport);
            } else {
                moveKeyword(transferSupport, tm, dropNode);
            }
        }
        return true;
    }

    private void addKeyword(TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode node, TransferSupport transferSupport) {
        treeModel.addKeyword(node,
                TransferHandlerListKeywords.toKeyword(transferSupport.
                getTransferable()));
    }

    private void moveKeyword(TransferSupport transferSupport,
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode dropNode) {
        try {
            Object o = transferSupport.getTransferable().
                    getTransferData(DataFlavor.stringFlavor);
            if (o instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) o;
                Object userObject = sourceNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    treeModel.move(sourceNode, dropNode,
                            (HierarchicalKeyword) userObject);
                }
            }
        } catch (Exception ex) {
            AppLog.logWarning(TransferHandlerTreeHierarchicalKeywords.class, ex);
        }
    }

    private boolean isDragFromListKeywords(Transferable transferable) {
        try {
            Object o = transferable.getTransferData(DataFlavor.stringFlavor);
            return o instanceof String;
        } catch (Exception ex) {
            AppLog.logWarning(TransferHandlerTreeHierarchicalKeywords.class, ex);
        }
        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore
    }

    private boolean hasHierarchicalKeyword(Transferable transferable) {
        try {
            Object o =
                    transferable.getTransferData(DataFlavor.stringFlavor);
            if (o instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) o).getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    return true;
                }
            }
        } catch (Exception ex) {
            AppLog.logWarning(TransferHandlerTreeHierarchicalKeywords.class, ex);
        }
        return false;
    }
}
