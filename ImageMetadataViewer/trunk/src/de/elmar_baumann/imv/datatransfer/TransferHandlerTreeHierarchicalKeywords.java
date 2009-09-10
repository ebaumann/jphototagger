package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.HierarchicalKeywordsTreePathExpander;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Handles drags and drops for a {@link HierarchicalKeywordsPanel}'s tree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-11
 */
public final class TransferHandlerTreeHierarchicalKeywords extends TransferHandler {

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        return (Flavors.hasKeywords(transferSupport) ||
                Flavors.hasHierarchicalKeywords(transferSupport));
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] selPaths = tree.getSelectionPaths();
        if (selPaths != null) {
            List<DefaultMutableTreeNode> selNodes =
                    new ArrayList<DefaultMutableTreeNode>();
            for (TreePath selPath : selPaths) {
                Object node = selPath.getLastPathComponent();
                if (node instanceof DefaultMutableTreeNode) {
                    Object userObject =
                            ((DefaultMutableTreeNode) node).getUserObject();
                    if (userObject instanceof HierarchicalKeyword) {
                        selNodes.add((DefaultMutableTreeNode) node);
                    }
                }
            }
            return new TransferableObject(
                    selNodes, Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        if (!checkImportSelection(transferSupport)) return false;
        DefaultMutableTreeNode dropNode = getDropNode(transferSupport);
        if (dropNode != null) {
            JTree tree = (JTree) transferSupport.getComponent();
            TreeModelHierarchicalKeywords tm =
                    (TreeModelHierarchicalKeywords) tree.getModel();
            if (Flavors.hasKeywords(transferSupport)) {
                addKeywords(tm, dropNode, transferSupport);
            } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
                moveKeywords(transferSupport, tm, dropNode);
            }
            HierarchicalKeywordsTreePathExpander.expand(dropNode);
        }
        return true;
    }

    private DefaultMutableTreeNode getDropNode(TransferSupport transferSupport) {
        if (transferSupport.isDrop()) {
            JTree.DropLocation dropLocation =
                    (JTree.DropLocation) transferSupport.getDropLocation();
            Object dropObject = dropLocation.getPath().getLastPathComponent();
            return dropObject instanceof DefaultMutableTreeNode
                    ? (DefaultMutableTreeNode) dropObject
                    : null;
        }
        JTree tree = (JTree) transferSupport.getComponent();
        TreePath selPath = tree.getSelectionPath();
        if (selPath != null) {
            Object o = selPath.getLastPathComponent();
            if (o instanceof DefaultMutableTreeNode) {
                return (DefaultMutableTreeNode) o;
            }
        }
        return null;
    }

    private boolean checkImportSelection(TransferSupport transferSupport) {
        JTree tree = (JTree) transferSupport.getComponent();
        if (tree.getSelectionCount() != 1) {
            MessageDisplayer.error(null,
                    "TransferHandlerTreeHierarchicalKeywords.Error.Import.Selection");
            return false;
        }
        return true;
    }

    private void addKeywords(
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode node,
            TransferSupport transferSupport) {

        Object[] keywords = TransferHandlerListKeywords.getKeywords(
                transferSupport.getTransferable());
        if (keywords == null) return;
        for (Object keyword : keywords) {
            treeModel.addKeyword(node, keyword.toString());
        }
    }

    public static void moveKeywords(
            TransferSupport transferSupport,
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode dropNode) {
        try {
            @SuppressWarnings("unchecked")
            List<DefaultMutableTreeNode> sourceNodes = (List<DefaultMutableTreeNode>) transferSupport.getTransferable().
                    getTransferData(Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
            for (DefaultMutableTreeNode sourceNode : sourceNodes) {
                Object userObject = sourceNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    treeModel.move(
                            sourceNode, dropNode,
                            (HierarchicalKeyword) userObject);
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(TransferHandlerTreeHierarchicalKeywords.class, ex);
        }
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore
    }
}
