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
package de.elmar_baumann.jpt.database.metadata.mapping;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link de.elmar_baumann.jpt.database.metadata.Column}
 * und dem Start eines
 * {@link com.adobe.xmp.properties.XMPPropertyInfo#getPath()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-19
 */
public final class XmpColumnXmpPathStartMapping {

    private static final Map<Column, String> XMP_PATH_START_OF_COLUMN = new HashMap<Column, String>();

    static {
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE                     , "dc:creator");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE                 , "dc:description");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE                      , "dc:rights");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE             , "dc:subject");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE                       , "dc:title");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE       , "Iptc4xmpCore:CountryCode");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE       , "Iptc4xmpCore:DateCreated");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE          , "Iptc4xmpCore:Location");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE      , "photoshop:AuthorsPosition");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE        , "photoshop:CaptionWriter");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE                 , "photoshop:City");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE              , "photoshop:Country");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE               , "photoshop:Credit");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE             , "photoshop:Headline");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE         , "photoshop:Instructions");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE               , "photoshop:Source");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE                , "photoshop:State");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, "photoshop:TransmissionReference");
        XMP_PATH_START_OF_COLUMN.put(ColumnXmpRating.INSTANCE                        , "xap:Rating");
    }

    /**
     * Liefert den Start des XMP-Pfads für eine XMP-Spalte.
     *
     * @param  column  XMP-Spalte
     * @return Pfadstart oder null bei unzugeordneter Spalte
     */
    public static String getXmpPathStartOfColumn(Column column) {
        return XMP_PATH_START_OF_COLUMN.get(column);
    }

    /**
     * Finds a column of a string with a specific path start.
     *
     * @param stringWithPathStart string with a path start, can contain more
     *                            characters after path start
     * @return                    column or null if not found
     */
    public static Column findColumn(String stringWithPathStart) {
        for (Column column : XMP_PATH_START_OF_COLUMN.keySet()) {
            if (stringWithPathStart.startsWith(XMP_PATH_START_OF_COLUMN.get(column))) {
                return column;
            }
        }
        return null;
    }

    private XmpColumnXmpPathStartMapping() {
    }
}
