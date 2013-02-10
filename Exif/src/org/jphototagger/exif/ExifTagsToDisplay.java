package org.jphototagger.exif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author Elmar Baumann
 */
public final class ExifTagsToDisplay {

    private static final List<Integer> ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY = new ArrayList<>();
    private static final List<Integer> ID_VALUES_OF_GPS_TAGS_TO_DISPLAY = new ArrayList<>();
    private static final List<Integer> ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY = new ArrayList<>();

    static {

        // Ordered alphabetically
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.ARTIST.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.CONTRAST.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.COPYRIGHT.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.DATE_TIME_ORIGINAL.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.DATE_TIME_DIGITIZED.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.DATE_TIME.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.EXPOSURE_PROGRAM.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.EXPOSURE_TIME.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.FILE_SOURCE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.FLASH.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.F_NUMBER.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.FOCAL_LENGTH.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.FOCAL_LENGTH_IN_35_MM_FILM.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.IMAGE_DESCRIPTION.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.IMAGE_UNIQUE_ID.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.ISO_SPEED_RATINGS.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.MAKE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.METERING_MODE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.MODEL.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.SATURATION.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.SHARPNESS.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.SOFTWARE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.USER_COMMENT.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.WHITE_BALANCE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.SPECTRAL_SENSITIVITY.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Properties.MAKER_NOTE_LENS.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Properties.GPS_DATE_STAMP.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Properties.GPS_SATELLITES.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Properties.GPS_TIME_STAMP.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Properties.GPS_VERSION_ID.getTagId());
    }

    public static List<ExifTag> getDisplayableExifTagsOf(Collection<? extends ExifTag> exifTags) {
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        List<ExifTag> displayableExifTags = new ArrayList<>(exifTags.size());

        for (ExifTag exifTag : exifTags) {
            if (isDisplayableExifTag(exifTag) && !containsCollectionTag(displayableExifTags, exifTag)) {
                displayableExifTags.add(exifTag);
            }
        }

        return displayableExifTags;
    }

    private static boolean containsCollectionTag(Collection<? extends ExifTag> exifTags, ExifTag searchExifTag) {
        for (ExifTag exifTag : exifTags) {
            if (exifTag.equals(searchExifTag)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDisplayableExifTag(ExifTag exifTag) {
        ExifIfd ifdType = exifTag.getIfd();
        int tagId = exifTag.getTagId();
        ExifTag.Properties id = exifTag.parseProperties();

        switch (ifdType) {
            case EXIF:
                return ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.contains(tagId);

            case INTEROPERABILITY:
                return ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY.contains(tagId);

            case GPS:
                return ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.contains(tagId);

            default:
                return ifdType.equals(ExifIfd.MAKER_NOTE) && !id.isMakerNoteTag();
        }
    }

    private ExifTagsToDisplay() {
    }
}
