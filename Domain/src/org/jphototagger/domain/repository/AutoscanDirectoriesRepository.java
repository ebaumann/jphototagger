package org.jphototagger.domain.repository;

import java.io.File;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface AutoscanDirectoriesRepository {

    boolean deleteDirectory(File directory);

    boolean existsDirectory(File directory);

    List<File> getAllDirectories();

    boolean insertDirectory(File directory);
}
