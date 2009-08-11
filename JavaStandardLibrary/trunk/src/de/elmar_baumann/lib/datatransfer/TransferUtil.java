package de.elmar_baumann.lib.datatransfer;

import java.awt.Toolkit;
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
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-17
 */
public final class TransferUtil {

    private static final String MIME_TYPE_URI_LIST =
            "text/uri-list;class=java.lang.String"; // NOI18N
    private static final DataFlavor STRING_FLAVOR = DataFlavor.stringFlavor;
    private static final DataFlavor FILE_LIST_FLAVOR =
            DataFlavor.javaFileListFlavor;
    private static DataFlavor URI_LIST_FLAVOR;

    static {
        try {
            URI_LIST_FLAVOR = new DataFlavor(MIME_TYPE_URI_LIST);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    /**
     * Returns the selected items in a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * Each item is separated by a delimiter.
     * 
     * @param  list       list
     * @param  delimiter  delimiter between the item strings
     * @return            <code>StringSelection</code>: A String within value
     *                    strings, separated by <code>delimiter</code>
     */
    public static Transferable getSelectedItemStringsTransferable(
            JList list, String delimiter) {
        if (list == null)
            throw new NullPointerException("list == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        Object[] values = list.getSelectedValues();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            Object val = values[i];
            buffer.append(val == null
                          ? "" // NOI18N
                          : val.toString());
            buffer.append(i != values.length - 1
                          ? delimiter
                          : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Integers of a list in a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * Each integer is separated by a delimiter.
     * 
     * @param  list      list
     * @param delimiter  delimiter
     * @return <code>StringSelection</code>: A String within integer token
     *         separated by <code>delimiter</code>
     */
    public static Transferable getIntegerListTransferable(
            List<Integer> list, String delimiter) {
        if (list == null)
            throw new NullPointerException("list == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Integer integer = list.get(i);
            buffer.append(integer.toString());
            buffer.append(i < size - 1
                          ? delimiter
                          : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the Strings of a list in a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * Each string is separated by a delimiter.
     * 
     * @param  list      list
     * @param  delimiter delimiter
     * @return           <code>StringSelection</code>: A String within integer
     *                   token separated by <code>delimiter</code>
     */
    public static Transferable getStringListTransferable(List<String> list,
            String delimiter) {
        if (list == null)
            throw new NullPointerException("list == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        StringBuffer buffer = new StringBuffer();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String string = list.get(i);
            buffer.append(string);
            buffer.append(i < size - 1
                          ? delimiter
                          : ""); // NOI18N
        }
        return new StringSelection(buffer.toString());
    }

    /**
     * Returns the flavor of a string with a URI list, needed to get files from 
     * {@link #getFilesFromUriList(java.awt.datatransfer.Transferable)}.
     * 
     * @return flavor
     */
    public static DataFlavor getUriListFlavor() {
        return URI_LIST_FLAVOR;
    }

    /**
     * Returns a list of files from a string within URIs, e.g.
     * <code>file:///home/elmar/workspace</code>. Linux file managers like
     * Konqueror and Nautilus sends such transfer data.
     * 
     * @param  transferable transferable
     * @return files
     */
    public static List<File> getFilesFromUriList(Transferable transferable) {
        if (transferable == null)
            throw new NullPointerException("transferable == null"); // NOI18N

        List<File> list = new ArrayList<File>();
        try {
            String data = (String) transferable.getTransferData(URI_LIST_FLAVOR);
            for (StringTokenizer st = new StringTokenizer(data, "\r\n"); // NOI18N
                    st.hasMoreTokens();) {
                String token = st.nextToken().trim();
                if (token.startsWith("file:")) { // NOI18N
                    list.add(new File(new URI(token)));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Returns a list of files from a string within file names, e.g.
     * <code>/home/elmar/workspace</code>.
     * 
     * @param  transferable transferable
     * @param  delimiter    delimiter which separates the file names
     * @return              files
     */
    public static List<File> getFilesFromTokenString(Transferable transferable,
            String delimiter) {
        if (transferable == null)
            throw new NullPointerException("transferable == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        List<File> list = new ArrayList<File>();
        try {
            String data = (String) transferable.getTransferData(STRING_FLAVOR);
            for (StringTokenizer st = new StringTokenizer(data, delimiter);
                    st.hasMoreTokens();) {
                list.add(new File(st.nextToken().trim()));
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    /**
     * Returns a list of files in a transferable which supports
     * {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}.
     * 
     * @param  transferable transferable
     * @return              list of files
     */
    public static List<File> getFilesFromJavaFileList(Transferable transferable) {
        if (transferable == null)
            throw new NullPointerException("transferable == null"); // NOI18N

        List<File> list = new ArrayList<File>();
        try {
            List files = (java.util.List) transferable.getTransferData(
                    FILE_LIST_FLAVOR);
            Iterator i = files.iterator();
            while (i.hasNext()) {
                list.add((File) i.next());
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
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
    public static List<File> getFiles(Transferable transferable,
            String delimiter) {
        if (transferable == null)
            throw new NullPointerException("transferable == null"); // NOI18N
        if (delimiter == null)
            throw new NullPointerException("delimiter == null"); // NOI18N

        List<File> list = new ArrayList<File>();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        if (isDataFlavorSupported(flavors, FILE_LIST_FLAVOR)) {
            return getFilesFromJavaFileList(transferable);
        } else if (isDataFlavorSupported(flavors, URI_LIST_FLAVOR)) {
            return getFilesFromUriList(transferable);
        } else if (isDataFlavorSupported(flavors, STRING_FLAVOR)) {
            return getFilesFromTokenString(transferable, delimiter);
        }
        return list;
    }

    /**
     * Returns wheter a transferable contains file data.
     * 
     * @param  transferable transferable
     * @return              true, if the transferable maybe contain file data
     */
    public static boolean maybeContainFileData(Transferable transferable) {
        if (transferable == null)
            throw new NullPointerException("transferable == null"); // NOI18N

        return containsFiles(transferable);
    }

    private static boolean containsFiles(Transferable transferable) {
        final DataFlavor[] flavors = transferable.getTransferDataFlavors();
        try {
            if (isDataFlavorSupported(flavors, FILE_LIST_FLAVOR)) {
                return ((java.util.List) transferable.getTransferData(
                        FILE_LIST_FLAVOR)).size() > 0;
            } else if (isDataFlavorSupported(flavors, URI_LIST_FLAVOR)) {
                return ((String) transferable.getTransferData(URI_LIST_FLAVOR)).
                        startsWith("file:");
            } else if (isDataFlavorSupported(flavors, STRING_FLAVOR)) {
                return new File((String) transferable.getTransferData(
                        STRING_FLAVOR)).exists();
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Returns whether the system clipboard maybe containing files.
     *
     * @return true if the system clipboard mayb containing files
     */
    public static boolean systemClipboardMaybeContainFiles() {
        try {
            return maybeContainFileData(Toolkit.getDefaultToolkit().
                    getSystemClipboard().getContents(TransferUtil.class));
        } catch (Exception ex) {
            Logger.getLogger(
                    TransferUtil.class.getName()).log(Level.SEVERE, "", ex);
        }
        return false;
    }

    /**
     * Returns whether a flavor is in a flavor array.
     * 
     * @param  flavors flavor array
     * @param  flavor  flavor to search
     * @return true    if found (supported)
     */
    public static boolean isDataFlavorSupported(
            DataFlavor[] flavors, DataFlavor flavor) {
        if (flavor == null)
            throw new NullPointerException("flavor == null"); // NOI18N

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
