package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-14
 */
public final class Flavors {

    private static final DataFlavor FILE_LIST_FLAVOR =
            DataFlavor.javaFileListFlavor;
    private static final DataFlavor URI_LIST_FLAVOR =
            TransferUtil.getUriListFlavor();
    private static final DataFlavor[] FILE_FLAVORS;

    static {
        FILE_FLAVORS = new DataFlavor[]{FILE_LIST_FLAVOR, URI_LIST_FLAVOR};
    }

    public static boolean filesTransfered(Transferable transferable) {
        return TransferUtil.isADataFlavorSupported(transferable, FILE_FLAVORS);
    }

    private Flavors() {
    }
}
