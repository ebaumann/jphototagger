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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.keywords.tree.KeywordTreeNodesClipboard;
import de.elmar_baumann.jpt.controller.keywords.tree.KeywordsTreePathExpander;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.helper.KeywordsHelper;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
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
public final class TransferHandlerKeywordsTree extends TransferHandler {

    private static final long serialVersionUID = 1714818504305178611L;

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        return (Flavor.hasKeywordsFromList(transferSupport) ||
                Flavor.hasKeywordsFromTree(transferSupport));
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
                    if (userObject instanceof Keyword) {
                        selNodes.add((DefaultMutableTreeNode) node);
                    }
                }
            }
            return new TransferableObject(selNodes, Flavor.KEYWORDS_TREE);
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
            TreeModelKeywords tm = (TreeModelKeywords) tree.getModel();
            if (Flavor.hasKeywordsFromList(transferSupport)) {
                if (!checkImportSelection(transferSupport)) return false;
                addKeywords(tm, dropNode, transferSupport);
            } else if (Flavor.hasKeywordsFromTree(transferSupport)) {
                if (!checkImportSelection(transferSupport)) return false;
                moveKeywords(transferSupport, tm, dropNode);
                KeywordTreeNodesClipboard.INSTANCE.empty();
            }
            KeywordsTreePathExpander.expand(dropNode);
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
            MessageDisplayer.error(null, "TransferHandlerKeywordsTree.Error.Import.Selection");
            return false;
        }
        return true;
    }

    private void addKeywords(
            TreeModelKeywords treeModel,
            DefaultMutableTreeNode        node,
            TransferSupport               transferSupport) {

        Object[] keywords = TransferHandlerKeywordsList.getKeywords(transferSupport.getTransferable());
        if (keywords == null) return;
        for (Object keyword : keywords) {
            treeModel.insert(node, keyword.toString(), true);
        }
    }

    @SuppressWarnings("unchecked")
    public static void moveKeywords(
            TransferSupport               transferSupport,
            TreeModelKeywords treeModel,
            DefaultMutableTreeNode        dropNode
            ) {
        try {
            List<DefaultMutableTreeNode> sourceNodes = (List<DefaultMutableTreeNode>)
                    transferSupport.getTransferable().getTransferData(Flavor.KEYWORDS_TREE);
            for (DefaultMutableTreeNode sourceNode : sourceNodes) {
                Object userObject = sourceNode.getUserObject();
                if (userObject instanceof Keyword) {
                    Keyword sourceKeyword = (Keyword) userObject;
                    Keyword srcCopy       = new Keyword(sourceKeyword);
                    treeModel.move(sourceNode, dropNode, sourceKeyword);
                    KeywordsHelper.moveInFiles(
                            KeywordsHelper.getParentKeywordNames(
                                    srcCopy, true), sourceKeyword); // sourceKeyword's new parent ID was set through treeModel.move()
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerKeywordsTree.class, ex);
        }
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore
    }
}
