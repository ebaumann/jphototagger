package org.jphototagger.domain.metadata.exif;

import java.io.File;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExifInfo {

    double getRotationAngleOfEmbeddedThumbnail(File file);
}
