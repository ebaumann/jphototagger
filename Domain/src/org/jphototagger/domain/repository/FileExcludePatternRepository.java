package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.api.event.ProgressListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface FileExcludePatternRepository {

    boolean deleteFileExcludePattern(String pattern);

    int deleteMatchingFiles(List<String> patterns, ProgressListener listener);

    boolean existsFileExcludePattern(String pattern);

    List<String> getAllFileExcludePatterns();

    boolean insertFileExcludePattern(String pattern);
}
