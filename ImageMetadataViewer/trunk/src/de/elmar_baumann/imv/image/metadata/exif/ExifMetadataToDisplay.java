package de.elmar_baumann.imv.image.metadata.exif;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifMetadataToDisplay {

    private static final List<Integer> TAGS_TO_DISPLAY =
            new ArrayList<Integer>();

    static {
        // Ordered alphabetically
        TAGS_TO_DISPLAY.add(ExifTag.ARTIST.getId());
        TAGS_TO_DISPLAY.add(ExifTag.CONTRAST.getId());
        TAGS_TO_DISPLAY.add(ExifTag.COPYRIGHT.getId());
        TAGS_TO_DISPLAY.add(ExifTag.DATE_TIME_ORIGINAL.getId());
        TAGS_TO_DISPLAY.add(ExifTag.EXPOSURE_PROGRAM.getId());
        TAGS_TO_DISPLAY.add(ExifTag.EXPOSURE_TIME.getId());
        TAGS_TO_DISPLAY.add(ExifTag.FILE_SOURCE.getId());
        TAGS_TO_DISPLAY.add(ExifTag.FLASH.getId());
        TAGS_TO_DISPLAY.add(ExifTag.F_NUMBER.getId());
        TAGS_TO_DISPLAY.add(ExifTag.FOCAL_LENGTH.getId());
        TAGS_TO_DISPLAY.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_DATE_STAMP.getId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_SATELLITES.getId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_TIME_STAMP.getId());
        TAGS_TO_DISPLAY.add(ExifTag.GPS_VERSION_ID.getId());
        TAGS_TO_DISPLAY.add(ExifTag.IMAGE_DESCRIPTION.getId());
        TAGS_TO_DISPLAY.add(ExifTag.IMAGE_UNIQUE_ID.getId());
        TAGS_TO_DISPLAY.add(ExifTag.ISO_SPEED_RATINGS.getId());
        TAGS_TO_DISPLAY.add(ExifTag.MAKE.getId());
        TAGS_TO_DISPLAY.add(ExifTag.METERING_MODE.getId());
        TAGS_TO_DISPLAY.add(ExifTag.MODEL.getId());
        TAGS_TO_DISPLAY.add(ExifTag.SATURATION.getId());
        TAGS_TO_DISPLAY.add(ExifTag.SHARPNESS.getId());
        TAGS_TO_DISPLAY.add(ExifTag.SOFTWARE.getId());
        TAGS_TO_DISPLAY.add(ExifTag.USER_COMMENT.getId());
        TAGS_TO_DISPLAY.add(ExifTag.WHITE_BALANCE.getId());
        TAGS_TO_DISPLAY.add(ExifTag.SPECTRAL_SENSITIVITY.getId());
    }

    /**
     * Returns the EXIF metadata to display to the user.
     *
     * @param  entries metadata
     * @return         subset of entries (displayable metadata)
     */
    public static List<IdfEntryProxy> get(
            List<IdfEntryProxy> entries) {
        List<IdfEntryProxy> displayableEntries = new ArrayList<IdfEntryProxy>(
                entries.size());
        for (IdfEntryProxy entry : entries) {
            if (isTagToDisplay(entry.getTag())) {
                if (!contains(displayableEntries, entry)) {
                    displayableEntries.add(entry);
                }
            }
        }
        return displayableEntries;
    }

    private static boolean contains(List<IdfEntryProxy> entries,
            IdfEntryProxy entry) {
        for (IdfEntryProxy e : entries) {
            if (ExifIfdEntryComparator.INSTANCE.compare(e, entry) == 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean isTagToDisplay(int tag) {
        return TAGS_TO_DISPLAY.contains(tag);
    }

    private ExifMetadataToDisplay() {
    }
}
