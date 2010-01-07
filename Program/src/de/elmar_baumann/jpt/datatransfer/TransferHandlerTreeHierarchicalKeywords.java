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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.HierarchicalKeywordTreeNodesClipboard;
import de.elmar_baumann.jpt.controller.hierarchicalkeywords.HierarchicalKeywordsTreePathExpander;
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import de.elmar_baumann.jpt.helper.HierarchicalKeywordsHelper;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.view.panels.KeywordsPanel;
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
 * Handles drags and drops for a {@link KeywordsPanel}'s tree.
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
            List<DefaultMutableTreeNode> selNodes = new ArrayList<DefaultMutableTreeNode>();
            for (TreePath selPath : selPaths) {
                Object node = selPath.getLastPathComponent();
                if (node instanceof DefaultMutableTreeNode) {
                    Object userObject = ((DefaultMutableTreeNode) node).getUserObject();
                    if (userObject instanceof HierarchicalKeyword) {
                        selNodes.add((DefaultMutableTreeNode) node);
                    }
                }
            }
            return new TransferableObject(selNodes, Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {

        DefaultMutableTreeNode dropNode = getDropNode(transferSupport);
        if (dropNode != null) {
            JTree tree = (JTree) transferSupport.getComponent();
            TreeModelHierarchicalKeywords tm = (TreeModelHierarchicalKeywords) tree.getModel();
            if (Flavors.hasKeywords(transferSupport)) {
                if (!checkImportSelection(transferSupport)) return false;
                addKeywords(tm, dropNode, transferSupport);
            } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
                if (!checkImportSelection(transferSupport)) return false;
                moveKeywords(transferSupport, tm, dropNode);
                HierarchicalKeywordTreeNodesClipboard.INSTANCE.empty();
            }
            HierarchicalKeywordsTreePathExpander.expand(dropNode);
        }
        return true;
    }

    private DefaultMutableTreeNode getDropNode(TransferSupport transferSupport) {

        if (transferSupport.isDrop()) {
            JTree.DropLocation dropLocation = (JTree.DropLocation) transferSupport.getDropLocation();
            Object             dropObject   = dropLocation.getPath().getLastPathComponent();

            return dropObject instanceof DefaultMutableTreeNode
                    ? (DefaultMutableTreeNode) dropObject
                    : null;
        }

        JTree    tree    = (JTree) transferSupport.getComponent();
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
            MessageDisplayer.error(
                    null,
                    "TransferHandlerTreeHierarchicalKeywords.Error.Import.Selection");
            return false;
        }
        return true;
    }

    private void addKeywords(
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode        node,
            TransferSupport               transferSupport) {

        Object[] keywords = TransferHandlerListKeywords.getKeywords(transferSupport.getTransferable());
        if (keywords == null) return;
        for (Object keyword : keywords) {
            treeModel.addKeyword(node, keyword.toString(), true);
        }
    }

    @SuppressWarnings("unchecked")
    public static void moveKeywords(
            TransferSupport               transferSupport,
            TreeModelHierarchicalKeywords treeModel,
            DefaultMutableTreeNode        dropNode
            ) {
        try {
            List<DefaultMutableTreeNode> sourceNodes = (List<DefaultMutableTreeNode>)
                    transferSupport.getTransferable().getTransferData(Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
            for (DefaultMutableTreeNode sourceNode : sourceNodes) {
                Object userObject = sourceNode.getUserObject();
                if (userObject instanceof HierarchicalKeyword) {
                    HierarchicalKeyword sourceKeyword = (HierarchicalKeyword) userObject;
                    HierarchicalKeyword srcCopy       = new HierarchicalKeyword(sourceKeyword);
                    treeModel.move(sourceNode, dropNode, sourceKeyword);
                    HierarchicalKeywordsHelper.moveInFiles(
                            HierarchicalKeywordsHelper.getParentKeywordNames(
                                    srcCopy, true), sourceKeyword); // sourceKeyword's new parent ID was set through treeModel.move()
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
