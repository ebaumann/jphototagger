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
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.small);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.large);
        EditHints repeatableHint = new EditHints(true, SizeEditField.large);
        editHintsForColumn.put(
            ColumnXmpDcSubjectsSubject.getInstance(), repeatableHint);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCategory.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), repeatableHint);
        editHintsForColumn.put(
            ColumnXmpDcTitle.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcDescription.getInstance(), notRepeatableHintLarge);
        editHintsForColumn.put(
            ColumnXmpPhotoshopHeadline.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpIptc4xmpcoreLocation.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopAuthorsposition.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcCreator.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCity.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopState.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCountry.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpDcRights.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCredit.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopSource.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopTransmissionReference.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopInstructions.getInstance(), notRepeatableHintSmall);
        editHintsForColumn.put(
            ColumnXmpPhotoshopCaptionwriter.getInstance(), notRepeatableHintSmall);
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
