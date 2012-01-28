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

import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.metadata.MetaDataValueData;
import org.jphototagger.lib.datatransfer.TransferUtil;

/**
 * DataTransferSupport for data transfer.
 *
 * @author Elmar Baumann
 */
public final class DataTransferSupport {

    private DataTransferSupport() {
    }

    /**
     * Returns the transferred keywords.
     *
     * <em>The data flavor has to be {@code Flavor#KEYWORDS_LIST}!</em>
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
            Logger.getLogger(DataTransferSupport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns from a transferable a collection of metadata value data.
     *
     * @param  transferable transferable supporting {@code Flavor#META_DATA_VALUE}
     * @return              collection or null on errors
     */
    @SuppressWarnings("unchecked")
    public static Collection<? extends MetaDataValueData> getMetaDataValueData(Transferable transferable) {
        if (transferable == null) {
            throw new NullPointerException("transferable == null");
        }

        try {
            return (Collection<? extends MetaDataValueData>) transferable.getTransferData(Flavor.META_DATA_VALUE);
        } catch (Exception ex) {
            Logger.getLogger(DataTransferSupport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Returns the transferred keyword node.
     *
     * <em>The data flavor has to be {@code Flavor#KEYWORDS_TREE}!</em>
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
            Logger.getLogger(DataTransferSupport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     *
     * @param  mdValueData can be null
     * @return            string, can be empty
     */
    public static String getStringFromMetaDataValueData(Collection<? extends MetaDataValueData> mdValueData) {
        if (mdValueData != null) {
            StringBuilder sb = new StringBuilder();
            int index = 0;

            for (MetaDataValueData data : mdValueData) {
                sb.append(index == 0 ? "" : ";");
                sb.append(data.getData().toString());
                index++;
            }

            return sb.toString();
        }

        return "";
    }

    /**
     * Returns a transferred string.
     *
     * <em>The data flavor has to be {@code DataFlavor#stringFlavor}!</em>
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
            Logger.getLogger(DataTransferSupport.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static List<File> getImageFiles(TransferSupport support) {
        List<File> files = TransferUtil.getFiles(support.getTransferable(), TransferUtil.FilenameDelimiter.NEWLINE);

        return FileFilterUtil.getImageFiles(files);
    }
}
