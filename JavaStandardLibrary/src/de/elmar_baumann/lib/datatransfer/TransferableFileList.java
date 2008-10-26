package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class TransferableFileList implements Transferable {

    private static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
    private static DataFlavor uriListFlavor;
    private static DataFlavor[] flavors = new DataFlavor[]{fileListFlavor};
    private List files;
    private String fileUris;
    

    static {
        try {
            uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
            flavors = new DataFlavor[]{fileListFlavor, uriListFlavor};
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TransferableFileList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TransferableFileList(File[] files) {
        this.files = Arrays.asList(files);
        createUriList(files);
    }

    private void createUriList(File[] files) {
        StringBuffer buffer = new StringBuffer();
        for (File file : files) {
            buffer.append("file://" + file.getAbsolutePath() + "\r\n");
        }
        fileUris = buffer.toString();
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (flavors);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(fileListFlavor) || flavor.equals(uriListFlavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(fileListFlavor)) {
            return files;
        } else if (flavor.equals(uriListFlavor)) {
            return fileUris;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
