package org.jphototagger.domain.database.repository;

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
