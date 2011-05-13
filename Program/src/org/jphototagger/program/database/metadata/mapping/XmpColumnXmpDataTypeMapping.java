package org.jphototagger.program.database.metadata.mapping;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen einer XMP-Spalte
 * {@link org.jphototagger.program.database.metadata.Column}
 * und einem XMP-Datentyp.
 *
 * @author Elmar Baumann
 */
public final class XmpColumnXmpDataTypeMapping {
    private static final Map<Column, XmpValueType> XMP_VALUE_TYPE_OF_COLUMN = new HashMap<Column, XmpValueType>();

    static {

        // Not copied into other XMP from
        // data org.jphototagger.program.image.metadata.xmp.XmpMetadata when
        // XmpValueType.SEQ_PROPER_NAME
        // XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, XmpValueType.SEQ_PROPER_NAME);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE, XmpValueType.BAG_TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, XmpValueType.PROPER_NAME);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpRating.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, XmpValueType.TEXT);
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
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return XMP_VALUE_TYPE_OF_COLUMN.get(column);
    }

    /**
     * Liefert, ob der Spaltenwert in einem Array zu speichern ist.
     *
     * @param  column  Spalte
     * @return true, wenn der Wert in einem Array zu speichern ist
     */
    public static boolean isArray(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        XmpValueType valueType = XMP_VALUE_TYPE_OF_COLUMN.get(column);

        if (valueType != null) {
            return valueType.equals(XmpValueType.BAG_TEXT) || valueType.equals(XmpValueType.LANG_ALT)
                   || valueType.equals(XmpValueType.SEQ_PROPER_NAME);
        }

        return false;
    }

    /**
     * Liefert, ob eine Spalte den Wert einer alternativen Sprache enthält.
     * Dies impliziert, dass für die Spalte gilt:
     * {@link #isArray(org.jphototagger.program.database.metadata.Column)}.
     *
     * @param   xmpColumn Spalte
     * @return  true, wenn der Spaltenwert für eine alternative Sprache gilt
     */
    public static boolean isLanguageAlternative(Column xmpColumn) {
        if (xmpColumn == null) {
            throw new NullPointerException("xmpColumn == null");
        }

        XmpValueType type = XmpColumnXmpDataTypeMapping.getXmpValueTypeOfColumn(xmpColumn);

        return (type != null) && type.equals(XmpValueType.LANG_ALT);
    }

    /**
     * Liefert, ob der Spaltenwert ein einfacher String ist.
     *
     * @param  column  Spalte
     * @return true, wenn der Spaltenwert ein einfacher String ist
     */
    public static boolean isText(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        XmpValueType valueType = XMP_VALUE_TYPE_OF_COLUMN.get(column);

        if (valueType != null) {
            return valueType.equals(XmpValueType.TEXT) || valueType.equals(XmpValueType.PROPER_NAME);
        }

        return false;
    }

    private XmpColumnXmpDataTypeMapping() {}
}
