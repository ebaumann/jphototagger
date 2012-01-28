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
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class MetaDataValueIcons {

    private static final Icon ICON_UNDEFINED = AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Map<MetaDataValue, Icon> ICON_OF_META_DATA_VALUE = new HashMap<MetaDataValue, Icon>();

    static {
        ICON_OF_META_DATA_VALUE.put(XmpRatingMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcRightsMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcCreatorMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopSourceMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_photoshop_source.png"));
        ICON_OF_META_DATA_VALUE.put(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_META_DATA_VALUE.put(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_timeline.png"));
        ICON_OF_META_DATA_VALUE.put(ExifIsoSpeedRatingsMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_META_DATA_VALUE.put(ExifFocalLengthMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_META_DATA_VALUE.put(ExifLensMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_META_DATA_VALUE.put(ExifRecordingEquipmentMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_exif_recording_equipment.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_keyword.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcDescriptionMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_description.png"));
        ICON_OF_META_DATA_VALUE.put(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_photoshop_headline.png"));
        ICON_OF_META_DATA_VALUE.put(XmpDcTitleMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_title.png"));
        ICON_OF_META_DATA_VALUE.put(FilesFilenameMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_file.png"));
        ICON_OF_META_DATA_VALUE.put(ThumbnailsThumbnailMetaDataValue.INSTANCE, AppLookAndFeel.getIcon("icon_image.png"));
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
