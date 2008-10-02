package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
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
import java.util.Vector;

/**
 * XMP-Spalten die aktualisiert werden können.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class UpdateableXmpColumns {

    private static Vector<Column> columns = new Vector<Column>();
    private static UpdateableXmpColumns instance = new UpdateableXmpColumns();
    

    static {
        columns.add(ColumnXmpDcCreatorsCreator.getInstance());
        columns.add(ColumnXmpDcDescription.getInstance());
        columns.add(ColumnXmpDcRights.getInstance());
        columns.add(ColumnXmpDcSubjectsSubject.getInstance());
        columns.add(ColumnXmpDcTitle.getInstance());
        columns.add(ColumnXmpIptc4xmpcoreCountrycode.getInstance());
        columns.add(ColumnXmpIptc4xmpcoreLocation.getInstance());
        columns.add(ColumnXmpPhotoshopAuthorsposition.getInstance());
        columns.add(ColumnXmpPhotoshopCaptionwriter.getInstance());
        columns.add(ColumnXmpPhotoshopCategory.getInstance());
        columns.add(ColumnXmpPhotoshopCity.getInstance());
        columns.add(ColumnXmpPhotoshopCountry.getInstance());
        columns.add(ColumnXmpPhotoshopCredit.getInstance());
        columns.add(ColumnXmpPhotoshopHeadline.getInstance());
        columns.add(ColumnXmpPhotoshopInstructions.getInstance());
        columns.add(ColumnXmpPhotoshopSource.getInstance());
        columns.add(ColumnXmpPhotoshopState.getInstance());
        columns.add(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance());
        columns.add(ColumnXmpPhotoshopTransmissionReference.getInstance());
    }

    public static UpdateableXmpColumns getInstance() {
        return instance;
    }

    public Vector<Column> getColumns() {
        return columns;
    }

    private UpdateableXmpColumns() {
    }
}
