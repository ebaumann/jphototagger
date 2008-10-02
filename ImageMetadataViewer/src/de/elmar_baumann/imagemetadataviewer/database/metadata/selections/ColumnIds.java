package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.collections.ColumnCollectionnamesName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifDateTimeOriginal;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesThumbnail;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesByLine;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesTitlesByLineTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCaptionAbstract;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationCodesContentLocationCode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationNamesContentLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCopyrightNotice;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCountryPrimaryLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCreationDate;
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
import de.elmar_baumann.imagemetadataviewer.database.metadata.savedsearches.ColumnSavedSearchesName;
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
import java.util.HashMap;
import java.util.Set;

/**
 * IDs der Tabellenspalten, die für den Benutzer relevant sind. So kann in der
 * Datenbank eine ID abgespeichert werden, aus der eindeutig die Tabellenspalte
 * ermittelt werden kann.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 */
public class ColumnIds {

    private static HashMap<Integer, Column> columnOfId = new HashMap<Integer, Column>();
    private static HashMap<Column, Integer> idOfColumn = new HashMap<Column, Integer>();
    private static ColumnIds instance = new ColumnIds();
    

    static {
        // TODO PERMANENT: Neue für den Benutzer relevante Spalten hinzufügen
        // Die Reihenfolge darf NIE verändert werden und ist bestimmt durch:
        // 1. Reihenfolge der hinzugefügten Tabellen in de.elmar_baumann.imagemetadataviewer.database.metadata.AllTables.get()
        // 2. Reihenfolge der Spalten durch addColumns() in jeder der hinugefügten Tabellen
        columnOfId.put(0, ColumnExifDateTimeOriginal.getInstance());
        columnOfId.put(1, ColumnExifFocalLength.getInstance());
        columnOfId.put(2, ColumnExifIsoSpeedRatings.getInstance());
        columnOfId.put(3, ColumnExifRecordingEquipment.getInstance());
        columnOfId.put(4, ColumnFilesFilename.getInstance());
        columnOfId.put(5, ColumnFilesLastModified.getInstance());
        columnOfId.put(6, ColumnFilesThumbnail.getInstance());
        columnOfId.put(7, ColumnIptcCopyrightNotice.getInstance());
        columnOfId.put(8, ColumnIptcCreationDate.getInstance());
        columnOfId.put(9, ColumnIptcCaptionAbstract.getInstance());
        columnOfId.put(10, ColumnIptcObjectName.getInstance());
        columnOfId.put(11, ColumnIptcHeadline.getInstance());
        columnOfId.put(12, ColumnIptcCategory.getInstance());
        columnOfId.put(13, ColumnIptcCity.getInstance());
        columnOfId.put(14, ColumnIptcProvinceState.getInstance());
        columnOfId.put(15, ColumnIptcCountryPrimaryLocationName.getInstance());
        columnOfId.put(16, ColumnIptcOriginalTransmissionReference.getInstance());
        columnOfId.put(17, ColumnIptcSpecialInstructions.getInstance());
        columnOfId.put(18, ColumnIptcCredit.getInstance());
        columnOfId.put(19, ColumnIptcSource.getInstance());
        columnOfId.put(20, ColumnIptcByLinesByLine.getInstance());
        columnOfId.put(21, ColumnIptcByLinesTitlesByLineTitle.getInstance());
        columnOfId.put(22, ColumnIptcContentLocationCodesContentLocationCode.getInstance());
        columnOfId.put(23, ColumnIptcContentLocationNamesContentLocationName.getInstance());
        columnOfId.put(24, ColumnIptcKeywordsKeyword.getInstance());
        columnOfId.put(25, ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance());
        columnOfId.put(26, ColumnIptcWritersEditorsWriterEditor.getInstance());
        columnOfId.put(27, ColumnXmpDcDescription.getInstance());
        columnOfId.put(28, ColumnXmpDcRights.getInstance());
        columnOfId.put(29, ColumnXmpDcTitle.getInstance());
        columnOfId.put(30, ColumnXmpIptc4xmpcoreCountrycode.getInstance());
        columnOfId.put(31, ColumnXmpIptc4xmpcoreLocation.getInstance());
        columnOfId.put(32, ColumnXmpPhotoshopAuthorsposition.getInstance());
        columnOfId.put(33, ColumnXmpPhotoshopCaptionwriter.getInstance());
        columnOfId.put(34, ColumnXmpPhotoshopCategory.getInstance());
        columnOfId.put(35, ColumnXmpPhotoshopCity.getInstance());
        columnOfId.put(36, ColumnXmpPhotoshopCountry.getInstance());
        columnOfId.put(37, ColumnXmpPhotoshopCredit.getInstance());
        columnOfId.put(38, ColumnXmpPhotoshopHeadline.getInstance());
        columnOfId.put(39, ColumnXmpPhotoshopInstructions.getInstance());
        columnOfId.put(40, ColumnXmpPhotoshopSource.getInstance());
        columnOfId.put(41, ColumnXmpPhotoshopState.getInstance());
        columnOfId.put(42, ColumnXmpPhotoshopTransmissionReference.getInstance());
        columnOfId.put(43, ColumnXmpDcCreatorsCreator.getInstance());
        columnOfId.put(44, ColumnXmpDcSubjectsSubject.getInstance());
        columnOfId.put(45, ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        columnOfId.put(46, ColumnCollectionnamesName.getInstance());
        columnOfId.put(47, ColumnSavedSearchesName.getInstance());

        Set<Integer> keys = columnOfId.keySet();
        for (Integer key : keys) {
            idOfColumn.put(columnOfId.get(key), key);
        }
    }

    public static ColumnIds getInstance() {
        return instance;
    }

    /**
     * Liefert eine Spalte mit bestimmter ID.
     * 
     * @param  id ID
     * @return Spalte oder null bei ungültiger ID
     */
    public Column getColumn(int id) {
        return columnOfId.get(id);
    }

    /**
     * Liefert die ID einer Spalte.
     * 
     * @param  column Spalte
     * @return ID
     */
    public int getId(Column column) {
        return idOfColumn.get(column);
    }

    private ColumnIds() {
    }
}
