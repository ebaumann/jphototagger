/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.app.AppLookAndFeel;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author  Elmar Baumann
 * @version 2008-09-13
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
    private static final Icon ICON_IMAGE_COLLECTION =
        AppLookAndFeel.getIcon("icon_imagecollection.png");

    static {
        ICON_OF_TABLENAME.put("exif", ICON_EXIF);
        ICON_OF_TABLENAME.put("files", ICON_FILES);
        ICON_OF_TABLENAME.put("xmp", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_subjects", ICON_XMP);
        ICON_OF_TABLENAME.put("collections", ICON_IMAGE_COLLECTION);
        ICON_OF_TABLENAME.put("collection_names", ICON_IMAGE_COLLECTION);
    }

    private TableIcons() {}

    public static Icon getIcon(String tablename) {
        Icon icon = ICON_OF_TABLENAME.get(tablename);

        return (icon == null)
               ? ICON_UNDEFINED
               : icon;
    }
}
