package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into an {@link JTextField} and {@link JTextArea} strings exported via
 * a {@link TransferHandlerDragListItems} or from a
 * {@link DefaultMutableTreeNode} with an {@link HierarchicalKeyword} as user
 * object when it's data flavor is {@link DataFlavor#stringFlavor}.
 * 
 * When multiple items exported, only the first will be inserted.
 * 
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropEdit extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                Flavors.hasCategories(transferSupport) || Flavors.
                hasHierarchicalKeywords(transferSupport) || Flavors.hasKeywords(
                transferSupport);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        Component c = transferSupport.getComponent();
        String string = null;
        Transferable transferable = transferSupport.getTransferable();
        if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            string = Support.getString(transferable);
        } else if (Flavors.hasCategories(transferSupport)) {
            string = getFirstString(Support.getCategories(transferable));
        } else if (Flavors.hasKeywords(transferSupport)) {
            string = getFirstString(Support.getKeywords(transferable));
        } else if (Flavors.hasHierarchicalKeywords(transferSupport)) {
            string = getFirstString(
                    Support.getHierarchicalKeywordsNode(transferable));
        }
        if (string == null) return false;
        if (c instanceof JTextArea) {
            setText((JTextArea) c, string);
        } else if (c instanceof JTextField) {
            setText((JTextField) c, string);
        } else {
            return false;
        }
        return true;
    }

    private String getFirstString(Object[] array) {
        if (array == null || array.length == 0 || array[0] == null) return null;
        return array[0].toString();
    }

    private String getFirstString(DefaultMutableTreeNode node) {
        if (node == null) return null;
        List<String> keywords =
                HierarchicalKeywordsHelper.getKeywordStrings(node, true);
        if (keywords.size() == 0) return null;
        return keywords.get(0);
    }

    private void setText(JTextArea textArea, String text) {
        if (textArea.getSelectedText() == null) {
            textArea.setText(textArea.getText() + text);
        } else {
            textArea.setText(text);
        }
    }

    private void setText(JTextField textField, String text) {
        if (textField.getSelectedText() == null) {
            textField.setText(textField.getText() + text);
        } else {
            textField.setText(text);
        }
    }
}
