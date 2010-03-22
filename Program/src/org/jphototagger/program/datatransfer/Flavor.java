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

import javax.swing.TransferHandler.TransferSupport;

/**
 * Data flavors supported in this application.
 *
 * @author  Elmar Baumann
 */
public final class Flavor {
    private final class KEYWORDS_TREE {}


    private final class IMAGE_COLLECTION {}


    private final class KEYWORDS_LIST {}


    private final class METADATA_TEMPLATES {}


    private final class THUMBNAILS_PANEL {}


    /**
     * {@link DataFlavor#javaFileListFlavor}
     */
    public static final DataFlavor FILE_LIST_FLAVOR =
        DataFlavor.javaFileListFlavor;

    /**
     * {@link TransferUtil#getUriListFlavor()}
     */
    public static final DataFlavor URI_LIST      =
        TransferUtil.getUriListFlavor();
    public static final DataFlavor KEYWORDS_TREE =
        new DataFlavor(KEYWORDS_TREE.class, null);
    public static final DataFlavor KEYWORDS_LIST =
        new DataFlavor(KEYWORDS_LIST.class, null);
    public static final DataFlavor THUMBNAILS_PANEL =
        new DataFlavor(THUMBNAILS_PANEL.class, null);
    public static final DataFlavor IMAGE_COLLECTION =
        new DataFlavor(IMAGE_COLLECTION.class, null);
    public static final DataFlavor METADATA_TEMPLATES =
        new DataFlavor(METADATA_TEMPLATES.class, null);

    /**
     * Contains {@link DataFlavor#javaFileListFlavor} and
     * {@link TransferUtil#getUriListFlavor()}.
     */
    static final DataFlavor[] FILE_FLAVORS = new DataFlavor[] {
                                                 FILE_LIST_FLAVOR,
            URI_LIST };

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

    private Flavor() {}
}
