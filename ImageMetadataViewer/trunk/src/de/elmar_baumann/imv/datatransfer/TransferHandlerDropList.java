package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into a {@link DefaultListModel} of strings strings exported via a
 * {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link HierarchicalKeyword} as user
 * object.
 * 
 * The list model has to be <em>of the type {@link DefaultListModel}</em> and
 * it's elements {@link String}s.
 * 
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropList extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return Flavors.hasKeywords(transferSupport) ||
                Flavors.hasHierarchicalKeywords(transferSupport) ||
                Flavors.hasCategories(transferSupport);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) return false;

        JList list = (JList) transferSupport.getComponent();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        Transferable transferable = transferSupport.getTransferable();

        if (Flavors.hasKeywords(transferSupport)) {
            return importKeywords(transferable, listModel);
        } else if (Flavors.hasCategories(transferSupport)) {
            return importCategories(transferable, listModel);
        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
            return importHierarchicalKeywords(
                    listModel, transferSupport.getTransferable());
        }
        return false;
    }

    private boolean importKeywords(
            Transferable transferable, DefaultListModel listModel) {
        Object[] keywords = Support.getKeywords(transferable);
        if (keywords == null) return false;
        return importStringArray(listModel, keywords);
    }

    private boolean importCategories(
            Transferable transferable, DefaultListModel listModel) {
        Object[] categories = Support.getCategories(transferable);
        if (categories == null) return false;
        return importStringArray(listModel, categories);
    }

    private boolean importStringArray(DefaultListModel listModel, Object[] array) {
        for (Object o : array) {
            listModel.addElement(o);
        }
        return true;
    }

    private boolean importHierarchicalKeywords(
            DefaultListModel listModel, Transferable transferable) {
        List<DefaultMutableTreeNode> nodes =
                Support.getHierarchicalKeywordsNodes(transferable);
        for (DefaultMutableTreeNode node : nodes) {
            importHierarchicalKeywords(node, listModel);
        }
        return true;
    }

    private void importHierarchicalKeywords(
            DefaultMutableTreeNode node, DefaultListModel listModel) {
        for (String keyword :
                HierarchicalKeywordsHelper.getKeywordStrings(node, true)) {
            if (!listModel.contains(keyword)) {
                listModel.addElement(keyword);
            }
        }
    }
}
