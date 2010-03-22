/*
 * @(#)TableIcons.java    Created on 2008-09-13
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

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author  Elmar Baumann
 */
public final class TableIcons {
    private static final Icon ICON_UNDEFINED =
        AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Icon ICON_FILES =
        AppLookAndFeel.getIcon("icon_file.png");
    private static final Icon ICON_EXIF =
        AppLookAndFeel.getIcon("icon_exif.png");
    private static final Icon ICON_XMP          =
        AppLookAndFeel.getIcon("icon_xmp.png");
    private static final Map<String, Icon> ICON_OF_TABLENAME =
        new HashMap<String, Icon>();

    static {
        ICON_OF_TABLENAME.put("exif", ICON_EXIF);
        ICON_OF_TABLENAME.put("exif_recording_equipment", ICON_EXIF);
        ICON_OF_TABLENAME.put("exif_lens", ICON_EXIF);
        ICON_OF_TABLENAME.put("files", ICON_FILES);
        ICON_OF_TABLENAME.put("xmp", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_creator", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_rights", ICON_XMP);
        ICON_OF_TABLENAME.put("iptc4xmpcore_location", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_authorsposition", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_captionwriter", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_city", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_country", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_credit", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_source", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_state", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_subjects", ICON_XMP);
    }

    private TableIcons() {}

    public static Icon getIcon(String tablename) {
        Icon icon = ICON_OF_TABLENAME.get(tablename);

        return (icon == null)
               ? ICON_UNDEFINED
               : icon;
    }
}
