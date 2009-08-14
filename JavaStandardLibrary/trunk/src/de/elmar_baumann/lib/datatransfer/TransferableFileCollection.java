package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Transferable with a collection of files.
 *
 * Supports {@link DataFlavor#javaFileListFlavor} and
 * {@link TransferUtil#getUriListFlavor()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class TransferableFileCollection implements Transferable {

    private static final DataFlavor FILE_LIST_FLAVOR =
            DataFlavor.javaFileListFlavor;
    private static final DataFlavor URI_LIST_FLAVOR =
            TransferUtil.getUriListFlavor();
    private static final DataFlavor[] FLAVORS;
    private static final String FILE_PROTOCOL = "file://"; // NOI18N
    private static final String TOKEN_DELIMITER = "\r\n"; // NOI18N
    private final Collection<File> files;
    private String fileUris;

    static {
        FLAVORS = new DataFlavor[]{FILE_LIST_FLAVOR, URI_LIST_FLAVOR};
    }

    public TransferableFileCollection(Collection<? extends File> files) {
        this.files = new ArrayList<File>(files);
        createUriList();
    }

    private void createUriList() {
        StringBuilder sb = new StringBuilder();
        for (File file : files) {
            sb.append(FILE_PROTOCOL + file.getAbsolutePath() + TOKEN_DELIMITER);
        }
        fileUris = sb.toString();
    }

    /**
     * Returns the supported flavors.
     *
     * @return {@link DataFlavor#javaFileListFlavor} and
     *         {@link TransferUtil#getUriListFlavor()}
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return FLAVORS.clone();
    }

    /**
     * Returns whether a data flavor is supported.
     *
     * @param  flavor flavor
     * @return        true, if the flavor is
     *                {@link DataFlavor#javaFileListFlavor} or
     *                {@link TransferUtil#getUriListFlavor()}
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FILE_LIST_FLAVOR) || flavor.equals(URI_LIST_FLAVOR);
    }

    /**
     * Returns a list of files or a token string with file URIs.
     *
     * @param  flavor {@link DataFlavor#javaFileListFlavor} if a {@link List} of
     *                {@link File} shall be returned or
     *                {@link TransferUtil#getUriListFlavor()} if a file URI
     *                token string shall be returned: The string contains names
     *                of files, every name is prepended by <code>file://</code>,
     *                the names are delimited by "\r\n"
     * @return
     * @throws        UnsupportedFlavorException if the flavor is not supported
     */
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {

        if (flavor.equals(FILE_LIST_FLAVOR)) {
            return new ArrayList<File>(files);
        } else if (flavor.equals(URI_LIST_FLAVOR)) {
            return fileUris;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
