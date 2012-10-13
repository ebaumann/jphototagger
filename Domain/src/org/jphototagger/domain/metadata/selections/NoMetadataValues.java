package org.jphototagger.domain.metadata.selections;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifDateTimeOriginalMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
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

/**
 * Metadata values to display in no metadata list.
 *
 * @author Elmar Baumann
 */
public final class NoMetadataValues {

    private static final List<MetaDataValue> VALUES = new ArrayList<>();

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
        VALUES.add(ExifDateTimeOriginalMetaDataValue.INSTANCE);
        VALUES.add(ExifFocalLengthMetaDataValue.INSTANCE);
        VALUES.add(ExifLensMetaDataValue.INSTANCE);
        VALUES.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
    }

    /**
     * Liefert die Spalten für die erweiterte Suche.
     *
     * @return Suchspalten
     */
    public static List<MetaDataValue> get() {
        return new ArrayList<>(VALUES);
    }

    private NoMetadataValues() {
    }
}
