package de.elmar_baumann.imv.dnd;

import de.elmar_baumann.lib.dnd.TransferTools;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * Transfer handler for a <code>JList</code> which handles
 * <code>java.awt.datatransfer.DataFlavor.stringFlavor</code>.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public class ListTransferHandlerThumbnailIndices extends TransferHandler {

    private int[] indices = null;

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return TransferTools.getSelectedItemStringsTransferable((JList) c);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport info) {
        if (!info.isDrop()) {
            return false;
        }

        JList list = (JList) info.getComponent();
        DefaultListModel listModel = (DefaultListModel) list.getModel();
        JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
        int index = dl.getIndex();
        boolean insert = dl.isInsert();

        Transferable t = info.getTransferable();
        List<Integer> data;
        try {
            data = (List<Integer>) t.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            return false;
        }
        System.out.println(data);

        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        JList source = (JList) c;
        DefaultListModel listModel = (DefaultListModel) source.getModel();

        if (action == TransferHandler.MOVE) {
            for (int i = indices.length - 1; i >= 0; i--) {
                listModel.remove(indices[i]);
            }
        }

        indices = null;
    }
}

