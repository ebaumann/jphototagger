/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.app.AppLog;
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
     * <em>The data flavor has to be {@link Flavors#KEYWORDS_FLAVOR}!</em>
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
     * <em>The data flavor has to be {@link Flavors#CATEGORIES_FLAVOR}!</em>
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
     * <em>The data flavor has to be {@link Flavors#HIERARCHICAL_KEYWORDS_FLAVOR}!</em>
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
