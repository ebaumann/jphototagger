package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.EditHints.SizeEditField;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Supported XMP columns for editing and updating XMP sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class EditColumns {

    private static final Map<Column, EditHints> EDIT_HINT_OF_COLUMN =
            new LinkedHashMap<Column, EditHints>();
    

    static {
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.SMALL);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.LARGE);
        EditHints repeatableHint = new EditHints(true, SizeEditField.LARGE);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpDcSubjectsSubject.INSTANCE, repeatableHint);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopCategory.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, repeatableHint);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpDcTitle.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpDcDescription.INSTANCE, notRepeatableHintLarge);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopHeadline.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpIptc4xmpcoreLocation.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopAuthorsposition.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpDcCreator.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopCity.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopState.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopCountry.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpDcRights.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopCredit.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopSource.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopTransmissionReference.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopInstructions.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(
            ColumnXmpPhotoshopCaptionwriter.INSTANCE, notRepeatableHintSmall);
    }

    public static Set<Column> get() {
        return EDIT_HINT_OF_COLUMN.keySet();
    }

    public static EditHints getEditHints(Column column) {
        return EDIT_HINT_OF_COLUMN.get(column);
    }

    private EditColumns() {
    }
}
