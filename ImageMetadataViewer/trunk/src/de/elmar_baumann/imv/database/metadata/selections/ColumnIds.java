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
import java.util.Map;
import java.util.Set;

/**
 * IDs der Tabellenspalten, die für den Benutzer relevant sind. So kann in der
 * Datenbank eine ID abgespeichert werden, aus der eindeutig die Tabellenspalte
 * ermittelt werden kann.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/13
 */
public final class ColumnIds {

    private static final Map<Integer, Column> columnOfId = new HashMap<Integer, Column>();
    private static final Map<Column, Integer> idOfColumn = new HashMap<Column, Integer>();
    

    static {
        // TODO PERMANENT: Neue für den Benutzer relevante Spalten hinzufügen
        // Vergebene IDs dürfen NIE verändert werden
        columnOfId.put(0, ColumnExifDateTimeOriginal.INSTANCE);
        columnOfId.put(1, ColumnExifFocalLength.INSTANCE);
        columnOfId.put(2, ColumnExifIsoSpeedRatings.INSTANCE);
        columnOfId.put(3, ColumnExifRecordingEquipment.INSTANCE);
        columnOfId.put(4, ColumnFilesFilename.INSTANCE);
        columnOfId.put(5, ColumnFilesLastModified.INSTANCE);
        columnOfId.put(6, ColumnFilesThumbnail.INSTANCE);
        columnOfId.put(7, ColumnXmpDcDescription.INSTANCE);
        columnOfId.put(8, ColumnXmpDcRights.INSTANCE);
        columnOfId.put(9, ColumnXmpDcTitle.INSTANCE);
        columnOfId.put(10, ColumnXmpIptc4xmpcoreCountrycode.INSTANCE);
        columnOfId.put(11, ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        columnOfId.put(12, ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        columnOfId.put(13, ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        columnOfId.put(14, ColumnXmpPhotoshopCategory.INSTANCE);
        columnOfId.put(15, ColumnXmpPhotoshopCity.INSTANCE);
        columnOfId.put(16, ColumnXmpPhotoshopCountry.INSTANCE);
        columnOfId.put(17, ColumnXmpPhotoshopCredit.INSTANCE);
        columnOfId.put(18, ColumnXmpPhotoshopHeadline.INSTANCE);
        columnOfId.put(19, ColumnXmpPhotoshopInstructions.INSTANCE);
        columnOfId.put(20, ColumnXmpPhotoshopSource.INSTANCE);
        columnOfId.put(21, ColumnXmpPhotoshopState.INSTANCE);
        columnOfId.put(22, ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        columnOfId.put(23, ColumnXmpDcCreator.INSTANCE);
        columnOfId.put(24, ColumnXmpDcSubjectsSubject.INSTANCE);
        columnOfId.put(25, ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        columnOfId.put(26, ColumnCollectionnamesName.INSTANCE);
        columnOfId.put(27, ColumnSavedSearchesName.INSTANCE);

        Set<Integer> keys = columnOfId.keySet();
        for (Integer key : keys) {
            idOfColumn.put(columnOfId.get(key), key);
        }
    }

    /**
     * Liefert eine Spalte mit bestimmter ID.
     * 
     * @param  id ID
     * @return Spalte oder null bei ungültiger ID
     */
    public static Column getColumn(int id) {
        return columnOfId.get(id);
    }

    /**
     * Liefert die ID einer Spalte.
     * 
     * @param  column Spalte
     * @return ID
     */
    public static int getId(Column column) {
        return idOfColumn.get(column);
    }

    private ColumnIds() {
    }
}
