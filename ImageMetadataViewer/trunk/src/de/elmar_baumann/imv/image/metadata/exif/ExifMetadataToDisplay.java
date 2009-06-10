package de.elmar_baumann.imv.image.metadata.exif;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the EXIF metadata to display (show) to the user.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifMetadataToDisplay {

    private static final List<Integer> tagsToDisplay = new ArrayList<Integer>();


    static {
        // Ordered alphabetically
        tagsToDisplay.add(ExifTag.ARTIST.getId());
        tagsToDisplay.add(ExifTag.CONTRAST.getId());
        tagsToDisplay.add(ExifTag.COPYRIGHT.getId());
        tagsToDisplay.add(ExifTag.DATE_TIME_ORIGINAL.getId());
        tagsToDisplay.add(ExifTag.EXPOSURE_PROGRAM.getId());
        tagsToDisplay.add(ExifTag.EXPOSURE_TIME.getId());
        tagsToDisplay.add(ExifTag.FILE_SOURCE.getId());
        tagsToDisplay.add(ExifTag.FLASH.getId());
        tagsToDisplay.add(ExifTag.F_NUMBER.getId());
        tagsToDisplay.add(ExifTag.FOCAL_LENGTH.getId());
        tagsToDisplay.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId());
        tagsToDisplay.add(ExifTag.GPS_DATE_STAMP.getId());
        tagsToDisplay.add(ExifTag.GPS_SATELLITES.getId());
        tagsToDisplay.add(ExifTag.GPS_TIME_STAMP.getId());
        tagsToDisplay.add(ExifTag.GPS_VERSION_ID.getId());
        tagsToDisplay.add(ExifTag.IMAGE_DESCRIPTION.getId());
        tagsToDisplay.add(ExifTag.IMAGE_UNIQUE_ID.getId());
        tagsToDisplay.add(ExifTag.ISO_SPEED_RATINGS.getId());
        tagsToDisplay.add(ExifTag.MAKE.getId());
        tagsToDisplay.add(ExifTag.METERING_MODE.getId());
        tagsToDisplay.add(ExifTag.MODEL.getId());
        tagsToDisplay.add(ExifTag.SATURATION.getId());
        tagsToDisplay.add(ExifTag.SHARPNESS.getId());
        tagsToDisplay.add(ExifTag.SOFTWARE.getId());
        tagsToDisplay.add(ExifTag.USER_COMMENT.getId());
        tagsToDisplay.add(ExifTag.WHITE_BALANCE.getId());
        tagsToDisplay.add(ExifTag.SPECTRAL_SENSITIVITY.getId());
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
        return tagsToDisplay.contains(tag);
    }

    private ExifMetadataToDisplay() {
    }
}
