package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.TransferHandler.TransferSupport;

/**
 * Data flavors supported in this application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-14
 */
public final class Flavors {

    private final class CATEGORIES {
    }

    private final class HIERARCHICAL_KEYWORDS {
    }

    private final class IMAGE_COLLECTION {
    }

    private final class KEYWORDS {
    }

    private final class THUMBNAILS_PANEL {
    }
    /**
     * {@link DataFlavor.javaFileListFlavor}
     */
    public static final DataFlavor FILE_LIST_FLAVOR =
            DataFlavor.javaFileListFlavor;
    /**
     * {@link TransferUtil#getUriListFlavor()}
     */
    public static final DataFlavor URI_LIST_FLAVOR =
            TransferUtil.getUriListFlavor();
    public static final DataFlavor HIERARCHICAL_KEYWORDS_FLAVOR =
            new DataFlavor(HIERARCHICAL_KEYWORDS.class, null);
    public static final DataFlavor KEYWORDS_FLAVOR =
            new DataFlavor(KEYWORDS.class, null);
    public static final DataFlavor CATEGORIES_FLAVOR =
            new DataFlavor(CATEGORIES.class, null);
    public static final DataFlavor THUMBNAILS_PANEL_FLAVOR =
            new DataFlavor(THUMBNAILS_PANEL.class, null);
    public static final DataFlavor IMAGE_COLLECTION_FLAVOR =
            new DataFlavor(IMAGE_COLLECTION.class, null);
    /**
     * Contains {@link DataFlavor#javaFileListFlavor} and
     * {@link TransferUtil#getUriListFlavor()}.
     */
    public static final DataFlavor[] FILE_FLAVORS = new DataFlavor[]{
        FILE_LIST_FLAVOR, URI_LIST_FLAVOR};

    /**
     * Returns whether a transferable supports the data flavor
     * {@link #FILE_FLAVORS}.
     *
     * @param  transferable transferable
     * @return              true if the transferable supports the data flavor
     *                      {@link #FILE_FLAVORS} is supported
     */
    public static boolean hasFiles(Transferable transferable) {
        return TransferUtil.isADataFlavorSupported(transferable, FILE_FLAVORS);
    }

    /**
     * Returns whether the data flavor {@link #KEYWORDS_FLAVOR}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #KEYWORDS_FLAVOR} is supported
     */
    public static boolean hasKeywords(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(KEYWORDS_FLAVOR);
    }

    /**
     * Returns whether the data flavor {@link #CATEGORIES_FLAVOR}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #CATEGORIES_FLAVOR} is supported
     */
    public static boolean hasHierarchicalKeywords(
            TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(
                HIERARCHICAL_KEYWORDS_FLAVOR);
    }

    /**
     * Returns whether the data flavor {@link #HIERARCHICAL_KEYWORDS_FLAVOR}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #HIERARCHICAL_KEYWORDS_FLAVOR} is
     *                         supported
     */
    public static boolean hasCategories(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(CATEGORIES_FLAVOR);
    }

    private Flavors() {
    }
}
