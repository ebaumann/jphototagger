package org.jphototagger.program.serviceprovider.repository;

import java.io.File;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.services.repository.ExifRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ExifRepositoryImpl implements ExifRepository {

    @Override
    public Exif getExif(File imageFile) {
        return DatabaseImageFiles.INSTANCE.getExifOfImageFile(imageFile);
    }
}
