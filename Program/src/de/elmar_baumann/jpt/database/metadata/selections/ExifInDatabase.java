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

import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import java.util.ArrayList;
import java.util.List;

/**
 * Liefert, welche Exif-Metadaten in die Datenbank gespeichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-14
 */
public final class ExifInDatabase {

    private static final List<ExifTag.Id> STORED_TAG_IDS = new ArrayList<ExifTag.Id>();
    

    static {
        STORED_TAG_IDS.add(ExifTag.Id.DATE_TIME_ORIGINAL);
        STORED_TAG_IDS.add(ExifTag.Id.FOCAL_LENGTH);
        STORED_TAG_IDS.add(ExifTag.Id.ISO_SPEED_RATINGS);
        STORED_TAG_IDS.add(ExifTag.Id.MODEL);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     * 
     * @param  exifTagId Tag ID
     * @return           true, falls gespeichert
     */
    public static boolean isInDatabase(ExifTag.Id exifTagId) {
        return STORED_TAG_IDS.contains(exifTagId);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     * 
     * @param  exifTagIdValue ID des Tags
     * @return                true, falls gespeichert
     */
    public static boolean isInDatabase(int exifTagIdValue) {
        ExifTag.Id exifTagId = ExifTag.Id.fromValue(exifTagIdValue);
        if (exifTagId != null) {
            return STORED_TAG_IDS.contains(exifTagId);
        }
        return false;
    }

    private ExifInDatabase() {
    }
}
