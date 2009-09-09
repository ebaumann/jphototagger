package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppLog;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Support for data transfer.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-08
 */
public final class Support {

    /**
     * Returns the transferred keywords.
     *
     * <em>The data flavor has to be {@link #KEYWORDS_FLAVOR}!</em>
     *
     * @param  transferable transferable
     * @return              keywords or null on errors
     */
    public static Object[] getKeywords(Transferable transferable) {
        try {
            return (Object[]) transferable.getTransferData(
                    Flavors.KEYWORDS_FLAVOR);
        } catch (Exception ex) {
            AppLog.logSevere(Flavors.class, ex);
        }
        return null;
    }

    /**
     * Returns the transferred keywords.
     *
     * <em>The data flavor has to be {@link #CATEGORIES_FLAVOR}!</em>
     *
     * @param  transferable transferable
     * @return              keywords or null on errors
     */
    public static Object[] getCategories(Transferable transferable) {
        try {
            return (Object[]) transferable.getTransferData(
                    Flavors.CATEGORIES_FLAVOR);
        } catch (Exception ex) {
            AppLog.logSevere(Flavors.class, ex);
        }
        return null;
    }

    /**
     * Returns the transferred hierarchical keywords nodes.
     *
     * <em>The data flavor has to be {@link #HIERARCHICAL_KEYWORDS_FLAVOR}!</em>
     *
     * @param  transferable transferable
     * @return              hierarchical keywords node or null on errors
     */
    @SuppressWarnings("unchecked")
    public static List<DefaultMutableTreeNode> getHierarchicalKeywordsNodes(
            Transferable transferable) {
        try {
            return (List<DefaultMutableTreeNode>) transferable.getTransferData(
                    Flavors.HIERARCHICAL_KEYWORDS_FLAVOR);
        } catch (Exception ex) {
            AppLog.logSevere(Flavors.class, ex);
        }
        return null;
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
        try {
            return (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception ex) {
            AppLog.logSevere(Flavors.class, ex);
        }
        return null;
    }

    private Support() {
    }

}
