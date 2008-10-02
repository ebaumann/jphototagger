package de.elmar_baumann.imagemetadataviewer.database.metadata.mapping;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesByLine;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesTitlesByLineTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCaptionAbstract;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationCodesContentLocationCode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationNamesContentLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCopyrightNotice;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCountryPrimaryLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCredit;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcKeywordsKeyword;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcObjectName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcOriginalTransmissionReference;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcProvinceState;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSource;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSpecialInstructions;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSupplementalCategoriesSupplementalCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcWritersEditorsWriterEditor;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcCreatorsCreator;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.lib.template.Pair;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * Mapping zwischen IPTC- und XMP-Spalten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class IptcXmpMapping {

    private static HashMap<Column, Column> xmpColumnOfIptcColumn = new HashMap<Column, Column>();
    private static HashMap<Column, Column> iptcColumnOfXmpColumn = new HashMap<Column, Column>();
    private static IptcXmpMapping instance = new IptcXmpMapping();
    

    static {
        xmpColumnOfIptcColumn.put(ColumnIptcCopyrightNotice.getInstance(), ColumnXmpDcRights.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcCaptionAbstract.getInstance(), ColumnXmpDcDescription.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcObjectName.getInstance(), ColumnXmpDcTitle.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcHeadline.getInstance(), ColumnXmpPhotoshopHeadline.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcCategory.getInstance(), ColumnXmpPhotoshopCategory.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcCity.getInstance(), ColumnXmpPhotoshopCity.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcProvinceState.getInstance(), ColumnXmpPhotoshopState.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcCountryPrimaryLocationName.getInstance(), ColumnXmpPhotoshopCountry.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcOriginalTransmissionReference.getInstance(), ColumnXmpPhotoshopTransmissionReference.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcSpecialInstructions.getInstance(), ColumnXmpPhotoshopInstructions.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcCredit.getInstance(), ColumnXmpPhotoshopCredit.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcSource.getInstance(), ColumnXmpPhotoshopSource.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcKeywordsKeyword.getInstance(), ColumnXmpDcSubjectsSubject.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcByLinesByLine.getInstance(), ColumnXmpDcCreatorsCreator.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcContentLocationNamesContentLocationName.getInstance(), ColumnXmpIptc4xmpcoreLocation.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcContentLocationCodesContentLocationCode.getInstance(), ColumnXmpIptc4xmpcoreCountrycode.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcWritersEditorsWriterEditor.getInstance(), ColumnXmpPhotoshopCaptionwriter.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance(), ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        xmpColumnOfIptcColumn.put(ColumnIptcByLinesTitlesByLineTitle.getInstance(), ColumnXmpPhotoshopAuthorsposition.getInstance());

        for (Column iptcColumn : xmpColumnOfIptcColumn.keySet()) {
            iptcColumnOfXmpColumn.put(xmpColumnOfIptcColumn.get(iptcColumn), iptcColumn);
        }
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Klasseninstanz
     */
    public static IptcXmpMapping getInstance() {
        return instance;
    }

    /**
     * Liefert die XMP-Spalte mit gleicher Bedeutung wie die IPTC-Spalte.
     * 
     * @param  iptcColumn  IPTC-Spalte
     * @return XMP-Spalte oder null, falls es keine Entsprechung gibt
     */
    public Column getXmpColumnOfIptcColumn(Column iptcColumn) {
        return xmpColumnOfIptcColumn.get(iptcColumn);
    }

    /**
     * Liefert die IPTC-Spalte mit gleicher Bedeutung wie die XMP-Spalte.
     * 
     * @param  xmpColumn  XMP-Spalte
     * @return IPTC-Spalte oder null, falls es keine Entsprechung gibt
     */
    public Column getIptcColumnOfXmpColumn(Column xmpColumn) {
        return iptcColumnOfXmpColumn.get(xmpColumn);
    }

    /**
     * Liefert paarweise die gemappten Spalten.
     * 
     * @return Spaltenpaare
     */
    public Vector<Pair<Column, Column>> getAllPairs() {
        Vector<Pair<Column, Column>> pairs = new Vector<Pair<Column, Column>>();
        Set<Column> iptcCols = xmpColumnOfIptcColumn.keySet();
        for (Column iptcCol : iptcCols) {
            pairs.add(new Pair<Column, Column>(iptcCol, xmpColumnOfIptcColumn.get(iptcCol)));
        }
        return pairs;
    }

    /**
     * Liefert die beiden Spalten gemeinsame Beschreibung: Es wird die IPTC-
     * Beschreibung geliefert ausschließlich " [...".
     * 
     * @param  iptcColumn IPTC-Spalte
     * @return Beschreibung
     */
    public String getCommonDiscription(Column iptcColumn) {
        String description = iptcColumn.getDescription();
        String commonDescription = description;
        int index = description.indexOf("["); // NOI18N
        if (index > 0) {
            commonDescription = description.substring(0, index - 1);
        }
        return commonDescription;
    }

    private IptcXmpMapping() {
    }
}
