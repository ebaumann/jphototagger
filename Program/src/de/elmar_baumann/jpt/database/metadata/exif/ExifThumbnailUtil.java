/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database.metadata.exif;

import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.image.metadata.exif.ExifTag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utils for thumbnails embedded in EXIF metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-10
 */
public final class ExifThumbnailUtil {

    private static final Map<String, Double> ROTATION_ANGLE_OF_STRING = new HashMap<String, Double>();

    static {
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is top-left"    , new Double(0));   // 1
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is top-right"   , new Double(0));   // 2
        ROTATION_ANGLE_OF_STRING.put("0, 0) is bottom-right" , new Double(180)); // 3
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is bottom-left" , new Double(180)); // 4
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is left-top"    , new Double(90));  // 5
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is right-top"   , new Double(90));  // 6
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is right-bottom", new Double(270)); // 7
        ROTATION_ANGLE_OF_STRING.put("(0, 0) is left-bottom" , new Double(270)); // 8
    }

    /**
     * Returns the rotation angle of an embedded thumbnail.
     *
     * @param  exifTags EXIF tags
     * @return          rotation angle
     */
    public static double getThumbnailRotationAngle(List<ExifTag> exifTags) {
        ExifTag exifTag = ExifMetadata.getExifTagIn(exifTags, 274);
        if (exifTag != null) {
            Double angle = ROTATION_ANGLE_OF_STRING.get(exifTag.stringValue());
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
