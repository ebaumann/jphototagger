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
import javax.swing.TransferHandler.TransferSupport;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/17
 */
public class TransferUtil {

    private static final String uriListMimeType = "text/uri-list;class=java.lang.String";
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
     * {@link #getFileListFromUriList(javax.swing.TransferHandler.TransferSupport)}.
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
     * @param  transferSupport  transfer support
     * @return files if the transfer object supports {@link #getUriListFlavor()}
     *         else an empty list
     */
    public static List<File> getFileListFromUriList(TransferSupport transferSupport) {
        List<File> list = new ArrayList<File>();
        try {
            String data = (String) transferSupport.getTransferable().getTransferData(uriListFlavor);
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
     * Returns a list of files if the transfer object supports 
     * {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}.
     * 
     * @param  transferSupport  transfer support
     * @return list of files if supported by the transfer object and if it has
     *         file data
     */
    public static List<File> getFileList(TransferSupport transferSupport) {
        List<File> list = new ArrayList<File>();
        if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try {
                List files = (java.util.List) transferSupport.getTransferable().
                    getTransferData(DataFlavor.javaFileListFlavor);
                Iterator i = files.iterator();
                while (i.hasNext()) {
                    list.add((File) i.next());
                }
            } catch (Exception ex) {
                Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }

    /**
     * Returns whether a flavor is in a flavor array.
     * 
     * @param  flavors  flavor array
     * @param  flavor   flavor to search
     * @return true if found (supported)
     */
    public static boolean isFlavorSupported(DataFlavor[] flavors, DataFlavor flavor) {
        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }
        return false;
    }
}
