package org.jphototagger.xmp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.xmp.ColumnXmpDcCreator;
import org.jphototagger.domain.database.xmp.ColumnXmpDcDescription;
import org.jphototagger.domain.database.xmp.ColumnXmpDcRights;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.ColumnXmpDcTitle;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.domain.database.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.domain.database.xmp.ColumnXmpRating;
import org.jphototagger.xmp.EditHints.SizeEditField;

/**
 * Supported XMP columns for editing and updating XMP sidecar files.
 *
 * @author Elmar Baumann
 */
public final class EditColumns {

    private static final Map<Column, EditHints> EDIT_HINT_OF_COLUMN = new LinkedHashMap<Column, EditHints>();

    static {
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.SMALL);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.LARGE);
        EditHints repeatableHint = new EditHints(true, SizeEditField.LARGE);

        EDIT_HINT_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE, repeatableHint);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE, notRepeatableHintLarge);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_COLUMN.put(ColumnXmpRating.INSTANCE, notRepeatableHintSmall);
    }

    public static List<Column> get() {
        return new ArrayList<Column>(EDIT_HINT_OF_COLUMN.keySet());
    }

    public static EditHints getEditHints(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return EDIT_HINT_OF_COLUMN.get(column);
    }

    private EditColumns() {
    }
}
