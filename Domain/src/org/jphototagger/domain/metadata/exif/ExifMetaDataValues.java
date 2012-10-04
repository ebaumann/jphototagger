package org.jphototagger.domain.metadata.exif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class ExifMetaDataValues {

    private static final Set<MetaDataValue> VALUES = new LinkedHashSet<MetaDataValue>();

    static {
        VALUES.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        VALUES.add(ExifLensMetaDataValue.INSTANCE);
        VALUES.add(ExifDateTimeOriginalMetaDataValue.INSTANCE);
        VALUES.add(ExifFocalLengthMetaDataValue.INSTANCE);
        VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
    }

    public static Collection<? extends MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(VALUES);
    }

    private ExifMetaDataValues() {
    }
}
