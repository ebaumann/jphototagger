/*
 * @(#)ColumnIcons.java    Created on 2009-08-03
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
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
 * @author  Elmar Baumann
 */
public final class ColumnIcons {
    private static final Icon ICON_UNDEFINED =
        AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Map<Column, Icon> ICON_OF_COLUMN = new HashMap<Column,
                                                                Icon>();

    static {
        ICON_OF_COLUMN.put(ColumnXmpRating.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_rating_set.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_dc_rights.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_dc_creator.png"));
        ICON_OF_COLUMN.put(
            ColumnXmpPhotoshopSource.INSTANCE,
            AppLookAndFeel.getIcon("icon_xmp_photoshop_source.png"));
        ICON_OF_COLUMN.put(
            ColumnXmpIptc4xmpcoreLocation.INSTANCE,
            AppLookAndFeel.getIcon("icon_xmp_iptc4_core_location.png"));
        ICON_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                           AppLookAndFeel.getIcon("icon_timeline.png"));
        ICON_OF_COLUMN.put(
            ColumnExifIsoSpeedRatings.INSTANCE,
            AppLookAndFeel.getIcon("icon_exif_iso_speed_ratings.png"));
        ICON_OF_COLUMN.put(
            ColumnExifFocalLength.INSTANCE,
            AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(
            ColumnExifLens.INSTANCE,
            AppLookAndFeel.getIcon("icon_exif_focal_length.png"));
        ICON_OF_COLUMN.put(
            ColumnExifRecordingEquipment.INSTANCE,
            AppLookAndFeel.getIcon("icon_exif_recording_equipment.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE,
                           AppLookAndFeel.getIcon("icon_keyword.png"));
        ICON_OF_COLUMN.put(
            ColumnXmpDcDescription.INSTANCE,
            AppLookAndFeel.getIcon("icon_xmp_dc_description.png"));
        ICON_OF_COLUMN.put(
            ColumnXmpPhotoshopHeadline.INSTANCE,
            AppLookAndFeel.getIcon("icon_xmp_photoshop_headline.png"));
        ICON_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE,
                           AppLookAndFeel.getIcon("icon_xmp_dc_title.png"));
        ICON_OF_COLUMN.put(ColumnFilesFilename.INSTANCE,
                           AppLookAndFeel.getIcon("icon_file.png"));
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
