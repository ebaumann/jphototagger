package org.jphototagger.repository.hsqldb.update.tables;


import org.jphototagger.lib.util.Version;

/**
 * @author Elmar Baumann
 */
public interface DatabaseUpdateTask {

    Version getUpdatesToDatabaseVersion();

    boolean canUpdateDatabaseVersion(Version version);

    void preCreateTables();

    void postCreateTables();
}
