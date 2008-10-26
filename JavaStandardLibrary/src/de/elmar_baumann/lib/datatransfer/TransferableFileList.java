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
public class TransferableFileList  implements Transferable {
 
	private static final DataFlavor dataFlavor = DataFlavor.javaFileListFlavor;
	private static final DataFlavor[] flavors = new DataFlavor[]{dataFlavor};
	private List fileList;
 
	public TransferableFileList(final File[] files) {
		fileList = Arrays.asList(files);
	}
	
    @Override
	public DataFlavor[] getTransferDataFlavors() {
		return(flavors);
	}
 
    @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (flavor.equals(dataFlavor)) {
			return(true);
		}
		return(false);
	}
 
    @Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(dataFlavor)) {
			return(fileList);
		} 
		else {
			throw new UnsupportedFlavorException(flavor);
		}
	}
}