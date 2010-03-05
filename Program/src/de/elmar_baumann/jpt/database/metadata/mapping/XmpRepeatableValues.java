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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
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
 * Returns, whether a XMP column has repeatable values.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-02-20
 */
public final class XmpRepeatableValues {

    private static final Map<Column, Boolean> IS_REPEATABLE =
            new HashMap<Column, Boolean>();

    static {
        IS_REPEATABLE.put(ColumnXmpDcCreator.INSTANCE                     , false);
        IS_REPEATABLE.put(ColumnXmpDcDescription.INSTANCE                 , false);
        IS_REPEATABLE.put(ColumnXmpDcRights.INSTANCE                      , false);
        IS_REPEATABLE.put(ColumnXmpDcSubjectsSubject.INSTANCE             , true);
        IS_REPEATABLE.put(ColumnXmpDcTitle.INSTANCE                       , false);
        IS_REPEATABLE.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE       , false);
        IS_REPEATABLE.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE          , false);
        IS_REPEATABLE.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE       , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE      , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE        , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCity.INSTANCE                 , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCountry.INSTANCE              , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopCredit.INSTANCE               , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopHeadline.INSTANCE             , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopInstructions.INSTANCE         , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopSource.INSTANCE               , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopState.INSTANCE                , false);
        IS_REPEATABLE.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, false);
        IS_REPEATABLE.put(ColumnXmpLastModified.INSTANCE                  , false);
        IS_REPEATABLE.put(ColumnXmpRating.INSTANCE                        , false);
    }

    /**
     * Returns, whether a XMP column has repeatable values.

     * @param  xmpColumn  XMP column
     * @return true if the column contains repeatable values
     * @throws IllegalArgumentException if there is no information whether
     *         the column has repeatable values
     */
    public static boolean isRepeatable(Column xmpColumn) {
        Boolean repeatable = IS_REPEATABLE.get(xmpColumn);
        if (repeatable == null)
            throw new IllegalArgumentException("Unknown column: " + xmpColumn);
        return repeatable;
    }

    private XmpRepeatableValues() {
    }
}
