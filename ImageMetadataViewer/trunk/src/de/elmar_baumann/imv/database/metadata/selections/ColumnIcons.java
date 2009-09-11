package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpRating;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 * Icons of table columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-03
 */
public final class ColumnIcons {

    private static final Icon ICON_UNDEFINED = AppLookAndFeel.getIcon(
            "icon_table_undefined.png"); // NOI18N
    private static final Map<Column, Icon> ICON_OF_COLUMN =
            new HashMap<Column, Icon>();

    static {
        ICON_OF_COLUMN.put(ColumnXmpRating.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_rating_set.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_dc_rights.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_dc_creator.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_photoshop_source.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_iptc4_core_location.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnExifIsoSpeedRatings.INSTANCE,
                AppLookAndFeel.getIcon("icon_exif_iso_speed_ratings.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnExifFocalLength.INSTANCE,
                AppLookAndFeel.getIcon("icon_exif_focal_length.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnExifRecordingEquipment.INSTANCE,
                AppLookAndFeel.getIcon("icon_exif_recording_equipment.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE,
                AppLookAndFeel.getIcon("icon_keyword.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_dc_description.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_photoshop_headline.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE,
                AppLookAndFeel.getIcon("icon_xmp_dc_title.png")); // NOI18N
        ICON_OF_COLUMN.put(ColumnFilesFilename.INSTANCE,
                AppLookAndFeel.getIcon("icon_file.png")); // NOI18N
    }

    /**
     * Returns the icon of a column.
     * 
     * @param  column column
     * @return        icon
     */
    public static Icon getIcon(Column column) {
        Icon icon = ICON_OF_COLUMN.get(column);
        return icon == null
               ? ICON_UNDEFINED
               : icon;
    }

    private ColumnIcons() {
    }
}
