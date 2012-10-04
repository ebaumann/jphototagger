package org.jphototagger.domain.metadata.selections;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class RepositoryInfoCountOfMetaDataValues {

    private static final List<MetaDataValue> META_DATA_VALUES = new ArrayList<MetaDataValue>();

    static {
        META_DATA_VALUES.add(FilesFilenameMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpDcCreatorMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
        META_DATA_VALUES.add(ExifLensMetaDataValue.INSTANCE);
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(META_DATA_VALUES);
    }

    private RepositoryInfoCountOfMetaDataValues() {
    }
}
