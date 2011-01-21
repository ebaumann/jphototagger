package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferUtil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

/**
 * Data flavors supported in JPhotoTagger.
 *
 * @author Elmar Baumann
 */
public final class Flavor {

    /**
     * A reference to {@link DataFlavor#javaFileListFlavor}
     */
    public static final DataFlavor FILE_LIST_FLAVOR =
        DataFlavor.javaFileListFlavor;

    /**
     * A reference to {@link TransferUtil#getUriListFlavor()}
     */
    public static final DataFlavor URI_LIST = TransferUtil.getUriListFlavor();

    /**
     * A {@link java.util.Collection} of {@link java.io.File}s: The selected
     * image files and their XMP sidecar files
     */
    public static final DataFlavor THUMBNAILS_PANEL =
        new DataFlavor(THUMBNAILS_PANEL.class, null);

    /**
     * The selected list values as an array of {@link Object}s
     */
    public static final DataFlavor METADATA_TEMPLATES =
        new DataFlavor(METADATA_TEMPLATES.class, null);

    /**
     * All metadata data flavors such as keywords or column data
     */
    private static final List<DataFlavor> METADATA_FLAVORS =
        new ArrayList<DataFlavor>();

    /**
     * A {@link java.util.Collection} of
     * {@link javax.swing.tree.DefaultMutableTreeNode}s: The selected tree nodes
     */
    public static final DataFlavor KEYWORDS_TREE =
        new DataFlavor(KEYWORDS_TREE.class, null);

    /**
     * The selected list values as an array of {@link Object}s
     */
    public static final DataFlavor KEYWORDS_LIST =
        new DataFlavor(KEYWORDS_LIST.class, null);

    /**
     * A {@link java.util.Collection} of {@link java.io.File}s: The selected
     * image files and their XMP sidecar files
     */
    public static final DataFlavor IMAGE_COLLECTION =
        new DataFlavor(IMAGE_COLLECTION.class, null);

    /**
     * Contains {@link #URI_LIST} and {@link #FILE_LIST_FLAVOR }
     */
    static final DataFlavor[] FILE_FLAVORS = new DataFlavor[] {
                                                 FILE_LIST_FLAVOR,
            URI_LIST };

    /**
     * A {@link java.util.Collection} of
     * {@link org.jphototagger.program.data.ColumnData} objects
     */
    public static final DataFlavor COLUMN_DATA =
        new DataFlavor(COLUMN_DATA.class, null);

    static {
        METADATA_FLAVORS.add(KEYWORDS_TREE);
        METADATA_FLAVORS.add(KEYWORDS_LIST);
        METADATA_FLAVORS.add(METADATA_TEMPLATES);
        METADATA_FLAVORS.add(COLUMN_DATA);
    }

    private Flavor() {}

    /**
     * Returns whether metadata is transferred, e.g. keywords or column data
     *
     * @param  t tranferable
     * @return   true if metadata is transferred
     */
    public static boolean isMetadataTransferred(Transferable t) {
        if (t == null) {
            throw new NullPointerException("t == null");
        }

        for (DataFlavor flavor : t.getTransferDataFlavors()) {
            if (METADATA_FLAVORS.contains(flavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether a transferable supports the data flavor
     * {@link #FILE_FLAVORS}.
     *
     * @param  transferable transferable
     * @return              true if the transferable supports the data flavor
     *                      {@link #FILE_FLAVORS} is supported
     */
    public static boolean hasFiles(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        return TransferUtil.isADataFlavorSupported(transferable, FILE_FLAVORS);
    }

    /**
     * Returns whether the data flavor {@link #KEYWORDS_LIST}
     * is supported.
     *
     * @param  support transfer support
     * @return                 true if the data flavor
     *                         {@link #KEYWORDS_LIST} is supported
     */
    public static boolean hasKeywordsFromList(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        return support.isDataFlavorSupported(KEYWORDS_LIST);
    }

    public static boolean hasMetadataTemplate(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        return support.isDataFlavorSupported(METADATA_TEMPLATES);
    }

    public static boolean hasColumnData(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        return support.isDataFlavorSupported(COLUMN_DATA);
    }

    /**
     * Returns whether the data flavor {@link #KEYWORDS_TREE}
     * is supported.
     *
     * @param  support transfer support
     * @return         true if the data flavor {@link #KEYWORDS_TREE} is
     *                 supported
     */
    public static boolean hasKeywordsFromTree(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        return support.isDataFlavorSupported(KEYWORDS_TREE);
    }

    private final class COLUMN_DATA {
        // Empty
    }


    private final class IMAGE_COLLECTION {
        // Empty
    }


    private final class KEYWORDS_LIST {
        // Empty
    }


    private final class KEYWORDS_TREE {
        // Empty
    }


    private final class METADATA_TEMPLATES {
        // Empty
    }


    private final class THUMBNAILS_PANEL {
        // Empty
    }
}
