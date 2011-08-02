package org.jphototagger.program.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jphototagger.domain.database.ColumnData;
import org.jphototagger.lib.datatransfer.TransferUtil;
import org.jphototagger.program.io.ImageFileFilterer;

/**
 * Support for data transfer.
 *
 * @author Elmar Baumann
 */
public final class Support {
    private Support() {}

    /**
     * Returns the transferred keywords.
     *
     * <em>The data flavor has to be {@link Flavor#KEYWORDS_LIST}!</em>
     *
     * @param  transferable transferable
     * @return              keywords or null on errors
     */
    public static Object[] getKeywords(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (Object[]) transferable.getTransferData(Flavor.KEYWORDS_LIST);
        } catch (Exception ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns from a transferable a collection of column data.
     *
     * @param  transferable transferable supporting {@link Flavor#COLUMN_DATA}
     * @return              collection or null on errors
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends ColumnData> getColumnData(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (Collection<? extends ColumnData>) transferable.getTransferData(Flavor.COLUMN_DATA);
        } catch (Exception ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns the transferred keyword node.
     *
     * <em>The data flavor has to be {@link Flavor#KEYWORDS_TREE}!</em>
     *
     * @param  transferable transferable
     * @return              keyword node or null on errors
     */
    @SuppressWarnings("unchecked")
    public static List<DefaultMutableTreeNode> getKeywordNodes(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (List<DefaultMutableTreeNode>) transferable.getTransferData(Flavor.KEYWORDS_TREE);
        } catch (Exception ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @param  columnData can be null
     * @return            string, can be empty
     */
    public static String getStringFromColumnData(Collection<? extends ColumnData> columnData) {
        if (columnData != null) {
            StringBuilder sb = new StringBuilder();
            int index = 0;

            for (ColumnData data : columnData) {
                sb.append((index++ == 0)
                          ? ""
                          : ";");
                sb.append(data.getData().toString());
            }

            return sb.toString();
        }

        return "";
    }

    /**
     * Returns a transferred string.
     *
     * <em>The data flavor has to be {@link DataFlavor#stringFlavor}!</em>
     *
     * @param  transferable transferable
     * @return              string or null on errors
     */
    public static String getString(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            Logger.getLogger(Support.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static List<File> getImageFiles(TransferSupport support) {
        List<File> files = TransferUtil.getFiles(support.getTransferable(), TransferUtil.FilenameDelimiter.NEWLINE);

        return ImageFileFilterer.getImageFiles(files);
    }
}
