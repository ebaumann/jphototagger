package org.jphototagger.domain.metadata.selections;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;

/**
 * Spalten für die Schnellsuche.
 *
 * @author Elmar Baumann
 */
public final class FastSearchMetaDataValues {

    private static final List<MetaDataValue> VALUES = new ArrayList<MetaDataValue>();

    static {

        // XMP
        VALUES.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        VALUES.add(XmpDcTitleMetaDataValue.INSTANCE);
        VALUES.add(XmpDcDescriptionMetaDataValue.INSTANCE);
        VALUES.add(XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);

        // EXIF
        VALUES.add(ExifFocalLengthMetaDataValue.INSTANCE);
        VALUES.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);

        // Files
        VALUES.add(FilesFilenameMetaDataValue.INSTANCE);
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(VALUES);
    }

    private FastSearchMetaDataValues() {
    }
}
