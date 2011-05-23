package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.ExifMetadata.IfdType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author Elmar Baumann
 */
public final class ExifTagsToDisplay {
    private static final List<Integer> ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY = new ArrayList<Integer>();
    private static final List<Integer> ID_VALUES_OF_GPS_TAGS_TO_DISPLAY = new ArrayList<Integer>();
    private static final List<Integer> ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY = new ArrayList<Integer>();

    static {

        // Ordered alphabetically
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.ARTIST.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.CONTRAST.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.COPYRIGHT.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.DATE_TIME_ORIGINAL.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.EXPOSURE_PROGRAM.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.EXPOSURE_TIME.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FILE_SOURCE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FLASH.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.F_NUMBER.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FOCAL_LENGTH.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.FOCAL_LENGTH_IN_35_MM_FILM.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.IMAGE_DESCRIPTION.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.IMAGE_UNIQUE_ID.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.ISO_SPEED_RATINGS.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MAKE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.METERING_MODE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MODEL.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SATURATION.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SHARPNESS.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SOFTWARE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.USER_COMMENT.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.WHITE_BALANCE.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.SPECTRAL_SENSITIVITY.getTagId());
        ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.add(ExifTag.Id.MAKER_NOTE_LENS.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_DATE_STAMP.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_SATELLITES.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_TIME_STAMP.getTagId());
        ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.add(ExifTag.Id.GPS_VERSION_ID.getTagId());
    }

    public static List<ExifTag> getDisplayableExifTagsOf(Collection<? extends ExifTag> exifTags) {
        if (exifTags == null) {
            throw new NullPointerException("exifTags == null");
        }

        List<ExifTag> displayableExifTags = new ArrayList<ExifTag>(exifTags.size());

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
        IfdType ifdType = exifTag.getIfdType();
        int tagId = exifTag.getTagId();
        ExifTag.Id id= exifTag.convertTagIdToEnumId();

        switch (ifdType) {
        case EXIF :
            return ID_VALUES_OF_EXIF_TAGS_TO_DISPLAY.contains(tagId);

        case INTEROPERABILITY :
            return ID_VALUES_OF_INTEROPARABILITY_TAGS_TO_DISPLAY.contains(tagId);

        case GPS :
            return ID_VALUES_OF_GPS_TAGS_TO_DISPLAY.contains(tagId);

        default :
            return ifdType.equals(IfdType.MAKER_NOTE) && !id.isMakerNoteId();
        }
    }

    private ExifTagsToDisplay() {}
}
