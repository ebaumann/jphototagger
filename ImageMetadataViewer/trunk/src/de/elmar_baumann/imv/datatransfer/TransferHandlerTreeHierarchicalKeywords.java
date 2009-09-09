package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.HierarchicalKeywordsTreePathExpander;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.datatransfer.TransferableObject;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
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
        return Flavors.hasKeywords(transferSupport) &&
                ((JTree.DropLocation) transferSupport.getDropLocation()).getPath() !=
                null;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath selPath = tree.getSelectionPath();
        if (selPath != null) {
            Object node = selPath.getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode) {
                Object userObject =
                        ((DefaultMutableTreeNode) node).getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    return new TransferableObject(
                            node, Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
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
        if (!transferSupport.isDrop()) return false;
        JTree.DropLocation dropLocation =
                (JTree.DropLocation) transferSupport.getDropLocation();
        Object dropObject = dropLocation.getPath().getLastPathComponent();
        JTree tree = ((JTree) transferSupport.getComponent());
        TreeModel model = tree.getModel();
        if (dropObject instanceof DefaultMutableTreeNode &&
                model instanceof TreeModelHierarchicalKeywords) {
            DefaultMutableTreeNode dropNode =
                    (DefaultMutableTreeNode) dropObject;
            TreeModelHierarchicalKeywords tm =
                    (TreeModelHierarchicalKeywords) model;
            if (Flavors.hasKeywords(transferSupport)) {
                addKeywords(tm, dropNode, transferSupport);
            } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
                moveKeyword(transferSupport, tm, dropNode);
            }
            HierarchicalKeywordsTreePathExpander.expand(dropNode);
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

    private void moveKeyword(
            TransferSupport transferSupport,
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode dropNode) {
        try {
            DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) transferSupport.getTransferable().
                    getTransferData(Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
            Object userObject = sourceNode.getUserObject();
            if (userObject instanceof HierarchicalKeyword) {
                treeModel.move(
                        sourceNode, dropNode, (HierarchicalKeyword) userObject);
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
