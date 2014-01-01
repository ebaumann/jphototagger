package org.jphototagger.exif;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Elmar Baumann
 */
public final class ExifToSaveInRepository {

    private static final List<ExifTag.Properties> EXIF_TAG_IDS_TO_SAVE = new ArrayList<>();

    static {
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Properties.DATE_TIME_ORIGINAL);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Properties.FOCAL_LENGTH);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Properties.ISO_SPEED_RATINGS);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Properties.MODEL);
        EXIF_TAG_IDS_TO_SAVE.add(ExifTag.Properties.MAKER_NOTE_LENS);
    }

    /**
     * Liefert, ob die Metadaten eines EXIF-Tags in die Datenbank gespeichert
     * werden.
     *
     * @param  exifTagId Tag ID
     * @return           true, falls gespeichert
     */
    public static boolean isSaveInRepository(ExifTag.Properties exifTagId) {
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
    public static boolean isSaveInRepository(ExifIfd ifdType, ExifTag.Properties id) {
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
