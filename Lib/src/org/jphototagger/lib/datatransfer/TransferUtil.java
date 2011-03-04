package org.jphototagger.lib.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.Toolkit;

import java.io.File;

import java.net.URI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.StringTokenizer;

import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Utilities for data transfer.
 *
 * All functions with object reference parameters are throwing a
 * {@link NullPointerException} if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class TransferUtil {
    private static final String MIME_TYPE_URI_LIST = "text/uri-list;class=java.lang.String";
    private static final DataFlavor STRING_FLAVOR = DataFlavor.stringFlavor;
    private static final DataFlavor FILE_LIST_FLAVOR = DataFlavor.javaFileListFlavor;
    private static DataFlavor URI_LIST_FLAVOR;

    static {
        try {
            URI_LIST_FLAVOR = new DataFlavor(MIME_TYPE_URI_LIST);
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Delimiter between file names whithin a string.
     */
    public enum FilenameDelimiter {

        /**
         * Filenames are delimited by an empty string <code>""</code>
         */
        EMPTY(""),

        /**
         * Filenames are delimited by a newline string <code>"\n"</code>
         */
        NEWLINE("\n"),
        ;

        private final String delimiter;

        private FilenameDelimiter(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public String toString() {
            return delimiter;
        }
    }

    /**
     * Returns the selected items in a {@link JList} as a token string within a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * @param  objects    objects
     * @param  delimiter  delimiter between the item strings
     * @return            {@link StringSelection}: selected items as strings
     *                    separated by <code>delimiter</code>
     */
    public static Transferable getSelectedItemStringsTransferable(JList objects, String delimiter) {
        if (objects == null) {
            throw new NullPointerException("list == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        Object[] values = objects.getSelectedValues();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            Object val = values[i];

            sb.append((val == null)
                      ? ""
                      : val.toString());
            sb.append((i != values.length - 1)
                      ? delimiter
                      : "");
        }

        return new StringSelection(sb.toString());
    }

    /**
     * Returns the Integers of a list in a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * Each integer is separated by a delimiter.
     *
     * @param  integers  integers
     * @param  delimiter delimiter
     * @return           {@link StringSelection}: A String within integer token
     *                   separated by <code>delimiter</code>
     */
    public static Transferable getIntegerListTransferable(List<Integer> integers, String delimiter) {
        if (integers == null) {
            throw new NullPointerException("list == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        StringBuilder sb = new StringBuilder();
        int size = integers.size();

        for (int i = 0; i < size; i++) {
            Integer integer = integers.get(i);

            sb.append(integer.toString());
            sb.append((i < size - 1)
                      ? delimiter
                      : "");
        }

        return new StringSelection(sb.toString());
    }

    /**
     * Returns the Strings of a list in a
     * {@link java.awt.datatransfer.StringSelection}.
     *
     * Each string is separated by a delimiter.
     *
     * @param  strings   strings
     * @param  delimiter delimiter
     * @return           <code>StringSelection</code>: A String within integer
     *                   token separated by <code>delimiter</code>
     */
    public static Transferable getStringListTransferable(List<String> strings, String delimiter) {
        if (strings == null) {
            throw new NullPointerException("list == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        StringBuilder sb = new StringBuilder();
        int size = strings.size();

        for (int i = 0; i < size; i++) {
            String string = strings.get(i);

            sb.append(string);
            sb.append((i < size - 1)
                      ? delimiter
                      : "");
        }

        return new StringSelection(sb.toString());
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
     * @return              files
     */
    public static List<File> getFilesFromUriList(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        List<File> files = new ArrayList<File>();

        try {
            String data = (String) transferable.getTransferData(URI_LIST_FLAVOR);

            for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens(); ) {
                String token = st.nextToken().trim();

                if (token.startsWith("file:")) {
                    files.add(new File(new URI(token)));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return files;
    }

    /**
     * Returns a list of files from a string within file names, e.g.
     * <code>/home/elmar/workspace</code>.
     *
     * @param  transferable transferable
     * @param  delimiter    delimiter
     * @return              files
     */
    public static List<File> getFilesFromTokenString(Transferable transferable, FilenameDelimiter delimiter) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        List<File> files = new ArrayList<File>();

        try {
            Object o = transferable.getTransferData(STRING_FLAVOR);

            if (o instanceof String) {
                String data = (String) o;

                for (StringTokenizer st = new StringTokenizer(data, delimiter.toString()); st.hasMoreTokens(); ) {
                    files.add(new File(st.nextToken().trim()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return files;
    }

    /**
     * Returns a list of files in a transferable which supports
     * {@link java.awt.datatransfer.DataFlavor#javaFileListFlavor}.
     *
     * @param   transferable transferable
     * @return               list of files
     */
    public static List<File> getFilesFromJavaFileList(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        List<File> list = new ArrayList<File>();

        try {
            @SuppressWarnings("unchecked") List<File> files =
                (List<File>) transferable.getTransferData(FILE_LIST_FLAVOR);
            Iterator<File> it = files.iterator();

            while (it.hasNext()) {
                list.add(it.next());
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
     * @param  delimiter    delimiter
     * @return              files or empty list
     */
    public static List<File> getFiles(Transferable transferable, FilenameDelimiter delimiter) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        if (delimiter == null) {
            throw new NullPointerException("delimiter == null");
        }

        List<File> files = new ArrayList<File>();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        if (isDataFlavorSupported(flavors, FILE_LIST_FLAVOR)) {
            return getFilesFromJavaFileList(transferable);
        } else if (isDataFlavorSupported(flavors, URI_LIST_FLAVOR)) {
            return getFilesFromUriList(transferable);
        } else if (isDataFlavorSupported(flavors, STRING_FLAVOR)) {
            return getFilesFromTokenString(transferable, delimiter);
        }

        return files;
    }

    /**
     * Returns wheter a transferable contains file data.
     *
     * @param  transferable transferable
     * @return              true, if the transferable maybe contain file data
     */
    public static boolean maybeContainFileData(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        return containsFiles(transferable);
    }

    private static boolean containsFiles(Transferable transferable) {
        final DataFlavor[] flavors = transferable.getTransferDataFlavors();

        try {
            if (isDataFlavorSupported(flavors, FILE_LIST_FLAVOR)) {
                return ((java.util.List<?>) transferable.getTransferData(FILE_LIST_FLAVOR)).size() > 0;
            } else if (isDataFlavorSupported(flavors, URI_LIST_FLAVOR)) {
                return ((String) transferable.getTransferData(URI_LIST_FLAVOR)).startsWith("file:");
            } else if (isDataFlavorSupported(flavors, STRING_FLAVOR)) {
                return new File((String) transferable.getTransferData(STRING_FLAVOR)).exists();
            }
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, null, ex);
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
            return maybeContainFileData(
                Toolkit.getDefaultToolkit().getSystemClipboard().getContents(TransferUtil.class));
        } catch (Exception ex) {
            Logger.getLogger(TransferUtil.class.getName()).log(Level.SEVERE, "", ex);
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
    public static boolean isDataFlavorSupported(DataFlavor[] flavors, DataFlavor flavor) {
        if (flavors == null) {
            throw new NullPointerException("flavors == null");
        }

        if (flavor == null) {
            throw new NullPointerException("flavor == null");
        }

        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns wheter transferable supports at least one data flavor.
     *
     * @param  transferable transferable
     * @param  flavors      data flavors
     * @return              true if that transferable supports at least on of
     *                      that data flavors
     */
    public static boolean isADataFlavorSupported(Transferable transferable, DataFlavor... flavors) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        if (flavors == null) {
            throw new NullPointerException("flavors == null");
        }

        for (DataFlavor flavor : flavors) {
            if (transferable.isDataFlavorSupported(flavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether to copy dropped data.
     * <p>
     * {@link TransferSupport#isDrop()} has to be true when calling this method!
     *
     * @param  support transfer support
     * @return         true if the data shall be copied
     */
    public static boolean isCopy(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        if (!support.isDrop()) {
            throw new IllegalStateException("No drop!");
        }

        return (support.getSourceDropActions() & TransferHandler.COPY) == TransferHandler.COPY;
    }

    /**
     * Returns whether to move dropped data.
     * <p>
     * {@link TransferSupport#isDrop()} has to be true when calling this method!
     *
     * @param  support transfer support
     * @return         true if the data shall be moved
     */
    public static boolean isMove(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        if (!support.isDrop()) {
            throw new IllegalStateException("No drop!");
        }

        return (support.getSourceDropActions() & TransferHandler.MOVE) == TransferHandler.MOVE;
    }

    /**
     * Returns whether to link dropped data.
     * <p>
     * {@link TransferSupport#isDrop()} has to be true when calling this method!
     *
     * @param  support transfer support
     * @return         true if the data shall be linked
     */
    public static boolean isLink(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        if (!support.isDrop()) {
            throw new IllegalStateException("No drop!");
        }

        return (support.getSourceDropActions() & TransferHandler.LINK) == TransferHandler.LINK;
    }

    /**
     * Returns the index of the drop location where the <em>target has to be an
     * instance of {@link JList}</em>.
     *
     * @param  support transfer support
     * @return         index or -1 if the drop location is not over a list item
     *                 or if the action is not a drop
     */
    public static int getListDropIndex(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        boolean isList = support.getComponent() instanceof JList;

        if (!isList) {
            throw new IllegalArgumentException("Component is not a JList: " + support.getComponent());
        }

        if (!support.isDrop()) {
            return -1;
        }

        JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();

        return dl.getIndex();
    }

    /**
     * Returns the drop node where <em>the target has to be an instance of
     * {@link JTree} and its nodes {@link DefaultMutableTreeNode}s</em>.
     *
     * @param  support transfer support
     * @return         node or null if the last path component of the drop is
     *                 not a default mutable tree node or if the action is not a
     *                 drop
     */
    public static DefaultMutableTreeNode getTreeDropNode(TransferSupport support) {
        if (support == null) {
            throw new NullPointerException("support == null");
        }

        boolean isTree = support.getComponent() instanceof JTree;

        if (!isTree) {
            throw new IllegalArgumentException("Component is not a JTree: " + support.getComponent());
        }

        if (support.isDrop()) {
            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            Object dropObject = dropLocation.getPath().getLastPathComponent();

            return (dropObject instanceof DefaultMutableTreeNode)
                   ? (DefaultMutableTreeNode) dropObject
                   : null;
        }

        JTree tree = (JTree) support.getComponent();
        TreePath selPath = tree.getSelectionPath();

        if (selPath != null) {
            Object o = selPath.getLastPathComponent();

            if (o instanceof DefaultMutableTreeNode) {
                return (DefaultMutableTreeNode) o;
            }
        }

        return null;
    }

    private TransferUtil() {}
}
