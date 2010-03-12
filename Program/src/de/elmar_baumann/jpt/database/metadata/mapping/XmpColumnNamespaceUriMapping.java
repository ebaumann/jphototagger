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

package de.elmar_baumann.jpt.database.metadata.mapping;

import com.adobe.xmp.XMPConst;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link de.elmar_baumann.jpt.database.metadata.Column} und
 * einem Namespace-URI
 *
 * @author  Elmar Baumann
 * @version 2008-09-19
 */
public final class XmpColumnNamespaceUriMapping {
    private static final Map<Column, String> NAMESPACE_URI_OF_COLUMN =
        new HashMap<Column, String>();

    static {
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE,
                                    XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE,
                                    XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE,
                                    XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                                    XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                                    XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE,
                                    XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(
            ColumnXmpPhotoshopTransmissionReference.INSTANCE,
            XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpRating.INSTANCE, XMPConst.NS_XMP);
    }

    /**
     * Liefert den Namespace-URI für eine Spalte.
     *
     * @param  column  Spalte
     * @return Namespace-URI oder null bei unbekannter Spalte
     */
    public static String getNamespaceUriOfColumn(Column column) {
        return NAMESPACE_URI_OF_COLUMN.get(column);
    }

    private XmpColumnNamespaceUriMapping() {}
}
