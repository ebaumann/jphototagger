package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import java.util.ArrayList;
import java.util.List;

/**
 * Spalten für die Schnellsuche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/29
 */
public class FastSearchColumns {

    private static List<Column> searchColumns = new ArrayList<Column>();
    private static FastSearchColumns instance = new FastSearchColumns();
    

    static {
        // EXIF
        searchColumns.add(ColumnExifRecordingEquipment.getInstance());
        searchColumns.add(ColumnExifFocalLength.getInstance());
        searchColumns.add(ColumnExifIsoSpeedRatings.getInstance());
        // Files
        searchColumns.add(ColumnFilesFilename.getInstance());
        // XMP
        searchColumns.add(ColumnXmpDcDescription.getInstance());
        searchColumns.add(ColumnXmpPhotoshopCategory.getInstance());
        searchColumns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        searchColumns.add(ColumnXmpDcSubjectsSubject.getInstance());
        searchColumns.add(ColumnXmpDcRights.getInstance());
        searchColumns.add(ColumnXmpIptc4xmpcoreLocation.getInstance());
        searchColumns.add(ColumnXmpPhotoshopHeadline.getInstance());
        searchColumns.add(ColumnXmpDcTitle.getInstance());
    }

    public static FastSearchColumns getInstance() {
        return instance;
    }

    public List<Column> getSearchColumns() {
        return searchColumns;
    }

    private FastSearchColumns() {
    }
}
