package de.elmar_baumann.imv.image.metadata.exif;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vergleicht IFDEntries zum Sortieren für die Anzeige.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ExifIfdEntryDisplayComparator implements Comparator<IdfEntryProxy> {

    private static final Map<Integer, Integer> orderOfTagValue = new HashMap<Integer, Integer>();
    private static final List<Integer> tagValues = new ArrayList<Integer>(30);
    public static final ExifIfdEntryDisplayComparator INSTANCE = new ExifIfdEntryDisplayComparator();
    
    static {
        // So kann später leicht umsortiert werden
        tagValues.add(ExifTag.DATE_TIME_ORIGINAL.getId());
        tagValues.add(ExifTag.MAKE.getId());
        tagValues.add(ExifTag.MODEL.getId());
        tagValues.add(ExifTag.FOCAL_LENGTH.getId());
        tagValues.add(ExifTag.FOCAL_LENGTH_IN_35_MM_FILM.getId());
        tagValues.add(ExifTag.SUBJECT_DISTANCE_RANGE.getId());
        tagValues.add(ExifTag.EXPOSURE_TIME.getId());
        tagValues.add(ExifTag.F_NUMBER.getId());
        tagValues.add(ExifTag.ISO_SPEED_RATINGS.getId());
        tagValues.add(ExifTag.METERING_MODE.getId());
        tagValues.add(ExifTag.EXPOSURE_MODE.getId());
        tagValues.add(ExifTag.EXPOSURE_PROGRAM.getId());
        tagValues.add(ExifTag.FLASH.getId());
        tagValues.add(ExifTag.WHITE_BALANCE.getId());
        tagValues.add(ExifTag.SATURATION.getId());
        tagValues.add(ExifTag.SHARPNESS.getId());
        tagValues.add(ExifTag.CONTRAST.getId());
        tagValues.add(ExifTag.USER_COMMENT.getId());
        tagValues.add(ExifTag.IMAGE_WIDTH.getId());
        tagValues.add(ExifTag.IMAGE_LENGTH.getId());
        tagValues.add(ExifTag.BITS_PER_SAMPLE.getId());
        tagValues.add(ExifTag.DATE_TIME_DIGITIZED.getId());
        tagValues.add(ExifTag.FILE_SOURCE.getId());
        tagValues.add(ExifTag.DATE_TIME.getId());
        tagValues.add(ExifTag.SOFTWARE.getId());

        int size = tagValues.size();
        for (int i = 0; i < size; i++) {
            orderOfTagValue.put(tagValues.get(i), i);
        }
    }

    @Override
    public int compare(IdfEntryProxy o1, IdfEntryProxy o2) {
        int tag1 = o1.getTag();
        int tag2 = o2.getTag();
        if (orderOfTagValue.containsKey(tag1) && orderOfTagValue.containsKey(tag2)) {
            return orderOfTagValue.get(tag1) - orderOfTagValue.get(tag2);
        }
        return tag1 - tag2;
    }

    private ExifIfdEntryDisplayComparator() {}
}
