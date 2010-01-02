/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifLens;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
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

    private static final Icon              ICON_UNDEFINED = AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Map<Column, Icon> ICON_OF_COLUMN = new HashMap<Column, Icon>();

    static {
        ICON_OF_COLUMN.put(ColumnXmpRating.INSTANCE              , AppLookAndFeel.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE            , AppLookAndFeel.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE           , AppLookAndFeel.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE     , AppLookAndFeel.getIcon("icon_xmp_photoshop_source.png"));
        ICON_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, AppLookAndFeel.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_COLUMN.put(ColumnExifIsoSpeedRatings.INSTANCE    , AppLookAndFeel.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_COLUMN.put(ColumnExifFocalLength.INSTANCE        , AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(ColumnExifLens.INSTANCE               , AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(ColumnExifRecordingEquipment.INSTANCE , AppLookAndFeel.getIcon("icon_exif_recording_equipment.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE   , AppLookAndFeel.getIcon("icon_keyword.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE       , AppLookAndFeel.getIcon("icon_xmp_dc_description.png"));
        ICON_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE   , AppLookAndFeel.getIcon("icon_xmp_photoshop_headline.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE             , AppLookAndFeel.getIcon("icon_xmp_dc_title.png"));
        ICON_OF_COLUMN.put(ColumnFilesFilename.INSTANCE          , AppLookAndFeel.getIcon("icon_file.png"));
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
