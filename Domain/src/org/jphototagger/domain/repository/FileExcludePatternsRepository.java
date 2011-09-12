package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.api.event.ProgressListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface FileExcludePatternsRepository {

    boolean deleteFileExcludePattern(String pattern);

    int deleteMatchingFiles(List<String> patterns, ProgressListener listener);

    boolean existsFileExcludePattern(String pattern);

    List<String> findAllFileExcludePatterns();

    boolean saveFileExcludePattern(String pattern);
}
