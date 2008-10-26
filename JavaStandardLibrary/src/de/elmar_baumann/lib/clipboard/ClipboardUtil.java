package de.elmar_baumann.lib.clipboard;

import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.datatransfer.TransferableFileList;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ClipboardUtil {

    private static final DataFlavor stringFlavor = DataFlavor.stringFlavor;
    private static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
    private static final DataFlavor uriListFlavor = TransferUtil.getUriListFlavor();

    /**
     * Copies files to the system clipboard.
     * 
     * @param files  files
     * @param owner  owner of the clipboard, can be null
     */
    public static void copyToSystemClipboard(List<File> files, ClipboardOwner owner) {
        copyToClipboard(files, Toolkit.getDefaultToolkit().getSystemClipboard(), owner);
    }

    /**
     * Copies files to a clipboard.
     * 
     * @param files      files
     * @param clipboard  clipboard
     * @param owner      owner of the clipboard, can be null
     */
    public static void copyToClipboard(
        List<File> files, Clipboard clipboard, ClipboardOwner owner) {
        clipboard.setContents(
            new TransferableFileList(FileUtil.fileListToFileArray(files)), owner);
    }

    /**
     * Returns a list of files the system clipboard.
     * 
     * @param delimiterStringList  delimiter which separates file names if in 
     *                             the clipboard is a string with file names
     * @return list of files or null if no files in the clipboard
     */
    public static List<File> getFilesFromSystemClipboard(String delimiterStringList) {
        return getFilesFromClipboard(
            Toolkit.getDefaultToolkit().getSystemClipboard(), delimiterStringList);
    }

    /**
     * Returns a list of files from a clipboard. 
     * 
     * @param  clipboard            clipboard
     * @param  delimiterStringList  delimiter which separates file names if in 
     *                              the clipboard is a string with file names
     * @return list of files or null if no files in the clipboard
     */
    public static List<File> getFilesFromClipboard(Clipboard clipboard,
        String delimiterStringList) {
        List<File> files = null;
        DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
        Transferable transferable = clipboard.getContents(ClipboardUtil.class);
        if (TransferUtil.isFlavorSupported(flavors, fileListFlavor)) {
            return TransferUtil.getFileList(transferable);
        } else if (TransferUtil.isFlavorSupported(flavors, uriListFlavor)) {
            return TransferUtil.getFileListFromUriList(transferable);
        } else if (TransferUtil.isFlavorSupported(flavors, stringFlavor)) {
            return TransferUtil.getFileListFromTokenString(transferable, delimiterStringList);
        }
        return files;
    }
}
