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
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public final class ClipboardUtil {

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
        if (files == null)
            throw new NullPointerException("files == null");
        copyToClipboard(files, Toolkit.getDefaultToolkit().getSystemClipboard(), owner);
    }

    /**
     * Copies files to a clipboard.
     * 
     * @param files      files
     * @param clipboard  clipboard
     * @param owner      owner of the clipboard, can be null
     */
    public static void copyToClipboard(List<File> files, Clipboard clipboard, ClipboardOwner owner) {
        if (files == null)
            throw new NullPointerException("files == null");
        if (clipboard == null)
            throw new NullPointerException("clipboard == null");
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
        if (delimiterStringList == null)
            throw new NullPointerException("delimiterStringList == null");
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
    public static List<File> getFilesFromClipboard(Clipboard clipboard, String delimiterStringList) {
        if (clipboard == null)
            throw new NullPointerException("files == null");
        if (delimiterStringList == null)
            throw new NullPointerException("delimiterStringList == null");
        List<File> files = null;
        DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
        Transferable transferable = clipboard.getContents(ClipboardUtil.class);
        if (TransferUtil.isDataFlavorSupported(flavors, fileListFlavor)) {
            return TransferUtil.getFilesFromJavaFileList(transferable);
        } else if (TransferUtil.isDataFlavorSupported(flavors, uriListFlavor)) {
            return TransferUtil.getFilesFromUriList(transferable);
        } else if (TransferUtil.isDataFlavorSupported(flavors, stringFlavor)) {
            return TransferUtil.getFilesFromTokenString(transferable, delimiterStringList);
        }
        return files;
    }

    private ClipboardUtil() {
    }
}
