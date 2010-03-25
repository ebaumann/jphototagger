/*
 * @(#)Flavor.java    Created on 2009-08-14
 *
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

package org.jphototagger.program.datatransfer;

import org.jphototagger.lib.datatransfer.TransferUtil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

/**
 * Data flavors supported in JPhotoTagger.
 *
 * @author Elmar Baumann
 */
public final class Flavor {

    /**
     * A reference to {@link DataFlavor#javaFileListFlavor}
     */
    public static final DataFlavor FILE_LIST_FLAVOR =
        DataFlavor.javaFileListFlavor;

    /**
     * A reference to {@link TransferUtil#getUriListFlavor()}
     */
    public static final DataFlavor URI_LIST = TransferUtil.getUriListFlavor();

    /**
     * A {@link java.util.Collection} of {@link java.io.File}s: The selected
     * image files and their XMP sidecar files
     */
    public static final DataFlavor THUMBNAILS_PANEL =
        new DataFlavor(THUMBNAILS_PANEL.class, null);

    /**
     * The selected list values as an array of {@link Object}s
     */
    public static final DataFlavor METADATA_TEMPLATES =
        new DataFlavor(METADATA_TEMPLATES.class, null);

    /**
     * All metadata data flavors such as keywords or column data
     */
    private static final List<DataFlavor> METADATA_FLAVORS =
        new ArrayList<DataFlavor>();

    /**
     * A {@link java.util.Collection} of
     * {@link javax.swing.tree.DefaultMutableTreeNode}s: The selected tree nodes
     */
    public static final DataFlavor KEYWORDS_TREE =
        new DataFlavor(KEYWORDS_TREE.class, null);

    /**
     * The selected list values as an array of {@link Object}s
     */
    public static final DataFlavor KEYWORDS_LIST =
        new DataFlavor(KEYWORDS_LIST.class, null);

    /**
     * A {@link java.util.Collection} of {@link java.io.File}s: The selected
     * image files and their XMP sidecar files
     */
    public static final DataFlavor IMAGE_COLLECTION =
        new DataFlavor(IMAGE_COLLECTION.class, null);

    /**
     * Contains {@link #URI_LIST} and {@link #FILE_LIST_FLAVOR }
     */
    static final DataFlavor[] FILE_FLAVORS = new DataFlavor[] {
                                                 FILE_LIST_FLAVOR,
            URI_LIST };

    /**
     * A {@link java.util.Collection} of
     * {@link org.jphototagger.program.data.ColumnData} objects
     */
    public static final DataFlavor COLUMN_DATA =
        new DataFlavor(COLUMN_DATA.class, null);

    static {
        METADATA_FLAVORS.add(KEYWORDS_TREE);
        METADATA_FLAVORS.add(KEYWORDS_LIST);
        METADATA_FLAVORS.add(METADATA_TEMPLATES);
        METADATA_FLAVORS.add(COLUMN_DATA);
    }

    private Flavor() {}

    /**
     * Returns whether metadata is transferred, e.g. keywords or column data
     *
     * @param  t tranferable
     * @return   true if metadata is transferred
     */
    public static boolean isMetadataTransferred(Transferable t) {
        for (DataFlavor flavor : t.getTransferDataFlavors()) {
            if (METADATA_FLAVORS.contains(flavor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether a transferable supports the data flavor
     * {@link #FILE_FLAVORS}.
     *
     * @param  transferable transferable
     * @return              true if the transferable supports the data flavor
     *                      {@link #FILE_FLAVORS} is supported
     */
    public static boolean hasFiles(Transferable transferable) {
        return TransferUtil.isADataFlavorSupported(transferable, FILE_FLAVORS);
    }

    /**
     * Returns whether the data flavor {@link #KEYWORDS_LIST}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #KEYWORDS_LIST} is supported
     */
    public static boolean hasKeywordsFromList(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(KEYWORDS_LIST);
    }

    public static boolean hasMetadataTemplate(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(METADATA_TEMPLATES);
    }

    public static boolean hasColumnData(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(COLUMN_DATA);
    }

    /**
     * Returns whether the data flavor {@link #KEYWORDS_TREE}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #KEYWORDS_TREE} is supported
     */
    public static boolean hasKeywordsFromTree(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(KEYWORDS_TREE);
    }

    private final class COLUMN_DATA {}


    private final class IMAGE_COLLECTION {}


    private final class KEYWORDS_LIST {}


    private final class KEYWORDS_TREE {}


    private final class METADATA_TEMPLATES {}


    private final class THUMBNAILS_PANEL {}
}
