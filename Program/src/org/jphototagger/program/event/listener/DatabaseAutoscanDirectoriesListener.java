package org.jphototagger.program.event.listener;

import java.io.File;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseAutoscanDirectories}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseAutoscanDirectoriesListener {

    /**
     * Will be called if a directory was inserted into
     * {@link org.jphototagger.program.database.DatabaseAutoscanDirectories}.
     *
     * @param directory inserted directory
     */
    void directoryInserted(File directory);

    /**
     * Will be called if a directory was deleted from
     * {@link org.jphototagger.program.database.DatabaseAutoscanDirectories}.
     *
     * @param directory deleted directory
     */
    void directoryDeleted(File directory);
}
