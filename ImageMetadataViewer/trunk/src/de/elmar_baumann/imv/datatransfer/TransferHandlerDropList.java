package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into a {@link ListModel} of strings strings exported via a
 * {@link TransferHandlerDragListItemsString} or from a
 * {@link DefaultMutableTreeNode} with an {@link HierarchicalKeyword} as user
 * object when it's data flavor is {@link DataFlavor#stringFlavor}.
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
        try {
            Object o = transferSupport.getTransferable().getTransferData(
                    DataFlavor.stringFlavor);
            if (o instanceof String) {
                return TransferHandlerDragListItemsString.startsWithPrefix(
                        (String) o);
            } else if (o instanceof DefaultMutableTreeNode) {
                return ((DefaultMutableTreeNode) o).getUserObject() instanceof HierarchicalKeyword;
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
        }
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) return false;

        Component c = transferSupport.getComponent();
        assert c instanceof JList : "Not a JList: " + c; // NOI18N

        ListModel lm = ((JList) transferSupport.getComponent()).getModel();
        assert lm instanceof DefaultListModel : "Not a DefaultListModel: " + lm; // NOI18N
        DefaultListModel listModel = (DefaultListModel) lm;

        Transferable t = transferSupport.getTransferable();
        String string = null;
        DefaultMutableTreeNode node = null;
        try {
            Object transferData = t.getTransferData(DataFlavor.stringFlavor);
            if (transferData instanceof String) {
                string = (String) transferData;
            } else if (transferData instanceof DefaultMutableTreeNode) {
                node = (DefaultMutableTreeNode) transferData;
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            return false;
        }

        if (node != null) {
            importKeywords(node, listModel);
        } else if (string != null) {
            importString(string, listModel);
        }
        return true;
    }

    public void importString(String string, DefaultListModel listModel) {
        StringTokenizer tokenizer =
                new StringTokenizer(string,
                TransferHandlerDragListItemsString.DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!TransferHandlerDragListItemsString.isPrefix(token) &&
                    !listModel.contains(token)) {
                listModel.addElement(token);
            }
        }
    }

    private void importKeywords(
            DefaultMutableTreeNode node, DefaultListModel listModel) {
        for (String keyword :
                HierarchicalKeywordsHelper.getKeywordStrings(node, true)) {
            if (!listModel.contains(keyword)) {
                listModel.addElement(keyword);
            }
        }
    }
}
