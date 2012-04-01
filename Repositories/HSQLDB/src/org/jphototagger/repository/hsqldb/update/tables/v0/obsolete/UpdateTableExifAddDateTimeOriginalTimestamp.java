package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.SQLException;
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
public final class UpdateTableExifAddDateTimeOriginalTimestamp extends Database implements DatabaseUpdateTask {

    @Override
    public Version getUpdatesToDatabaseVersion() {
        return new Version(0, 11, 1);
    }

    @Override
    public boolean canUpdateDatabaseVersion(Version version) {
        return true;
    }

    @Override
    public void preCreateTables() {
        // Ignore
    }

    @Override
    public void postCreateTables() {
        Connection con = null;
        Logger logger = Logger.getLogger(UpdateTableExifAddDateTimeOriginalTimestamp.class.getName());

        try {
            con = getConnection();
            con.setAutoCommit(true);
            if (!DatabaseMetadata.INSTANCE.existsColumn(con, "exif", "exif_date_time_original_timestamp")) {
                logger.info("Adding to table 'exif' column 'exif_date_time_original_timestamp'");
                Database.execute(con, "ALTER TABLE exif ADD COLUMN exif_date_time_original_timestamp BIGINT");
                logger.info("Added to table 'exif' column 'exif_date_time_original_timestamp'");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            free(con);
        }
    }
}
