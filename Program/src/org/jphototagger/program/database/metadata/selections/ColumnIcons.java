package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif.ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif.ColumnExifLens;
import org.jphototagger.program.database.metadata.exif.ColumnExifRecordingEquipment;
import org.jphototagger.program.database.metadata.file.ColumnFilesFilename;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;

/**
 * Icons of table columns.
 *
 * @author Elmar Baumann
 */
public final class ColumnIcons {
    private static final Icon ICON_UNDEFINED = AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Map<Column, Icon> ICON_OF_COLUMN = new HashMap<Column, Icon>();

    static {
        ICON_OF_COLUMN.put(ColumnXmpRating.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_photoshop_source.png"));
        ICON_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, AppLookAndFeel.getIcon("icon_timeline.png"));
        ICON_OF_COLUMN.put(ColumnExifIsoSpeedRatings.INSTANCE,
                           AppLookAndFeel.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_COLUMN.put(ColumnExifFocalLength.INSTANCE, AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(ColumnExifLens.INSTANCE, AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(ColumnExifRecordingEquipment.INSTANCE,
                           AppLookAndFeel.getIcon("icon_exif_recording_equipment.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE, AppLookAndFeel.getIcon("icon_keyword.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_description.png"));
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_photoshop_headline.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_dc_title.png"));
        ICON_OF_COLUMN.put(ColumnFilesFilename.INSTANCE, AppLookAndFeel.getIcon("icon_file.png"));
    }

    /**
     * Returns the icon of a column.
     *
     * @param  column column
     * @return        icon
     */
    public static Icon getIcon(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        Icon icon = ICON_OF_COLUMN.get(column);

        return (icon == null)
               ? ICON_UNDEFINED
               : icon;
    }

    private ColumnIcons() {}
}
