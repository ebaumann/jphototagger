package de.elmar_baumann.imv.database.metadata.mapping;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import java.util.HashMap;

/**
 * Mapping zwischen einer XMP-Spalte
 * {@link de.elmar_baumann.imagemetadataviewer.database.metadata.Column}
 * und einem XMP-Datentyp.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/20
 */
public class XmpColumnXmpDataTypeMapping {

    private static HashMap<Column, XmpValueType> xmpValueTypeOfColumn = new HashMap<Column, XmpValueType>();
    private static XmpColumnXmpDataTypeMapping instance = new XmpColumnXmpDataTypeMapping();
    

    static {
        xmpValueTypeOfColumn.put(ColumnXmpDcCreator.getInstance(), XmpValueType.SeqProperName);
        xmpValueTypeOfColumn.put(ColumnXmpDcDescription.getInstance(), XmpValueType.LangAlt);
        xmpValueTypeOfColumn.put(ColumnXmpDcRights.getInstance(), XmpValueType.LangAlt);
        xmpValueTypeOfColumn.put(ColumnXmpDcSubjectsSubject.getInstance(), XmpValueType.BagText);
        xmpValueTypeOfColumn.put(ColumnXmpDcTitle.getInstance(), XmpValueType.LangAlt);
        xmpValueTypeOfColumn.put(ColumnXmpIptc4xmpcoreLocation.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopAuthorsposition.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopCaptionwriter.getInstance(), XmpValueType.ProperName);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopCategory.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopCity.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopCountry.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopCredit.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopHeadline.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopInstructions.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopSource.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopState.getInstance(), XmpValueType.Text);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), XmpValueType.BagText);
        xmpValueTypeOfColumn.put(ColumnXmpPhotoshopTransmissionReference.getInstance(), XmpValueType.Text);
    }

    /**
     * XMP-Datentyp für einen Property Value.
     */
    public enum XmpValueType {

        /**
         * Name einer Person oder Organisation, Unicode-String
         */
        ProperName,
        /**
         * Unicode-String
         */
        Text,
        /**
         * Array mit Unicode-Strings in geordneter Reihenfolge,
         * wobei die Reihenfolge der Elemente keine Bedeutung hat; in
         * Schema Definitions ein <code>bag</code>
         */
        BagText,
        /**
         * Array mit Namen von Personen oder Organisationen als Unicode-Strings
         * in geordneter Reihenfolge, wobei die Reihenfolge von Bedeutung ist;
         * in Schema Definitions eine <code>seq</code>
         */
        SeqProperName,
        /**
         * Array mit Sprachalternativen als Unicode-Strings in geordneter
         * Reihenfolge, wobei eine Anwendung eine bestimmte der Alternativen
         * auswählen kann; in Schema Definitions eine <code>alt</code>
         */
        LangAlt
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static XmpColumnXmpDataTypeMapping getInstance() {
        return instance;
    }

    /**
     * Liefert den XMP-Datentyp eines Property-Werts für eine Spalte.
     * 
     * @param  column  Spalte
     * @return Typ oder null bei ungültiger Spalte
     */
    public XmpValueType getXmpValueTypeOfColumn(Column column) {
        return xmpValueTypeOfColumn.get(column);
    }

    /**
     * Liefert, ob der Spaltenwert in einem Array zu speichern ist.
     * 
     * @param  column  Spalte
     * @return true, wenn der Wert in einem Array zu speichern ist
     */
    public boolean isArray(Column column) {
        XmpValueType valueType = xmpValueTypeOfColumn.get(column);
        if (valueType != null) {
            return valueType.equals(XmpValueType.BagText) ||
                valueType.equals(XmpValueType.LangAlt) ||
                valueType.equals(XmpValueType.SeqProperName);
        }
        return false;
    }

    /**
     * Liefert, ob eine Spalte den Wert einer alternativen Sprache enthält.
     * Dies impliziert, dass für die Spalte gilt:
     * {@link #isArray(de.elmar_baumann.imagemetadataviewer.database.metadata.Column)}.
     * 
     * @param   xmpColumn Spalte
     * @return  true, wenn der Spaltenwert für eine alternative Sprache gilt
     */
    public boolean isLanguageAlternative(Column xmpColumn) {
        XmpValueType type = XmpColumnXmpDataTypeMapping.getInstance().
            getXmpValueTypeOfColumn(xmpColumn);
        return type != null && type.equals(XmpValueType.LangAlt);
    }

    /**
     * Liefert, ob der Spaltenwert ein einfacher String ist.
     * 
     * @param  column  Spalte
     * @return true, wenn der Spaltenwert ein einfacher String ist
     */
    public boolean isText(Column column) {
        XmpValueType valueType = xmpValueTypeOfColumn.get(column);
        if (valueType != null) {
            return valueType.equals(XmpValueType.Text) ||
                valueType.equals(XmpValueType.ProperName);
        }
        return false;
    }

    private XmpColumnXmpDataTypeMapping() {
    }
}
