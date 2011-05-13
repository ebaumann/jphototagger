package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.util.ArrayList;
import java.util.List;

/**
 * Liefert, welche Exif-Metadaten in die Datenbank gespeichert werden.
 *
 * @author Elmar Baumann
 */
public final class ExifInDatabase {
    private static final List<ExifTag.Id> STORED_TAG_IDS_EXIF_IFD = new ArrayList<ExifTag.Id>();

    static {
        STORED_TAG_IDS_EXIF_IFD.add(ExifTag.Id.DATE_TIME_ORIGINAL);
        STORED_TAG_IDS_EXIF_IFD.add(ExifTag.Id.FOCAL_LENGTH);
        STORED_TAG_IDS_EXIF_IFD.add(ExifTag.Id.ISO_SPEED_RATINGS);
        STORED_TAG_IDS_EXIF_IFD.add(ExifTag.Id.MODEL);
        STORED_TAG_IDS_EXIF_IFD.add(ExifTag.Id.MAKER_NOTE_LENS);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     *
     * @param  exifTagId Tag ID
     * @return           true, falls gespeichert
     */
    public static boolean isInDatabase(ExifTag.Id exifTagId) {
        if (exifTagId == null) {
            throw new NullPointerException("exifTagId == null");
        }

        return STORED_TAG_IDS_EXIF_IFD.contains(exifTagId);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     *
     * @param ifdType IFD type
     * @param id      id or null
     * @return        true, falls gespeichert
     */
    public static boolean isInDatabase(IfdType ifdType, ExifTag.Id id) {
        if (ifdType == null) {
            throw new NullPointerException("ifdType == null");
        }

        if (id == null) {
            throw new NullPointerException("id == null");
        }

        switch (ifdType) {
        case EXIF :
            return STORED_TAG_IDS_EXIF_IFD.contains(id);

        default :
            return false;
        }
    }

    private ExifInDatabase() {}
}
