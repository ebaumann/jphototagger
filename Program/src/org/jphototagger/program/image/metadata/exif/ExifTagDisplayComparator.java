package org.jphototagger.program.image.metadata.exif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the display order of EXIF data.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class ExifTagDisplayComparator implements Comparator<ExifTag> {
    private static final Map<Integer, Integer> ORDER_OF_TAG_ID_VALUE = new HashMap<Integer, Integer>();
    private static final List<Integer> EXIF_IFD_TAG_ID_VALUES = new ArrayList<Integer>(30);
    private static final List<Integer> GPS_IFD_TAG_ID_VALUES = new ArrayList<Integer>(30);
    public static final ExifTagDisplayComparator INSTANCE = new ExifTagDisplayComparator();

    static {

        // display order
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_ORIGINAL.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_DESCRIPTION.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MAKE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MODEL.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FOCAL_LENGTH.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SUBJECT_DISTANCE_RANGE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_TIME.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.F_NUMBER.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.ISO_SPEED_RATINGS.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.METERING_MODE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_MODE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.EXPOSURE_PROGRAM.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FLASH.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.WHITE_BALANCE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SATURATION.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SHARPNESS.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.CONTRAST.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.USER_COMMENT.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.COPYRIGHT.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.ARTIST.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_WIDTH.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.IMAGE_LENGTH.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.BITS_PER_SAMPLE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME_DIGITIZED.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.FILE_SOURCE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.DATE_TIME.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.SOFTWARE.getTagId());
        EXIF_IFD_TAG_ID_VALUES.add(ExifTag.Id.MAKER_NOTE_LENS.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_VERSION_ID.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE_REF.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LATITUDE.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE_REF.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_LONGITUDE.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE_REF.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_ALTITUDE.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_TIME_STAMP.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_SATELLITES.getTagId());
        GPS_IFD_TAG_ID_VALUES.add(ExifTag.Id.GPS_DATE_STAMP.getTagId());

        int order = 0;

        // 1. EXIF IFD
        int size = EXIF_IFD_TAG_ID_VALUES.size();

        for (int index = 0; index < size; index++) {
            ORDER_OF_TAG_ID_VALUE.put(EXIF_IFD_TAG_ID_VALUES.get(index), order++);
        }

        // 2. GPS IFD
        size = GPS_IFD_TAG_ID_VALUES.size();

        for (int index = 0; index < size; index++) {
            ORDER_OF_TAG_ID_VALUE.put(GPS_IFD_TAG_ID_VALUES.get(index), order++);
        }
    }

    @Override
    public int compare(ExifTag exifTagLeft, ExifTag exifTagRight) {
        int tagIdLeft = exifTagLeft.getTagId();
        int tagIdRight = exifTagRight.getTagId();

        if (ORDER_OF_TAG_ID_VALUE.containsKey(tagIdLeft) && ORDER_OF_TAG_ID_VALUE.containsKey(tagIdRight)) {
            return ORDER_OF_TAG_ID_VALUE.get(tagIdLeft) - ORDER_OF_TAG_ID_VALUE.get(tagIdRight);
        }

        return tagIdLeft - tagIdRight;
    }

    private ExifTagDisplayComparator() {}
}
