package org.jphototagger.program.database.metadata.selections;

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
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseInfoRecordCountColumns {
    private static final List<MetaDataValue> COLUMNS = new ArrayList<MetaDataValue>();

    static {
        COLUMNS.add(FilesFilenameMetaDataValue.INSTANCE);
        COLUMNS.add(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        COLUMNS.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        COLUMNS.add(XmpDcCreatorMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        COLUMNS.add(XmpDcRightsMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopCreditMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        COLUMNS.add(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        COLUMNS.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
        COLUMNS.add(ExifLensMetaDataValue.INSTANCE);
    }

    public static List<MetaDataValue> get() {
        return new ArrayList<MetaDataValue>(COLUMNS);
    }

    private DatabaseInfoRecordCountColumns() {}
}
