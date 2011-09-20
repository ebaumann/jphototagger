package org.jphototagger.api.image.exif;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExifInfo {

    double getRotationAngleOfEmbeddedThumbnail(File file);
}
