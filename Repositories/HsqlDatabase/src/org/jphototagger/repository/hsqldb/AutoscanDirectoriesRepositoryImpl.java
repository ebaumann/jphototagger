package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.util.List;

import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = AutoscanDirectoriesRepository.class)
public final class AutoscanDirectoriesRepositoryImpl implements AutoscanDirectoriesRepository {

    private final DatabaseAutoscanDirectories db = DatabaseAutoscanDirectories.INSTANCE;

    @Override
    public boolean deleteAutoscanDirectory(File directory) {
        return db.deleteDirectory(directory);
    }

    @Override
    public boolean existsAutoscanDirectory(File directory) {
        return db.existsDirectory(directory);
    }

    @Override
    public List<File> findAllAutoscanDirectories() {
        return db.getAllDirectories();
    }

    @Override
    public boolean saveAutoscanDirectory(File directory) {
        return db.insertDirectory(directory);
    }
}