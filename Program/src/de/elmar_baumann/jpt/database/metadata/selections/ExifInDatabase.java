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

    private static final List<ExifTag> STORED_TAGS = new ArrayList<ExifTag>();
    

    static {
        STORED_TAGS.add(ExifTag.DATE_TIME_ORIGINAL);
        STORED_TAGS.add(ExifTag.FOCAL_LENGTH);
        STORED_TAGS.add(ExifTag.ISO_SPEED_RATINGS);
        STORED_TAGS.add(ExifTag.MODEL);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     * 
     * @param  exifTag  Tag 
     * @return true, falls gespeichert
     */
    public static boolean isInDatabase(ExifTag exifTag) {
        return STORED_TAGS.contains(exifTag);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     * 
     * @param  tagId  ID des Tags
     * @return true, falls gespeichert
     */
    public static boolean isInDatabase(int tagId) {
        ExifTag tag = ExifTag.getTag(tagId);
        if (tag != null) {
            return STORED_TAGS.contains(tag);
        }
        return false;
    }

    private ExifInDatabase() {
    }
}
