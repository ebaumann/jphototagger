package de.elmar_baumann.imv.image.metadata.exif;

import com.imagero.reader.tiff.IFDEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Vergleicht IFDEntries zum Sortieren für die Anzeige.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ExifIfdEntryDisplayComparator implements Comparator<IFDEntry> {

    private static HashMap<Integer, Integer> orderOfTagValue = new HashMap<Integer, Integer>();
    private static List<Integer> tagValues = new ArrayList<Integer>(30);
    
    static {
        // So kann später leicht umsortiert werden
        tagValues.add(ExifTag.DateTimeOriginal.getId());
        tagValues.add(ExifTag.Make.getId());
        tagValues.add(ExifTag.Model.getId());
        tagValues.add(ExifTag.FocalLength.getId());
        tagValues.add(ExifTag.FocalLengthIn35mmFilm.getId());
        tagValues.add(ExifTag.SubjectDistanceRange.getId());
        tagValues.add(ExifTag.ExposureTime.getId());
        tagValues.add(ExifTag.FNumber.getId());
        tagValues.add(ExifTag.ISOSpeedRatings.getId());
        tagValues.add(ExifTag.MeteringMode.getId());
        tagValues.add(ExifTag.ExposureMode.getId());
        tagValues.add(ExifTag.ExposureProgram.getId());
        tagValues.add(ExifTag.Flash.getId());
        tagValues.add(ExifTag.WhiteBalance.getId());
        tagValues.add(ExifTag.Saturation.getId());
        tagValues.add(ExifTag.Sharpness.getId());
        tagValues.add(ExifTag.Contrast.getId());
        tagValues.add(ExifTag.UserComment.getId());
        tagValues.add(ExifTag.ImageWidth.getId());
        tagValues.add(ExifTag.ImageLength.getId());
        tagValues.add(ExifTag.BitsPerSample.getId());
        tagValues.add(ExifTag.DateTimeDigitized.getId());
        tagValues.add(ExifTag.FileSource.getId());
        tagValues.add(ExifTag.DateTime.getId());
        tagValues.add(ExifTag.Software.getId());

        int size = tagValues.size();
        for (int i = 0; i < size; i++) {
            orderOfTagValue.put(tagValues.get(i), i);
        }
    }

    @Override
    public int compare(IFDEntry o1, IFDEntry o2) {
        int tag1 = o1.getEntryMeta().getTag();
        int tag2 = o2.getEntryMeta().getTag();
        if (orderOfTagValue.containsKey(tag1) && orderOfTagValue.containsKey(tag2)) {
            return orderOfTagValue.get(tag1) - orderOfTagValue.get(tag2);
        }
        return tag1 - tag2;
    }
}
