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

    private static final Map<Column, EditHints> editHintsForColumn = new LinkedHashMap<Column, EditHints>();
    

    static {
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.SMALL);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.LARGE);
        EditHints repeatableHint = new EditHints(true, SizeEditField.LARGE);
        editHintsForColumn.put(
            ColumnXmpDcSubjectsSubject.INSTANCE, repeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCategory.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, repeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcTitle.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcDescription.INSTANCE, notRepeatableHintLarge);
        editHintsForColumn.put(
            ColumnXmpPhotoshopHeadline.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreLocation.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopAuthorsposition.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcCreator.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCity.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopState.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCountry.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcRights.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCredit.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSource.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopTransmissionReference.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopInstructions.INSTANCE, notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCaptionwriter.INSTANCE, notRepeatableHintSmall);
    }

    public static Set<Column> getColumns() {
        return editHintsForColumn.keySet();
    }

    public static EditHints getEditHints(Column column) {
        return editHintsForColumn.get(column);
    }

    private EditColumns() {
    }
}
