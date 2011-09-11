package org.jphototagger.domain.repository;

import java.io.File;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface AutoscanDirectoriesRepository {

    boolean deleteAutoscanDirectory(File directory);

    boolean existsAutoscanDirectory(File directory);

    List<File> getAllAutoscanDirectories();

    boolean insertAutoscanDirectory(File directory);
}
