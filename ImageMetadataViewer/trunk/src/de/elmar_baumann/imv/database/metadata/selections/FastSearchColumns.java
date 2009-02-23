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
public final class FastSearchColumns {

    private static final List<Column> searchColumns = new ArrayList<Column>();
    

    static {
        // XMP
        searchColumns.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        searchColumns.add(ColumnXmpPhotoshopCategory.INSTANCE);
        searchColumns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        searchColumns.add(ColumnXmpDcTitle.INSTANCE);
        searchColumns.add(ColumnXmpDcDescription.INSTANCE);
        searchColumns.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        searchColumns.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        searchColumns.add(ColumnXmpDcRights.INSTANCE);
        // EXIF
        searchColumns.add(ColumnExifFocalLength.INSTANCE);
        searchColumns.add(ColumnExifIsoSpeedRatings.INSTANCE);
        searchColumns.add(ColumnExifRecordingEquipment.INSTANCE);
        // Files
        searchColumns.add(ColumnFilesFilename.INSTANCE);
    }

    public static List<Column> get() {
        return searchColumns;
    }

    private FastSearchColumns() {
    }
}
