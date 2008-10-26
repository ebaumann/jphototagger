package de.elmar_baumann.lib.clipboard;

import de.elmar_baumann.lib.datatransfer.TransferableFileList;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ClipboardUtil {

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
}
