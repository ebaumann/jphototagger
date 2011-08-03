package org.jphototagger.domain.repository;

import java.io.File;
import org.jphototagger.domain.exif.Exif;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ExifRepository {

    Exif getExif(File imageFile);
}
