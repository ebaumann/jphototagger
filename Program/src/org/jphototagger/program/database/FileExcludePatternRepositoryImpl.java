package org.jphototagger.program.database;

import java.util.List;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.repository.FileExcludePatternRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileExcludePatternRepository.class)
public final class FileExcludePatternRepositoryImpl implements FileExcludePatternRepository {

    private final DatabaseFileExcludePatterns db = DatabaseFileExcludePatterns.INSTANCE;

    @Override
    public boolean deleteFileExcludePattern(String pattern) {
        return db.deleteFileExcludePattern(pattern);
    }

    @Override
    public int deleteMatchingFiles(List<String> patterns, ProgressListener listener) {
        return db.deleteMatchingFiles(patterns, listener);
    }

    @Override
    public boolean existsFileExcludePattern(String pattern) {
        return db.existsFileExcludePattern(pattern);
    }

    @Override
    public List<String> getAllFileExcludePatterns() {
        return db.getAllFileExcludePatterns();
    }

    @Override
    public boolean insertFileExcludePattern(String pattern) {
        return db.insertFileExcludePattern(pattern);
    }
}
