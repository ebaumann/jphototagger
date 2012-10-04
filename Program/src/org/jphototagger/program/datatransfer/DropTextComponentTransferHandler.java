package org.jphototagger.program.datatransfer;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.program.module.keywords.KeywordsUtil;

/**
 * Imports into an {@code JTextField} and {@code JTextArea} strings exported via
 * a {@code TransferHandlerDragListItems} or from a
 * {@code DefaultMutableTreeNode} with an {@code Keyword} as user
 * object when it's data flavor is {@code DataFlavor#stringFlavor}.
 *
 * When multiple items exported, only the first will be inserted.
 *
 * Does <em>not</em> support moving data.
 *
 * @author Elmar Baumann
 */
public final class DropTextComponentTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.stringFlavor)
                || Flavor.isMetadataTransferred(support.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTextComponent) {
            return new StringSelection(((JTextComponent) c).getSelectedText());
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        Component component = support.getComponent();
        String string = null;
        Transferable transferable = support.getTransferable();

        if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            string = DataTransferSupport.getString(transferable);
        } else if (Flavor.hasKeywordsFromList(support)) {
            string = getStrings(DataTransferSupport.getKeywords(transferable));
        } else if (Flavor.hasKeywordsFromTree(support)) {
            string = getStrings(DataTransferSupport.getKeywordNodes(transferable));
        } else if (Flavor.hasMetaDataValue(support)) {
            string = DataTransferSupport.getStringFromMetaDataValueData(DataTransferSupport.getMetaDataValueData(transferable));
        } else if (Flavor.hasMetadataTemplate(support)) {
            MetadataTemplateSupport.setTemplate(support);

            return true;
        }

        if (string == null) {
            return false;
        }

        if (component instanceof JTextArea) {
            setText((JTextArea) component, string);
        } else if (component instanceof JTextField) {
            setText((JTextField) component, string);
        } else {
            return false;
        }

        return true;
    }

    private String getStrings(Object[] array) {
        if ((array == null) || (array.length == 0) || (array[0] == null)) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (Object o : array) {
            sb.append(index == 0 ? "" : ";");
            sb.append(o.toString());
            index++;
        }

        return sb.toString();
    }

    private String getStrings(List<DefaultMutableTreeNode> nodes) {
        if (nodes.size() <= 0) {
            return null;
        }

        List<String> keywords = new ArrayList<String>();

        for (DefaultMutableTreeNode node : nodes) {
            keywords.addAll(KeywordsUtil.getKeywordStrings(node, true));
        }

        if (keywords.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (String keyword : keywords) {
            sb.append(index == 0 ? "" : ";");
            sb.append(keyword);
            index++;
        }

        return sb.toString();
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
