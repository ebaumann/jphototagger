package org.jphototagger.xmp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.xmp.EditHints.SizeEditField;

/**
 * Supported XMP metadata values for editing and updating XMP sidecar files.
 *
 * @author Elmar Baumann
 */
public final class EditableMetaDataValues {

    private static final Map<MetaDataValue, EditHints> EDIT_HINT_OF_META_DATA_VALUE = new LinkedHashMap<>();

    static {
        EditHints notRepeatableHintSmall = new EditHints(false, SizeEditField.SMALL);
        EditHints notRepeatableHintMedium = new EditHints(false, SizeEditField.MEDIUM);
        EditHints notRepeatableHintLarge = new EditHints(false, SizeEditField.LARGE);
        EditHints repeatableHint = new EditHints(true, SizeEditField.LARGE);

        EDIT_HINT_OF_META_DATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, repeatableHint);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, notRepeatableHintLarge);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, notRepeatableHintMedium);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopCreditMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, notRepeatableHintSmall);
        EDIT_HINT_OF_META_DATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, notRepeatableHintSmall);
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<>(EDIT_HINT_OF_META_DATA_VALUE.keySet());
    }

    public static EditHints getEditHints(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return EDIT_HINT_OF_META_DATA_VALUE.get(value);
    }

    private EditableMetaDataValues() {
    }
}
