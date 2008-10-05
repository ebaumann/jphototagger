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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Supported XMP columns for editing and updating XMP sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class EditColumns {

    private static Map<Column, EditHints> editHintsForColumn = new LinkedHashMap<Column, EditHints>();
    private static EditColumns instance = new EditColumns();
    

    static {
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.small);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.large);
        EditHints repeatableHint = new EditHints(true, SizeEditField.large);
        // Remain order! Database.insertMetaDataEditTemplate() a.o. depends on it
        editHintsForColumn.put(
            ColumnXmpDcSubjectsSubject.getInstance(), repeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcTitle.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopHeadline.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcDescription.getInstance(), notRepeatableHintLarge);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCaptionwriter.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreLocation.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreCountrycode.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCategory.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), repeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcRights.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcCreator.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopAuthorsposition.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCity.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopState.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCountry.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopTransmissionReference.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopInstructions.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCredit.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSource.getInstance(), notRepeatableHintSmall);
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
