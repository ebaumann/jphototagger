package de.elmar_baumann.imagemetadataviewer.database.metadata.mapping;

import com.imagero.reader.iptc.IPTCEntryMeta;
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
import java.util.HashMap;

/**
 * Mapping zwischen
 * {@link com.imagero.reader.iptc.IPTCEntryMeta}
 * und {@link de.elmar_baumann.imagemetadataviewer.database.metadata.Column}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class IptcEntryMetaIptcColumnMapping {

    private static HashMap<Column, IPTCEntryMeta> iptcEntryMetaOfColumn = new HashMap<Column, IPTCEntryMeta>();
    private static HashMap<IPTCEntryMeta, Column> columnOfIptcEntryMeta = new HashMap<IPTCEntryMeta, Column>();
    private static IptcEntryMetaIptcColumnMapping instance = new IptcEntryMetaIptcColumnMapping();
    

    static {
        iptcEntryMetaOfColumn.put(ColumnIptcByLinesByLine.getInstance(), IPTCEntryMeta.BYLINE);
        iptcEntryMetaOfColumn.put(ColumnIptcByLinesTitlesByLineTitle.getInstance(), IPTCEntryMeta.BYLINE_TITLE);
        iptcEntryMetaOfColumn.put(ColumnIptcCaptionAbstract.getInstance(), IPTCEntryMeta.CAPTION_ABSTRACT);
        iptcEntryMetaOfColumn.put(ColumnIptcCategory.getInstance(), IPTCEntryMeta.CATEGORY);
        iptcEntryMetaOfColumn.put(ColumnIptcCity.getInstance(), IPTCEntryMeta.CITY);
        iptcEntryMetaOfColumn.put(ColumnIptcContentLocationCodesContentLocationCode.getInstance(), IPTCEntryMeta.CONTENT_LOCATION_CODE);
        iptcEntryMetaOfColumn.put(ColumnIptcContentLocationNamesContentLocationName.getInstance(), IPTCEntryMeta.CONTENT_LOCATION_NAME);
        iptcEntryMetaOfColumn.put(ColumnIptcCopyrightNotice.getInstance(), IPTCEntryMeta.COPYRIGHT_NOTICE);
        iptcEntryMetaOfColumn.put(ColumnIptcCountryPrimaryLocationName.getInstance(), IPTCEntryMeta.COUNTRY_PRIMARY_LOCATION_NAME);
        iptcEntryMetaOfColumn.put(ColumnIptcCredit.getInstance(), IPTCEntryMeta.CREDIT);
        iptcEntryMetaOfColumn.put(ColumnIptcHeadline.getInstance(), IPTCEntryMeta.HEADLINE);
        iptcEntryMetaOfColumn.put(ColumnIptcKeywordsKeyword.getInstance(), IPTCEntryMeta.KEYWORDS);
        iptcEntryMetaOfColumn.put(ColumnIptcObjectName.getInstance(), IPTCEntryMeta.OBJECT_NAME);
        iptcEntryMetaOfColumn.put(ColumnIptcProvinceState.getInstance(), IPTCEntryMeta.PROVINCE_STATE);
        iptcEntryMetaOfColumn.put(ColumnIptcOriginalTransmissionReference.getInstance(), IPTCEntryMeta.ORIGINAL_TRANSMISSION_REFERENCE);
        iptcEntryMetaOfColumn.put(ColumnIptcSource.getInstance(), IPTCEntryMeta.SOURCE);
        iptcEntryMetaOfColumn.put(ColumnIptcSpecialInstructions.getInstance(), IPTCEntryMeta.SPECIAL_INSTRUCTIONS);
        iptcEntryMetaOfColumn.put(ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance(), IPTCEntryMeta.SUPPLEMENTAL_CATEGORY);
        iptcEntryMetaOfColumn.put(ColumnIptcWritersEditorsWriterEditor.getInstance(), IPTCEntryMeta.WRITER_EDITOR);

        for (Column column : iptcEntryMetaOfColumn.keySet()) {
            columnOfIptcEntryMeta.put(iptcEntryMetaOfColumn.get(column), column);
        }
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static IptcEntryMetaIptcColumnMapping getInstance() {
        return instance;
    }

    /**
     * Liefert die Metadaten des Entrys für eine Spalte.
     * 
     * @param  column Spalte
     * @return Metadaten des Entrys oder null bei nicht zugeordneter Spalte
     */
    public IPTCEntryMeta getEntryMetaOfColumn(Column column) {
        return iptcEntryMetaOfColumn.get(column);
    }

    /**
     * Liefert eine Spalte für Metadaten eines Entrys.
     * 
     * @param  entry Metadaten des Entrys
     * @return Spalte oder null wenn nicht zugeordnet
     */
    public Column getColumnOfEntryMeta(IPTCEntryMeta entry) {
        return columnOfIptcEntryMeta.get(entry);
    }

    private IptcEntryMetaIptcColumnMapping() {
    }
}
