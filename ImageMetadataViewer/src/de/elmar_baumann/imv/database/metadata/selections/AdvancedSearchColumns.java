package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifDateTimeOriginal;
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
 * Spalten für die erweiterte Suche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/22
 */
public final class AdvancedSearchColumns {

    private static final List<Column> columns = new ArrayList<Column>();
    

    static {
        columns.add(ColumnXmpDcSubjectsSubject.getInstance());
        columns.add(ColumnXmpPhotoshopCategory.getInstance());
        columns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        columns.add(ColumnXmpDcTitle.getInstance());
        columns.add(ColumnXmpDcDescription.getInstance());
        columns.add(ColumnXmpPhotoshopHeadline.getInstance());
        columns.add(ColumnXmpIptc4xmpcoreLocation.getInstance());
        columns.add(ColumnXmpPhotoshopAuthorsposition.getInstance());
        columns.add(ColumnXmpDcCreator.getInstance());
        columns.add(ColumnXmpPhotoshopCity.getInstance());
        columns.add(ColumnXmpPhotoshopState.getInstance());
        columns.add(ColumnXmpPhotoshopCountry.getInstance());
        columns.add(ColumnXmpDcRights.getInstance());
        columns.add(ColumnXmpPhotoshopCredit.getInstance());
        columns.add(ColumnXmpPhotoshopSource.getInstance());
        columns.add(ColumnXmpPhotoshopTransmissionReference.getInstance());
        columns.add(ColumnXmpPhotoshopInstructions.getInstance());
        columns.add(ColumnXmpPhotoshopCaptionwriter.getInstance());
        columns.add(ColumnExifDateTimeOriginal.getInstance());
        columns.add(ColumnExifFocalLength.getInstance());
        columns.add(ColumnExifIsoSpeedRatings.getInstance());
        columns.add(ColumnExifRecordingEquipment.getInstance());
        columns.add(ColumnFilesFilename.getInstance());
    }

    /**
     * Liefert die Spalten für die erweiterte Suche.
     * 
     * @return Suchspalten
     */
    public static List<Column> get() {
        return columns;
    }
}
