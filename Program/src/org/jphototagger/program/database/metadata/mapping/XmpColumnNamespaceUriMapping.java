/*
 * @(#)XmpColumnNamespaceUriMapping.java    Created on 2008-09-19
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

package org.jphototagger.program.database.metadata.mapping;

import com.adobe.xmp.XMPConst;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link org.jphototagger.program.database.metadata.Column} und
 * einem Namespace-URI
 *
 * @author  Elmar Baumann
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
