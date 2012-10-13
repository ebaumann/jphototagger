package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FileExcludePatternsRepository.class)
public final class FileExcludePatternsRepositoryImpl implements FileExcludePatternsRepository {

    @Override
    public boolean deleteFileExcludePattern(String pattern) {
        return FileExcludePatternsDatabase.INSTANCE.deleteFileExcludePattern(pattern);
    }

    @Override
    public int deleteMatchingFiles(List<String> patterns, ProgressListener listener) {
        return FileExcludePatternsDatabase.INSTANCE.deleteMatchingFiles(patterns, listener);
    }

    @Override
    public boolean existsFileExcludePattern(String pattern) {
        return FileExcludePatternsDatabase.INSTANCE.existsFileExcludePattern(pattern);
    }

    @Override
    public List<String> findAllFileExcludePatterns() {
        return FileExcludePatternsDatabase.INSTANCE.getAllFileExcludePatterns();
    }

    @Override
    public boolean saveFileExcludePattern(String pattern) {
        return FileExcludePatternsDatabase.INSTANCE.insertFileExcludePattern(pattern);
    }
}
