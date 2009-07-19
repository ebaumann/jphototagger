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
 * @version 2008-07-29
 */
public final class FastSearchColumns {

    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {
        // XMP
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCategory.INSTANCE);
        COLUMNS.add(
                ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        COLUMNS.add(ColumnXmpDcTitle.INSTANCE);
        COLUMNS.add(ColumnXmpDcDescription.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        // EXIF
        COLUMNS.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS.add(ColumnExifIsoSpeedRatings.INSTANCE);
        COLUMNS.add(ColumnExifRecordingEquipment.INSTANCE);
        // Files
        COLUMNS.add(ColumnFilesFilename.INSTANCE);
    }

    public static List<Column> get() {
        return COLUMNS;
    }

    private FastSearchColumns() {
    }
}
