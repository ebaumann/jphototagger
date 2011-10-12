package org.jphototagger.exif;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public final class ExifToSaveInRepository {

    private static final List<ExifTag.Id> EXIF_TAG_IDS_TO_SAVE = new ArrayList<ExifTag.Id>();

    static {
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Id.DATE_TIME_ORIGINAL);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Id.FOCAL_LENGTH);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Id.ISO_SPEED_RATINGS);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Id.MODEL);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Id.MAKER_NOTE_LENS);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     *
     * @param  exifTagId Tag ID
     * @return           true, falls gespeichert
     */
    public static boolean isSaveInRepository(ExifTag.Id exifTagId) {
        if (exifTagId == null) {
            throw new NullPointerException("exifTagId == null");
        }

        return EXIF_TAG_IDS_TO_SAVE.contains(exifTagId);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     *
     * @param ifdType IFD type
     * @param id      id or null
     * @return        true, falls gespeichert
     */
    public static boolean isSaveInRepository(ExifIfdType ifdType, ExifTag.Id id) {
        if (ifdType == null) {
            throw new NullPointerException("ifdType == null");
        }

        if (id == null) {
            throw new NullPointerException("id == null");
        }

        switch (ifdType) {
            case EXIF:
                return EXIF_TAG_IDS_TO_SAVE.contains(id);

            default:
                return false;
        }
    }

    private ExifToSaveInRepository() {
    }
}
