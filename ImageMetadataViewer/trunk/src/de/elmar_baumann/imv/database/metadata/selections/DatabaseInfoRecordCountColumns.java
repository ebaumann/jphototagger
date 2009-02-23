package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/08
 */
public final class DatabaseInfoRecordCountColumns {

    private static final List<Column> columns = new ArrayList<Column>();
    

    static {
        columns.add(ColumnFilesFilename.INSTANCE);
        columns.add(ColumnXmpDcSubjectsSubject.INSTANCE);
        columns.add(ColumnXmpPhotoshopCategory.INSTANCE);
        columns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE);
        columns.add(ColumnXmpDcTitle.INSTANCE);
        columns.add(ColumnXmpDcDescription.INSTANCE);
        columns.add(ColumnXmpPhotoshopHeadline.INSTANCE);
        columns.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        columns.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        columns.add(ColumnXmpDcCreator.INSTANCE);
        columns.add(ColumnXmpPhotoshopCity.INSTANCE);
        columns.add(ColumnXmpPhotoshopState.INSTANCE);
        columns.add(ColumnXmpPhotoshopCountry.INSTANCE);
        columns.add(ColumnXmpDcRights.INSTANCE);
        columns.add(ColumnXmpPhotoshopCredit.INSTANCE);
        columns.add(ColumnXmpPhotoshopSource.INSTANCE);
        columns.add(ColumnXmpPhotoshopTransmissionReference.INSTANCE);
        columns.add(ColumnXmpPhotoshopInstructions.INSTANCE);
        columns.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        columns.add(ColumnExifFocalLength.INSTANCE);
        columns.add(ColumnExifIsoSpeedRatings.INSTANCE);
        columns.add(ColumnExifRecordingEquipment.INSTANCE);
    }

    public static List<Column> get() {
        return columns;
    }

    private DatabaseInfoRecordCountColumns() {
    }
}
