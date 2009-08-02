package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

/**
 * Imports into a {@link ListModel} of strings strings exported via a
 * {@link TransferHandlerDragListItemsString}.
 * 
 * The list model has to be <em>of the type {@link DefaultListModel}</em> and
 * it's elements {@link String}s.
 * 
 * Does <em>not</em> support moving data.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-02
 */
public final class TransferHandlerDropListItemsString extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        try {
            Object o = transferSupport.getTransferable().getTransferData(
                    DataFlavor.stringFlavor);
            if (o instanceof String) {
                return ((String) o).startsWith(
                        TransferHandlerDragListItemsString.PREFIX);
            }
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
        }
        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }

        Component c = transferSupport.getComponent();
        assert c instanceof JList :
                "Component is not a JList but a " + c.getClass();

        ListModel lm = ((JList) transferSupport.getComponent()).getModel();
        assert lm instanceof DefaultListModel :
                "List model is not a DefaultListModel but a " + lm.getClass();
        DefaultListModel listModel = (DefaultListModel) lm;

        Transferable t = transferSupport.getTransferable();
        String data;
        try {
            data = (String) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            AppLog.logSevere(getClass(), ex);
            return false;
        }

        StringTokenizer tokenizer = new StringTokenizer(
                data, TransferHandlerDragListItemsString.DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            String item = tokenizer.nextToken();
            if (!item.equals(TransferHandlerDragListItemsString.PREFIX) &&
                    !listModel.contains(item)) {
                listModel.addElement(item);
            }
        }
        return true;
    }
}
