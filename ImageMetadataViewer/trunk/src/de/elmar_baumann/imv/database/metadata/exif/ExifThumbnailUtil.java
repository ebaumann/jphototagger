package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.imv.image.metadata.exif.IdfEntryProxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utils for thumbnails embedded in EXIF metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/10
 */
public final class ExifThumbnailUtil {

    private static final Map<String, Double> rotationAngleOfString =
            new HashMap<String, Double>();


    static {
        rotationAngleOfString.put("(0, 0) is top-left", new Double(0)); // 1 // NOI18N
        rotationAngleOfString.put("(0, 0) is top-right", new Double(0)); // 2 // NOI18N
        rotationAngleOfString.put("0, 0) is bottom-right", new Double(180)); // 3 // NOI18N
        rotationAngleOfString.put("(0, 0) is bottom-left", new Double(180)); // 4 // NOI18N
        rotationAngleOfString.put("(0, 0) is left-top", new Double(90)); // 5 // NOI18N
        rotationAngleOfString.put("(0, 0) is right-top", new Double(90)); // 6 // NOI18N
        rotationAngleOfString.put("(0, 0) is right-bottom", new Double(270)); // 7 // NOI18N
        rotationAngleOfString.put("(0, 0) is left-bottom", new Double(270)); // 8 // NOI18N
    }

    /**
     * Returns the rotation angle of an embedded thumbnail.
     *
     * @param  entries EXIF metadata
     * @return         rotation angle
     */
    public static double getThumbnailRotationAngle(List<IdfEntryProxy> entries) {
        IdfEntryProxy entry = ExifMetadata.findEntryWithTag(entries, 274);
        if (entry != null) {
            Double angle = rotationAngleOfString.get(entry.toString());
            if (angle == null) {
                return 0;
            }
            return angle.doubleValue();
        }
        return 0;
    }

    private ExifThumbnailUtil() {
    }
}
