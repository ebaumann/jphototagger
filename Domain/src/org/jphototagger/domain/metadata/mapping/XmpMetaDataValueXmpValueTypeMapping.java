package org.jphototagger.domain.metadata.mapping;

import java.util.HashMap;
import java.util.Map;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;

/**
 * Mapping zwischen einer XMP-Spalte
 * {@link org.jphototagger.program.database.metadata.MetaDataValue}
 * und einem XMP-Datentyp.
 *
 * @author Elmar Baumann
 */
public final class XmpMetaDataValueXmpValueTypeMapping {

    private static final Map<MetaDataValue, XmpValueType> XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE = new HashMap<MetaDataValue, XmpValueType>();

    static {

        // Not copied into other XMP from
        // data org.jphototagger.program.image.metadata.xmp.XmpMetadata when
        // XmpValueType.SEQ_PROPER_NAME
        // XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, XmpValueType.SEQ_PROPER_NAME);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, XmpValueType.BAG_TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, XmpValueType.LANG_ALT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, XmpValueType.PROPER_NAME);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopCreditMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, XmpValueType.TEXT);
        XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, XmpValueType.TEXT);
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
     * @param  value  Spalte
     * @return Typ oder null bei ungültiger Spalte
     */
    public static XmpValueType getXmpValueType(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.get(value);
    }

    /**
     * Liefert, ob der Spaltenwert in einem Array zu speichern ist.
     *
     * @param  value  Spalte
     * @return true, wenn der Wert in einem Array zu speichern ist
     */
    public static boolean isArray(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        XmpValueType valueType = XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.get(value);

        if (valueType != null) {
            return valueType.equals(XmpValueType.BAG_TEXT) || valueType.equals(XmpValueType.LANG_ALT)
                    || valueType.equals(XmpValueType.SEQ_PROPER_NAME);
        }

        return false;
    }

    /**
     * Liefert, ob eine Spalte den Wert einer alternativen Sprache enthält.
     * Dies impliziert, dass für die Spalte gilt:
     * {@link #isArray(org.jphototagger.program.database.metadata.MetaDataValue)}.
     *
     * @param   value Spalte
     * @return  true, wenn der Spaltenwert für eine alternative Sprache gilt
     */
    public static boolean isLanguageAlternative(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        XmpValueType type = XmpMetaDataValueXmpValueTypeMapping.getXmpValueType(value);

        return (type != null) && type.equals(XmpValueType.LANG_ALT);
    }

    /**
     * Liefert, ob der Spaltenwert ein einfacher String ist.
     *
     * @param  value  Spalte
     * @return true, wenn der Spaltenwert ein einfacher String ist
     */
    public static boolean isText(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        XmpValueType valueType = XMP_VALUE_TYPE_OF_XMP_META_DATA_VALUE.get(value);

        if (valueType != null) {
            return valueType.equals(XmpValueType.TEXT) || valueType.equals(XmpValueType.PROPER_NAME);
        }

        return false;
    }

    private XmpMetaDataValueXmpValueTypeMapping() {
    }
}
