package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imagemetadataviewer.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imagemetadataviewer.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcKeywordsKeyword;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSupplementalCategoriesSupplementalCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcObjectName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationNamesContentLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCopyrightNotice;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCaptionAbstract;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import java.util.Vector;

/**
 * Spalten für die Schnellsuche.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/29
 */
public class FastSearchColumns {

    private static Vector<Column> searchColumns = new Vector<Column>();
    private static FastSearchColumns instance = new FastSearchColumns();
    

    static {
        // EXIF
        searchColumns.add(ColumnExifRecordingEquipment.getInstance());
        searchColumns.add(ColumnExifFocalLength.getInstance());
        searchColumns.add(ColumnExifIsoSpeedRatings.getInstance());
        // Files
        searchColumns.add(ColumnFilesFilename.getInstance());
        // IPTC
        searchColumns.add(ColumnIptcCaptionAbstract.getInstance());
        searchColumns.add(ColumnIptcCategory.getInstance());
        searchColumns.add(ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance());
        searchColumns.add(ColumnIptcKeywordsKeyword.getInstance());
        searchColumns.add(ColumnIptcCopyrightNotice.getInstance());
        searchColumns.add(ColumnIptcContentLocationNamesContentLocationName.getInstance());
        searchColumns.add(ColumnIptcHeadline.getInstance());
        searchColumns.add(ColumnIptcObjectName.getInstance());
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

    public Vector<Column> getSearchColumns() {
        return searchColumns;
    }

    private FastSearchColumns() {
    }
}
