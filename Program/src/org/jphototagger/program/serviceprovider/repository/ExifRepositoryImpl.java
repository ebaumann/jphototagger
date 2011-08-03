package org.jphototagger.program.serviceprovider.repository;

import java.io.File;

import org.jphototagger.domain.repository.ExifRepository;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifRepository.class)
public final class ExifRepositoryImpl implements ExifRepository {

    @Override
    public Exif getExif(File imageFile) {
        return DatabaseImageFiles.INSTANCE.getExifOfImageFile(imageFile);
    }
}
