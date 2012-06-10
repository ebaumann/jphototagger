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
public final class DatabaseUpdateTask03 extends Database implements DatabaseUpdateTask {

    private static final Logger LOGGER = Logger.getLogger(DatabaseUpdateTask03.class.getName());

    @Override
    public Version getUpdatesToDatabaseVersion() {
        return new Version(0, Integer.MAX_VALUE, 0);
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
            addExifGpsColumns(con);
        } finally {
            free(con);
        }
    }

    private void addExifGpsColumns(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "exif", "exif_gps_latitude")) {
            LOGGER.log(Level.INFO, "Adding column 'exif_gps_latitude' to table 'exif'");
            Database.execute(con, "ALTER TABLE exif ADD COLUMN exif_gps_latitude DOUBLE");
        }
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "exif", "exif_gps_longitude")) {
            LOGGER.log(Level.INFO, "Adding column 'exif_gps_longitude' to table 'exif'");
            Database.execute(con, "ALTER TABLE exif ADD COLUMN exif_gps_longitude DOUBLE");
        }
    }
}
