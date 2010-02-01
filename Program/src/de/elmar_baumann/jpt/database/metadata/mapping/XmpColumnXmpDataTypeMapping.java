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
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
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
 * Mapping zwischen einer XMP-Spalte
 * {@link de.elmar_baumann.jpt.database.metadata.Column}
 * und einem XMP-Datentyp.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-20
 */
public final class XmpColumnXmpDataTypeMapping {

    private static final Map<Column, XmpValueType> XMP_VALUE_TYPE_OF_COLUMN =
            new HashMap<Column, XmpValueType>();

    static {
        // Not copied into other XMP from
        // data de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata when
        // XmpValueType.SEQ_PROPER_NAME
        //XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, XmpValueType.SEQ_PROPER_NAME);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE                     , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE                 , XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE                      , XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE             , XmpValueType.BAG_TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE                       , XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE          , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE       , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE      , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE        , XmpValueType.PROPER_NAME);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE                 , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE              , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE               , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE             , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE         , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE               , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE                , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpRating.INSTANCE                        , XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE       , XmpValueType.TEXT);
    }

    /**
     * XMP-Datentyp für einen Property Value.
     */
    public enum XmpValueType {

        /**
         * Name einer Person oder Organisation, Unicode-String
         */
        PROPER_NAME,
        /**
         * Unicode-String
         */
        TEXT,
        /**
         * Array mit Unicode-Strings in geordneter Reihenfolge,
         * wobei die Reihenfolge der Elemente keine Bedeutung hat; in
         * Schema Definitions ein <code>bag</code>
         */
        BAG_TEXT,
        /**
         * Array mit Namen von Personen oder Organisationen als Unicode-Strings
         * in geordneter Reihenfolge, wobei die Reihenfolge von Bedeutung ist;
         * in Schema Definitions eine <code>seq</code>
         */
        SEQ_PROPER_NAME,
        /**
         * Array mit Sprachalternativen als Unicode-Strings in geordneter
         * Reihenfolge, wobei eine Anwendung eine bestimmte der Alternativen
         * auswählen kann; in Schema Definitions eine <code>alt</code>
         */
        LANG_ALT
    }

    /**
     * Liefert den XMP-Datentyp eines Property-Werts für eine Spalte.
     *
     * @param  column  Spalte
     * @return Typ oder null bei ungültiger Spalte
     */
    public static XmpValueType getXmpValueTypeOfColumn(Column column) {
        return XMP_VALUE_TYPE_OF_COLUMN.get(column);
    }

    /**
     * Liefert, ob der Spaltenwert in einem Array zu speichern ist.
     *
     * @param  column  Spalte
     * @return true, wenn der Wert in einem Array zu speichern ist
     */
    public static boolean isArray(Column column) {
        XmpValueType valueType = XMP_VALUE_TYPE_OF_COLUMN.get(column);
        if (valueType != null) {
            return valueType.equals(XmpValueType.BAG_TEXT) ||
                    valueType.equals(XmpValueType.LANG_ALT) ||
                    valueType.equals(XmpValueType.SEQ_PROPER_NAME);
        }
        return false;
    }

    /**
     * Liefert, ob eine Spalte den Wert einer alternativen Sprache enthält.
     * Dies impliziert, dass für die Spalte gilt:
     * {@link #isArray(de.elmar_baumann.jpt.database.metadata.Column)}.
     *
     * @param   xmpColumn Spalte
     * @return  true, wenn der Spaltenwert für eine alternative Sprache gilt
     */
    public static boolean isLanguageAlternative(Column xmpColumn) {
        XmpValueType type = XmpColumnXmpDataTypeMapping.getXmpValueTypeOfColumn(xmpColumn);
        return type != null && type.equals(XmpValueType.LANG_ALT);
    }

    /**
     * Liefert, ob der Spaltenwert ein einfacher String ist.
     *
     * @param  column  Spalte
     * @return true, wenn der Spaltenwert ein einfacher String ist
     */
    public static boolean isText(Column column) {
        XmpValueType valueType = XMP_VALUE_TYPE_OF_COLUMN.get(column);
        if (valueType != null) {
            return valueType.equals(XmpValueType.TEXT) ||
                    valueType.equals(XmpValueType.PROPER_NAME);
        }
        return false;
    }

    private XmpColumnXmpDataTypeMapping() {
    }
}
