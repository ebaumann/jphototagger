package de.elmar_baumann.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public final class TransferUtil {

    private static final String uriListMimeType = "text/uri-list;class=java.lang.String";
    private static final DataFlavor stringFlavor = DataFlavor.stringFlavor;
    private static final DataFlavor fileListFlavor = DataFlavor.javaFileListFlavor;
    private static DataFlavor uriListFlavor;

    static {
        try {
            uriListFlavor = new DataFlavor(uriListMimeType);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the selected items in a {@link java.awt.datatransfer.StringSelection}.
     * Each item is separated by a delimiter.
     * 
     * @param  list       list
     * @param  delimiter  delimiter between the item strings
     * @return <code>StringSelection</code>: A String within value strings,
     *         separated by <code>delimiter</code>
     */
    public static Transferable getSelectedItemStringsTransferable(JList list, String delimiter) {
        Object[] values = list.getSelectedValues();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            Object val = values[i];
            buffer.append(val == null ? "" : val.toString());
            buffer.append(i != values.length - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Integers of a list in a {@link java.awt.datatransfer.StringSelection}.
     * Each integer is separated by a delimiter.
     * 
     * @param  list      list
     * @param delimiter  delimiter
     * @return <code>StringSelection</code>: A String within integer token
     *         separated by <code>delimiter</code>
     */
    public static Transferable getIntegerListTransferable(List<Integer> list, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer integer = list.get(i);
            buffer.append(integer.toString());
            buffer.append(i < size - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Strings of a list in a {@link java.awt.datatransfer.StringSelection}.
     * Each string is separated by a delimiter.
     * 
     * @param  list      list
     * @param delimiter  delimiter
     * @return <code>StringSelection</code>: A String within integer token
     *         separated by <code>delimiter</code>
     */
    public static Transferable getStringListTransferable(List<String> list, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String string = list.get(i);
            buffer.append(string);
            buffer.append(i < size - 1 ? delimiter : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the flavor of a string with a URI list, needed to get files from 
     * {@link #getFilesFromUriList(javax.swing.TransferHandler.TransferSupport)}.
     * 
     * @return flavor
     */
    public static DataFlavor getUriListFlavor() {
        return uriListFlavor;
    }

    /**
     * Returns a list of files from a string within URIs, e.g.
     * <code>file:///home/elmar/workspace</code>. Linux file managers like
     * Konqueror and Nautilus sends such transfer data.
     * 
     * @param  transferable  transferable
     * @return files
     */
    public static List<File> getFilesFromUriList(Transferable transferable) {
        List<File> list = new ArrayList<File>();
        try {
            String data = (String) transferable.getTransferData(uriListFlavor);
            for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
                String token = st.nextToken().trim();
                if (token.startsWith("file:")) {
                    list.add(new File(new URI(token)));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Returns a list of files from a string within file names, e.g.
     * <code>/home/elmar/workspace</code>.
     * 
     * @param  transferable  transferable
     * @param  delimiter     delimiter which separates the file names
     * @return files
     */
    public static List<File> getFilesFromTokenString(Transferable transferable,
        String delimiter) {
        List<File> list = new ArrayList<File>();
        try {
            String data = (String) transferable.getTransferData(stringFlavor);
            for (StringTokenizer st = new StringTokenizer(data, delimiter); st.hasMoreTokens();) {
                list.add(new File(st.nextToken().trim()));
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Returns a list of files in a transferable which supports
     * {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}.
     * 
     * @param  transferable  transferable
     * @return list of files
     */
    public static List<File> getFilesFromJavaFileList(Transferable transferable) {
        List<File> list = new ArrayList<File>();
        try {
            List files = (java.util.List) transferable.getTransferData(fileListFlavor);
            Iterator i = files.iterator();
            while (i.hasNext()) {
                list.add((File) i.next());
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Returns a file list from a transferable. First ist checks the supported
     * flavors and then ist calls the appropriate function which retrieves the
     * file list.
     * 
     * @param  transferable transferable
     * @param  delimiter    token delimiter if files names are in a token string
     * @return files
     */
    public static List<File> getFiles(Transferable transferable, String delimiter) {
        List<File> list = new ArrayList<File>();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        if (isDataFlavorSupported(flavors, fileListFlavor)) {
            return getFilesFromJavaFileList(transferable);
        } else if (isDataFlavorSupported(flavors, uriListFlavor)) {
            return getFilesFromUriList(transferable);
        } else if (isDataFlavorSupported(flavors, stringFlavor)) {
            return getFilesFromTokenString(transferable, delimiter);
        }
        return list;
    }
    
    /**
     * Returns wheter a transferable contains file data. This is true, if
     * it supports {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}
     * or {@link java.awt.datatransfer.DataFlavor#stringFlavor}. The second
     * case is the reason for <em>maybe</em>.
     * 
     * @param  transferable  transferable
     * @return true, if the transferable maybe contain file data
     */
    public static boolean maybeContainFileData(Transferable transferable) {
        return isDataFlavorSupported(transferable.getTransferDataFlavors(), DataFlavor.javaFileListFlavor) ||
            isDataFlavorSupported(transferable.getTransferDataFlavors(), DataFlavor.stringFlavor);
    }

    /**
     * Returns whether a flavor is in a flavor array.
     * 
     * @param  flavors  flavor array
     * @param  flavor   flavor to search
     * @return true if found (supported)
     */
    public static boolean isDataFlavorSupported(DataFlavor[] flavors, DataFlavor flavor) {
        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    private TransferUtil() {
    }
}
