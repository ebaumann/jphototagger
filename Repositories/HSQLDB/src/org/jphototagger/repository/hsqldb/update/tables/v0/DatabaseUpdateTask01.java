package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.lib.util.Version;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.update.tables.DatabaseUpdateTask;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = DatabaseUpdateTask.class)
public final class DatabaseUpdateTask01 extends Database implements DatabaseUpdateTask {

    private static final Version UPDATES_TO_DB_VERSION = new Version(0, 10, 0);
    private static final Version MIN_REQUIRED_DB_VERSION = new Version(0, 0, 0);

    @Override
    public void preCreateTables() {

        // DO NOT ADD FURTHER TASKS, implement DatabaseUpdateTask's as service providers!

        Connection con = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            // Never change the order!
            new UpdateTablesMakePlural().update(con);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            free(con);
        }
    }

    @Override
    public void postCreateTables() {

        // DO NOT ADD FURTHER TASKS, implement DatabaseUpdateTask's as service providers!

        Connection con = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            // Never change the order!
            new UpdateTablesDropTables().update(con);
            new UpdateTablesDropColumns().update(con);
            new UpdateTablesRenameColumns().update(con);
            new UpdateTablesInsertColumns().update(con);
            new UpdateTablesIndexes().update(con);
            new UpdateTablesPrimaryKeys().update(con);
            new UpdateTablesXmpLastModified().update(con);
            new UpdateTablesPrograms().update(con);
            new UpdateTablesDeleteInvalidExif().update(con);
            new UpdateTablesThumbnails().update(con);
            new UpdateTablesDropCategories().update(con);
            new UpdateTablesXmpDcSubjects().update(con);
            new UpdateTablesMake1n().update(con);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            free(con);
        }
    }

    @Override
    public Version getUpdatesToDatabaseVersion() {
        return UPDATES_TO_DB_VERSION;
    }

    @Override
    public boolean canUpdateDatabaseVersion(Version version) {
        return UPDATES_TO_DB_VERSION.compareTo(MIN_REQUIRED_DB_VERSION) >= 0;
    }

    @Override
    public String toString() {
        return "Database Update Task from Database Version 0.0.0 up to Database Version 0.10.0";
    }
}
