package de.elmar_baumann.imv.database.metadata.selections;

import de.elmar_baumann.imv.image.metadata.exif.ExifTag;
import java.util.ArrayList;
import java.util.List;

/**
 * Liefert, welche Exif-Metadaten in die Datenbank gespeichert werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class ExifInDatabase {

    private static final List<ExifTag> storedTags = new ArrayList<ExifTag>();
    

    static {
        storedTags.add(ExifTag.DateTimeOriginal);
        storedTags.add(ExifTag.FocalLength);
        storedTags.add(ExifTag.ISOSpeedRatings);
        storedTags.add(ExifTag.Model);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     * 
     * @param  exifTag  Tag 
     * @return true, falls gespeichert
     */
    public static boolean isInDatabase(ExifTag exifTag) {
        return storedTags.contains(exifTag);
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
            return storedTags.contains(tag);
        }
        return false;
    }
}
