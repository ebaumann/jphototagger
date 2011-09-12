package org.jphototagger.domain.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jphototagger.domain.metadata.exif.ExifDateTimeOriginalMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.file.FilesLastModifiedMetaDataValue;
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
 * IDs der Tabellenspalten, die für den Benutzer relevant sind. So kann in der
 * Datenbank eine ID abgespeichert werden, aus der eindeutig die Tabellenspalte
 * ermittelt werden kann.
 *
 * @author Elmar Baumann
 */
public final class MetaDataValueIds {

    private static final Map<Integer, MetaDataValue> META_DATA_VALUE_OF_ID = new HashMap<Integer, MetaDataValue>();
    private static final Map<MetaDataValue, Integer> ID_OF_META_DATA_VALUE = new HashMap<MetaDataValue, Integer>();

    static {

        // UPDATE IF an new MetaDataValue was created an will be used by a class that
        // uses this class.
        // *Never* change existing IDs and don't use an ID twice!
        META_DATA_VALUE_OF_ID.put(0, ExifDateTimeOriginalMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(1, ExifFocalLengthMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(2, ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(3, ExifRecordingEquipmentMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(4, FilesFilenameMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(5, FilesLastModifiedMetaDataValue.INSTANCE);

        // Removed 6: FilesThumbnail
        META_DATA_VALUE_OF_ID.put(7, XmpDcDescriptionMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(8, XmpDcRightsMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(9, XmpDcTitleMetaDataValue.INSTANCE);

        // Removed 10: XmpIptc4xmpcoreCountrycode
        META_DATA_VALUE_OF_ID.put(11, XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(12, XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(13, XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(15, XmpPhotoshopCityMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(16, XmpPhotoshopCountryMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(17, XmpPhotoshopCreditMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(18, XmpPhotoshopHeadlineMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(19, XmpPhotoshopInstructionsMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(20, XmpPhotoshopSourceMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(21, XmpPhotoshopStateMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(22, XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(23, XmpDcCreatorMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(24, XmpDcSubjectsSubjectMetaDataValue.INSTANCE);

        // Removed 26: CollectionnamesName
        // Removed 27: SavedSearchesName
        META_DATA_VALUE_OF_ID.put(28, XmpRatingMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(29, ExifLensMetaDataValue.INSTANCE);
        META_DATA_VALUE_OF_ID.put(30, XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE);

        // Next ID: 31 - UPDATE ID after assigning! --
        Set<Integer> keys = META_DATA_VALUE_OF_ID.keySet();

        for (Integer key : keys) {
            ID_OF_META_DATA_VALUE.put(META_DATA_VALUE_OF_ID.get(key), key);
        }
    }

    private MetaDataValueIds() {
    }

    /**
     * Liefert eine Spalte mit bestimmter ID.
     *
     * @param  id ID
     * @return Spalte oder null bei ungültiger ID
     */
    public static MetaDataValue getMetaDataValue(int id) {
        return META_DATA_VALUE_OF_ID.get(id);
    }

    /**
     * Liefert die ID einer Spalte.
     *
     * @param  value Spalte
     * @return ID
     */
    public static int getId(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        return ID_OF_META_DATA_VALUE.get(value);
    }
}
