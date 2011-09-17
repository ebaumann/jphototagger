package org.jphototagger.program.datatransfer;

import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.lib.datatransfer.TransferableObject;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.controller.keywords.tree.KeywordTreeNodesClipboard;
import org.jphototagger.program.controller.keywords.tree.KeywordsTreePathExpander;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.helper.MiscMetadataHelper;
import org.jphototagger.program.model.KeywordsTreeModel;

/**
 * Handles drags and drops for a {@code KeywordsPanel}'s tree.
 *
 * @author Elmar Baumann
 */
public final class KeywordsTreeTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1714818504305178611L;

    @Override
    public boolean canImport(TransferSupport support) {
        return (Flavor.hasKeywordsFromList(support) || Flavor.hasKeywordsFromTree(support)
                || Flavor.hasFiles(support.getTransferable()));
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
    public boolean importData(TransferSupport support) {
        DefaultMutableTreeNode dropNode = TransferUtil.getTreeDropNode(support);

        if (dropNode != null) {
            KeywordsTreeModel model = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);

            if (Flavor.hasKeywordsFromList(support)) {
                addKeywords(model, dropNode, support);
            } else if (Flavor.hasKeywordsFromTree(support)) {
                moveKeywords(support, model, dropNode);
                KeywordTreeNodesClipboard.INSTANCE.empty();
            } else if (Flavor.hasFiles(support.getTransferable())) {
                return addKeywordsToFiles(support);
            }

            KeywordsTreePathExpander.expand((JTree) support.getComponent(), dropNode);
        }

        return true;
    }

    private static boolean checkSelCount(int selCount) {
        if (selCount != 1) {
            String message = Bundle.getString(KeywordsTreeTransferHandler.class, "KeywordsTreeTransferHandler.Error.Import.Selection");
            MessageDisplayer.error(null, message);

            return false;
        }

        return true;
    }

    private void addKeywords(KeywordsTreeModel treeModel, DefaultMutableTreeNode node, TransferSupport support) {
        Object[] keywords = KeywordsListTransferHandler.getKeywords(support.getTransferable());

        if (keywords == null) {
            return;
        }

        for (Object keyword : keywords) {
            treeModel.insert(node, keyword.toString(), true, true);
        }
    }

    private boolean addKeywordsToFiles(TransferSupport support) {
        DefaultMutableTreeNode dropNode = TransferUtil.getTreeDropNode(support);

        if (dropNode == null) {
            return false;
        }

        List<Keyword> keywords = KeywordsHelper.getKeywords(dropNode, true);

        if (keywords.isEmpty()) {
            return false;
        }

        List<MetaDataValueData> cd = new ArrayList<MetaDataValueData>(keywords.size());

        for (Keyword keyword : keywords) {
            cd.add(new MetaDataValueData(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, keyword.getName()));
        }

        List<File> imageFiles = DataTransferSupport.getImageFiles(support);
        int fileCount = imageFiles.size();

        if ((fileCount > 0) && confirmImport(fileCount)) {
            MiscMetadataHelper.saveToImageFiles(cd, imageFiles);

            return true;
        }

        return false;
    }

    private boolean confirmImport(int fileCount) {
        String message = Bundle.getString(KeywordsTreeTransferHandler.class, "KeywordsTransferHandler.Confirm.Import", fileCount);

        return MessageDisplayer.confirmYesNo(null, message);
    }

    @SuppressWarnings("unchecked")
    public static void moveKeywords(TransferSupport support, final KeywordsTreeModel treeModel,
            final DefaultMutableTreeNode dropNode) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        if (treeModel == null) {
            throw new NullPointerException("treeModel == null");
        }

        if (dropNode == null) {
            throw new NullPointerException("dropNode == null");
        }

        try {
            List<DefaultMutableTreeNode> sourceNodes =
                    (List<DefaultMutableTreeNode>) support.getTransferable().getTransferData(Flavor.KEYWORDS_TREE);

            if (!checkSelCount(sourceNodes.size())) {
                return;
            }

            for (final DefaultMutableTreeNode sourceNode : sourceNodes) {
                Object userObject = sourceNode.getUserObject();

                if (userObject instanceof Keyword) {
                    if (sourceNode != dropNode) {
                        final Keyword sourceKeyword = (Keyword) userObject;

                        EventQueueUtil.invokeInDispatchThread(new Runnable() {

                            @Override
                            public void run() {
                                treeModel.move(sourceNode, dropNode, sourceKeyword);
                            }
                        });
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(KeywordsTreeTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        // ignore
    }
}
