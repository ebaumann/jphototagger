package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.StringTokenizer;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

/**
 * Imports into an {@link JTextField} and {@link JTextArea} strings exported via
 * a {@link TransferHandlerDragListItemsString}.
 * 
 * When multiple items exported, only the first will be inserted.
 * 
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropEditItemsString extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }

        Component c = transferSupport.getComponent();

        Transferable t = transferSupport.getTransferable();
        String data;
        try {
            data = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            return false;
        }

        JTextField textField = null;
        JTextArea textArea = null;
        if (c instanceof JTextField) {
            textField = (JTextField) c;
        } else if (c instanceof JTextArea) {
            textArea = (JTextArea) c;
        }
        if (textArea == null && textField == null) return false;

        StringTokenizer tokenizer = new StringTokenizer(
                data, TransferHandlerDragListItemsString.DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.equals(TransferHandlerDragListItemsString.PREFIX)) {
                if (textField != null) {
                    textField.setText(token);
                } else if (textArea != null) {
                    textArea.setText(token);
                }
                return true;
            }
        }
        return true;
    }
}
