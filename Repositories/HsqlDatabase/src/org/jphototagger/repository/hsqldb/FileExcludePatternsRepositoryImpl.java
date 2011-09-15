package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileExcludePatternsRepository.class)
public final class FileExcludePatternsRepositoryImpl implements FileExcludePatternsRepository {

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
    public List<String> findAllFileExcludePatterns() {
        return db.getAllFileExcludePatterns();
    }

    @Override
    public boolean saveFileExcludePattern(String pattern) {
        return db.insertFileExcludePattern(pattern);
    }
}
