package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 * @author Elmar Baumann
 */
final class UpdateTablesXmpLastModified {

    private static final Logger LOGGER = Logger.getLogger(UpdateTablesXmpLastModified.class.getName());

    void update(Connection con) throws SQLException {
        removeColumnXmpLastModifiedFromTableXmp(con);
        addColumnXmpLastModifiedToTableFiles(con);
    }

    private void removeColumnXmpLastModifiedFromTableXmp(Connection con) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, "xmp", "lastmodified")) {
            Database.execute(con, "ALTER TABLE xmp DROP COLUMN lastmodified");
        }
    }

    private void addColumnXmpLastModifiedToTableFiles(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "files", "xmp_lastmodified")) {
            Database.execute(con, "ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT");
            copyLastModifiedToXmp(con);
        }
    }

    // too slow and no feedback:
    // "UPDATE files SET xmp_lastmodified = lastmodified"
    private void copyLastModifiedToXmp(Connection con) throws SQLException {
        PreparedStatement stmtUpdate = null;
        Statement stmtQuery = null;
        ResultSet rsQuery = null;

        try {
            stmtQuery = con.createStatement();
            stmtUpdate = con.prepareStatement("UPDATE files SET xmp_lastmodified = ? WHERE id = ?");

            long lastModified = -1;
            long idFiles = -1;
            String sql = "SELECT id, lastmodified FROM files";

            LOGGER.log(Level.FINEST, sql);
            rsQuery = stmtQuery.executeQuery(sql);

            while (rsQuery.next()) {
                idFiles = rsQuery.getLong(1);
                lastModified = rsQuery.getLong(2);
                stmtUpdate.setLong(1, lastModified);
                stmtUpdate.setLong(2, idFiles);
                LOGGER.log(Level.FINER, stmtUpdate.toString());
                stmtUpdate.executeUpdate();
            }
        } finally {
            Database.close(rsQuery, stmtQuery);
            Database.close(stmtUpdate);
        }
    }
}
