package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.lib.util.Version;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;
import org.jphototagger.repository.hsqldb.update.tables.DatabaseUpdateTask;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = DatabaseUpdateTask.class)
public final class DatabaseUpdateTask02 extends Database implements DatabaseUpdateTask {

    private static final Logger LOGGER = Logger.getLogger(DatabaseUpdateTask02.class.getName());

    @Override
    public Version getUpdatesToDatabaseVersion() {
        return new Version(0, 9999, 0);
    }

    @Override
    public boolean canUpdateDatabaseVersion(Version version) {
        return true;
    }

    @Override
    public void preCreateTables() {
        // Do nothing
    }

    @Override
    public void postCreateTables() {
        try {
            update();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void update() throws SQLException {
        Connection con = null;
        try {
            con = getConnection();
            dropThumbnailColumn(con);
            addChecksumColumn(con);
        } finally {
            free(con);
        }
    }

    private void dropThumbnailColumn(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "files", "thumbnail")) {
            return;
        }
        LOGGER.log(Level.INFO, "Deleting column 'thumbnail' from table 'files'");
        Database.execute(con, "ALTER TABLE files DROP COLUMN thumbnail");
    }

    private void addChecksumColumn(Connection con) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, "files", "checksum")) {
            return;
        }
        LOGGER.log(Level.INFO, "Adding column 'checksum' to table 'files'");
        Database.execute(con, "ALTER TABLE files ADD COLUMN checksum VARCHAR(255)");
    }
}
