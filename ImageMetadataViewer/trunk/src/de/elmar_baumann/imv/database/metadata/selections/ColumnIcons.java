package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
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

    private static final Icon ICON_UNDEFINED = AppIcons.getIcon(
            "icon_table_undefined.png"); // NOI18N
    private static final Map<Column, Icon> ICON_OF_COLUMN =
            new HashMap<Column, Icon>();

    static {
        ICON_OF_COLUMN.put(ColumnXmpRating.INSTANCE,
                AppIcons.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE,
                AppIcons.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE,
                AppIcons.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE,
                AppIcons.getIcon("icon_xmp_source.png"));
        ICON_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                AppIcons.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_COLUMN.put(ColumnExifIsoSpeedRatings.INSTANCE,
                AppIcons.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_COLUMN.put(ColumnExifFocalLength.INSTANCE,
                AppIcons.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(ColumnExifRecordingEquipment.INSTANCE,
                AppIcons.getIcon("icon_exif_recording_equipment.png"));
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
