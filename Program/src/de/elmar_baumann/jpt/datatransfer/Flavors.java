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

import de.elmar_baumann.lib.datatransfer.TransferUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.TransferHandler.TransferSupport;

/**
 * Data flavors supported in this application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-14
 */
public final class Flavors {

    private final class HIERARCHICAL_KEYWORDS {
    }

    private final class IMAGE_COLLECTION {
    }

    private final class KEYWORDS {
    }

    private final class METADATA_EDIT_TEMPLATES {
    }

    private final class THUMBNAILS_PANEL {
    }
    /**
     * {@link DataFlavor#javaFileListFlavor}
     */
    public static final DataFlavor FILE_LIST_FLAVOR = DataFlavor.javaFileListFlavor;
    /**
     * {@link TransferUtil#getUriListFlavor()}
     */
    public static final DataFlavor URI_LIST_FLAVOR              = TransferUtil.getUriListFlavor();
    public static final DataFlavor HIERARCHICAL_KEYWORDS_FLAVOR = new DataFlavor(HIERARCHICAL_KEYWORDS.class, null);
    public static final DataFlavor KEYWORDS_FLAVOR              = new DataFlavor(KEYWORDS.class, null);
    public static final DataFlavor THUMBNAILS_PANEL_FLAVOR      = new DataFlavor(THUMBNAILS_PANEL.class, null);
    public static final DataFlavor IMAGE_COLLECTION_FLAVOR      = new DataFlavor(IMAGE_COLLECTION.class, null);
    public static final DataFlavor METADATA_EDIT_TEMPLATES      = new DataFlavor(METADATA_EDIT_TEMPLATES.class, null);
    /**
     * Contains {@link DataFlavor#javaFileListFlavor} and
     * {@link TransferUtil#getUriListFlavor()}.
     */
    public static final DataFlavor[] FILE_FLAVORS = new DataFlavor[]{
        FILE_LIST_FLAVOR, URI_LIST_FLAVOR};

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
     * Returns whether the data flavor {@link #KEYWORDS_FLAVOR}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #KEYWORDS_FLAVOR} is supported
     */
    public static boolean hasKeywords(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(KEYWORDS_FLAVOR);
    }

    public static boolean hasMetadataEditTemplate(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(METADATA_EDIT_TEMPLATES);
    }

    /**
     * Returns whether the data flavor {@link #HIERARCHICAL_KEYWORDS_FLAVOR}
     * is supported.
     *
     * @param  transferSupport transfer support
     * @return                 true if the data flavor
     *                         {@link #HIERARCHICAL_KEYWORDS_FLAVOR} is supported
     */
    public static boolean hasHierarchicalKeywords(TransferSupport transferSupport) {
        return transferSupport.isDataFlavorSupported(HIERARCHICAL_KEYWORDS_FLAVOR);
    }

    private Flavors() {
    }
}
