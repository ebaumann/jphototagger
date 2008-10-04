package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditHints.SizeEditField;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcCreator;
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
 * Supported XMP columns for editing and updating XMP sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class EditColumns {

    private static HashMap<Column, EditHints> editHintsForColumn = new HashMap<Column, EditHints>();
    private static EditColumns instance = new EditColumns();
    

    static {
        EditHints notRepeatableHint = new EditHints(false, SizeEditField.small);
        EditHints repeatableHint = new EditHints(true, SizeEditField.large);
        editHintsForColumn.put(
            ColumnXmpDcCreator.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcDescription.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcRights.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcSubjectsSubject.getInstance(),
            repeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcTitle.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreCountrycode.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreLocation.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopAuthorsposition.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCaptionwriter.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCategory.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCity.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCountry.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCredit.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopHeadline.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopInstructions.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSource.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopState.getInstance(),
            notRepeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(),
            repeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopTransmissionReference.getInstance(),
            notRepeatableHint);
    }

    public static EditColumns getInstance() {
        return instance;
    }

    public Set<Column> getColumns() {
        return editHintsForColumn.keySet();
    }

    public EditHints getEditHintsForColumn(Column column) {
        return editHintsForColumn.get(column);
    }

    private EditColumns() {
    }
}
