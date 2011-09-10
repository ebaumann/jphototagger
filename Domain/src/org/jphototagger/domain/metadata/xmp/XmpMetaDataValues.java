package org.jphototagger.domain.metadata.xmp;

import java.util.ArrayList;
import java.util.List;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * Collection of all XMP metadata values.
 *
 * @author Elmar Baumann
 */
public final class XmpMetaDataValues {

    private static final List<MetaDataValue> VALUES = new ArrayList<MetaDataValue>();

    static {
        VALUES.add(XmpDcCreatorMetaDataValue.INSTANCE);
        VALUES.add(XmpDcDescriptionMetaDataValue.INSTANCE);
        VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);
        VALUES.add(XmpDcTitleMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        VALUES.add(XmpRatingMetaDataValue.INSTANCE);
        VALUES.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
    }

    private XmpMetaDataValues() {
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(VALUES);
    }
}
