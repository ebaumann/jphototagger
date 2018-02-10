package org.jphototagger.program.module.search;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.domain.metadata.file.FilesFilenameMetaDataValue;
import org.jphototagger.domain.metadata.thumbnails.ThumbnailsThumbnailMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class MetaDataValueIcons {

    private static final Icon ICON_UNDEFINED = Icons.getIcon("icon_table_undefined.png");
    private static final Map<MetaDataValue, Icon> ICON_OF_META_DATA_VALUE = new HashMap<>();

    static {
        ICON_OF_META_DATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopCountryMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopStateMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopCityMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_photoshop_source.png"));
        ICON_OF_META_DATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_META_DATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, Icons.getIcon("icon_timeline.png"));
        ICON_OF_META_DATA_VALUE.put(ExifIsoSpeedRatingsMetaDataValue.INSTANCE, Icons.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_META_DATA_VALUE.put(ExifFocalLengthMetaDataValue.INSTANCE, Icons.getIcon("icon_exif_focal_length.png"));
        ICON_OF_META_DATA_VALUE.put(ExifLensMetaDataValue.INSTANCE, Icons.getIcon("icon_exif_focal_length.png"));
        ICON_OF_META_DATA_VALUE.put(ExifRecordingEquipmentMetaDataValue.INSTANCE, Icons.getIcon("icon_exif_recording_equipment.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, Icons.getIcon("icon_keyword.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_dc_description.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_photoshop_headline.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, Icons.getIcon("icon_xmp_dc_title.png"));
        ICON_OF_META_DATA_VALUE.put(FilesFilenameMetaDataValue.INSTANCE, Icons.getIcon("icon_file.png"));
        ICON_OF_META_DATA_VALUE.put(ThumbnailsThumbnailMetaDataValue.INSTANCE, Icons.getIcon("icon_image.png"));
    }

    /**
     * Returns the icon of a metadata value.
     *
     * @param  value
     * @return        icon
     */
    public static Icon getIcon(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        Icon icon = ICON_OF_META_DATA_VALUE.get(value);

        return (icon == null)
                ? ICON_UNDEFINED
                : icon;
    }

    private MetaDataValueIcons() {
    }
}
