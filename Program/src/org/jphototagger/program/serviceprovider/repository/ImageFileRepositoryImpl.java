package org.jphototagger.program.serviceprovider.repository;

import java.io.File;
import java.util.Collection;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageFileRepository.class)
public final class ImageFileRepositoryImpl implements ImageFileRepository {

    @Override
    public Collection<? extends File> getAllImageFiles() {
        return DatabaseImageFiles.INSTANCE.getAllImageFiles();
    }

    @Override
    public long getRepositoryImageFileTimestamp(File imageFile) {
        return DatabaseImageFiles.INSTANCE.getImageFileLastModified(imageFile);
    }

    @Override
    public long getRepositoryXmpFileTimestamp(File imageFile) {
        return DatabaseImageFiles.INSTANCE.getLastModifiedXmp(imageFile);
    }
}
