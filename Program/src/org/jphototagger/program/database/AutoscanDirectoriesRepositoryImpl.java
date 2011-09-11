package org.jphototagger.program.database;

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
    public boolean deleteDirectory(File directory) {
        return db.deleteDirectory(directory);
    }

    @Override
    public boolean existsDirectory(File directory) {
        return db.existsDirectory(directory);
    }

    @Override
    public List<File> getAllDirectories() {
        return db.getAllDirectories();
    }

    @Override
    public boolean insertDirectory(File directory) {
        return db.insertDirectory(directory);
    }
}
