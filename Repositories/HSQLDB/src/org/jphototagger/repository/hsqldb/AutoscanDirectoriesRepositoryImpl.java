package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.util.List;
import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AutoscanDirectoriesRepository.class)
public final class AutoscanDirectoriesRepositoryImpl implements AutoscanDirectoriesRepository {

    @Override
    public boolean deleteAutoscanDirectory(File directory) {
        return AutoscanDirectoriesDatabase.INSTANCE.deleteDirectory(directory);
    }

    @Override
    public boolean existsAutoscanDirectory(File directory) {
        return AutoscanDirectoriesDatabase.INSTANCE.existsDirectory(directory);
    }

    @Override
    public List<File> findAllAutoscanDirectories() {
        return AutoscanDirectoriesDatabase.INSTANCE.getAllDirectories();
    }

    @Override
    public boolean saveAutoscanDirectory(File directory) {
        return AutoscanDirectoriesDatabase.INSTANCE.insertDirectory(directory);
    }
}
