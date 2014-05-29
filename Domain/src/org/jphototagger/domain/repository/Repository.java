package org.jphototagger.domain.repository;

import java.io.File;

/**
 * @author Elmar Baumann
 */
public interface Repository {

    void init();

    void shutdown();

    boolean isInit();

    void backupToDirectory(File directory);
}
