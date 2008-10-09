package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.collections.ColumnCollectionnamesName;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifDateTimeOriginal;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesLastModified;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesThumbnail;
import de.elmar_baumann.imv.database.metadata.savedsearches.ColumnSavedSearchesName;
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
        columnOfId.put(7, ColumnXmpDcDescription.getInstance());
        columnOfId.put(8, ColumnXmpDcRights.getInstance());
        columnOfId.put(9, ColumnXmpDcTitle.getInstance());
        columnOfId.put(10, ColumnXmpIptc4xmpcoreCountrycode.getInstance());
        columnOfId.put(11, ColumnXmpIptc4xmpcoreLocation.getInstance());
        columnOfId.put(12, ColumnXmpPhotoshopAuthorsposition.getInstance());
        columnOfId.put(13, ColumnXmpPhotoshopCaptionwriter.getInstance());
        columnOfId.put(14, ColumnXmpPhotoshopCategory.getInstance());
        columnOfId.put(15, ColumnXmpPhotoshopCity.getInstance());
        columnOfId.put(16, ColumnXmpPhotoshopCountry.getInstance());
        columnOfId.put(17, ColumnXmpPhotoshopCredit.getInstance());
        columnOfId.put(18, ColumnXmpPhotoshopHeadline.getInstance());
        columnOfId.put(19, ColumnXmpPhotoshopInstructions.getInstance());
        columnOfId.put(20, ColumnXmpPhotoshopSource.getInstance());
        columnOfId.put(21, ColumnXmpPhotoshopState.getInstance());
        columnOfId.put(22, ColumnXmpPhotoshopTransmissionReference.getInstance());
        columnOfId.put(23, ColumnXmpDcCreator.getInstance());
        columnOfId.put(24, ColumnXmpDcSubjectsSubject.getInstance());
        columnOfId.put(25, ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        columnOfId.put(26, ColumnCollectionnamesName.getInstance());
        columnOfId.put(27, ColumnSavedSearchesName.getInstance());

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
