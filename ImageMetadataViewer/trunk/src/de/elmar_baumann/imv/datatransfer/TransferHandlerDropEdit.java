package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.helper.HierarchicalKeywordsHelper;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Imports into an {@link JTextField} and {@link JTextArea} strings exported via
 * a {@link TransferHandlerDragListItemsString} or from a
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
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        Component c = transferSupport.getComponent();
        Transferable t = transferSupport.getTransferable();
        DefaultMutableTreeNode node = null;
        String string = null;
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
        JTextArea textArea = null;
        JTextField textField = null;
        if (c instanceof JTextArea) {
            textArea = (JTextArea) c;
        } else if (c instanceof JTextField) {
            textField = (JTextField) c;
        }
        if (node != null) {
            importKeywords(node, textArea, textField);
        } else {
            importString(string, textArea, textField);
        }
        return true;
    }

    public void importString(
            String string, JTextArea textArea, JTextField textField) {
        if (textArea == null && textField == null) return;
        StringTokenizer tokenizer =
                new StringTokenizer(string,
                TransferHandlerDragListItemsString.DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!TransferHandlerDragListItemsString.isPrefix(token)) {
                if (textArea != null) {
                    setText(textArea, token);
                } else if (textField != null) {
                    setText(textField, token);
                }
                return;
            }
        }
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

    private void importKeywords(
            DefaultMutableTreeNode node,
            JTextArea textArea,
            JTextField textField) {
        List<String> keywords =
                HierarchicalKeywordsHelper.getKeywordStrings(node, true);
        if (keywords.size() <= 0) return;
        if (textArea != null) {
            setText(textArea, keywords.get(0));
        } else if (textField != null) {
            setText(textField, keywords.get(0));
        }
    }
}
