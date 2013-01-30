package org.jphototagger.domain.metadata.xmp;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * All XMP metadata values handled by JPhotoTagger.
 * @author Elmar Baumann
 */
public final class XmpMetaDataValues {

    private static final Set<MetaDataValue> VALUES = new LinkedHashSet<>();

    static {
        VALUES.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        VALUES.add(XmpDcTitleMetaDataValue.INSTANCE);
        VALUES.add(XmpDcDescriptionMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        VALUES.add(XmpDcCreatorMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        VALUES.add(XmpRatingMetaDataValue.INSTANCE);
    }

    private XmpMetaDataValues() {
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<>(VALUES);
    }

    /**
     * @param value null ok (returns false)
     * @return
     */
    public static boolean isXmpMetaDataValue(MetaDataValue value) {
        if (value == null) {
            return false;
        }
        return VALUES.contains(value);
    }
}
