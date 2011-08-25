package org.jphototagger.program.database;

import java.io.File;

import org.jphototagger.domain.repository.ExifRepository;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifRepository.class)
public final class ExifRepositoryImpl implements ExifRepository {

    private final ImageFileRepository repo = Lookup.getDefault().lookup(ImageFileRepository.class);

    @Override
    public Exif getExif(File imageFile) {
        return repo.getExifOfImageFile(imageFile);
    }
}
