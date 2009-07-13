package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsDialog;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Handles drops onto the {@link HierarchicalKeywordsDialog}'s tree.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/11
 */
public final class TransferHandlerTreeHierarchicalKeywords extends TransferHandler {

    private static final String PREFIX =
            "TransferHandlerTreeHierarchicalKeywords:";

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        Transferable transferable = transferSupport.getTransferable();
        if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        if (((JTree.DropLocation) transferSupport.getDropLocation()).getPath() ==
                null) {
            return false;
        }
        if (!TransferHandlerListKeywords.hasKeyword(transferable)) {
            return false;
        }
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.NONE;
    }

    @Override
    public boolean importData(TransferSupport transferSupport) {
        JTree.DropLocation dropLocation =
                (JTree.DropLocation) transferSupport.getDropLocation();
        Object o = dropLocation.getPath().getLastPathComponent();
        TreeModel model = HierarchicalKeywordsDialog.INSTANCE.getPanel().getTree().
                getModel();
        if (o instanceof DefaultMutableTreeNode &&
                model instanceof TreeModelHierarchicalKeywords) {
            ((TreeModelHierarchicalKeywords) model).addKeyword(
                    (DefaultMutableTreeNode) o,
                    TransferHandlerListKeywords.toKeyword(
                    transferSupport.getTransferable()));
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }
}
