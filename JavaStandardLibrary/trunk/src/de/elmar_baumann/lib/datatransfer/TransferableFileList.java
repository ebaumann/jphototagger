package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public final class TransferableFileList implements Transferable {

    private static final DataFlavor FILE_LIST_FLAVOR =
            DataFlavor.javaFileListFlavor;
    private static final DataFlavor URI_LIST_FLAVOR =
            TransferUtil.getUriListFlavor();
    private static final DataFlavor[] FLAVORS =
            new DataFlavor[]{FILE_LIST_FLAVOR, URI_LIST_FLAVOR};
    private final List files;
    private String fileUris;

    public TransferableFileList(File[] files) {
        this.files = Arrays.asList(files);
        createUriList(files);
    }

    private void createUriList(File[] files) {
        StringBuffer buffer = new StringBuffer();
        for (File file : files) {
            buffer.append("file://" + file.getAbsolutePath() + "\r\n"); // NOI18N
        }
        fileUris = buffer.toString();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (FLAVORS);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FILE_LIST_FLAVOR) || flavor.equals(URI_LIST_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws
            UnsupportedFlavorException, IOException {
        if (flavor.equals(FILE_LIST_FLAVOR)) {
            return files;
        } else if (flavor.equals(URI_LIST_FLAVOR)) {
            return fileUris;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
