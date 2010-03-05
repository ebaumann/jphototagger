/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.app.AppLogger;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Support for data transfer.
 *
 * @author  Elmar Baumann
 * @version 2009-09-08
 */
public final class Support {

    /**
     * Returns the transferred keywords.
     *
     * <em>The data flavor has to be {@link Flavor#KEYWORDS_LIST}!</em>
     *
     * @param  transferable transferable
     * @return              keywords or null on errors
     */
    public static Object[] getKeywords(Transferable transferable) {
        try {
            return (Object[]) transferable.getTransferData(
                Flavor.KEYWORDS_LIST);
        } catch (Exception ex) {
            AppLogger.logSevere(Flavor.class, ex);
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
    public static List<DefaultMutableTreeNode> getKeywordNodes(
            Transferable transferable) {
        try {
            return (List<DefaultMutableTreeNode>) transferable.getTransferData(
                Flavor.KEYWORDS_TREE);
        } catch (Exception ex) {
            AppLogger.logSevere(Flavor.class, ex);
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
            return (String) transferable.getTransferData(
                DataFlavor.stringFlavor);
        } catch (Exception ex) {
            AppLogger.logSevere(Flavor.class, ex);
        }

        return null;
    }

    private Support() {}
}
