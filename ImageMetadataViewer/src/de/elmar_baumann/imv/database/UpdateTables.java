package de.elmar_baumann.imv.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/23
 */
class UpdateTables extends Database {

    private static UpdateTables instance = new UpdateTables();

    static UpdateTables getInstance() {
        return instance;
    }

    private UpdateTables() {
    }

    synchronized void update(Connection connection) throws SQLException {
        updateXmp(connection);
    }

    synchronized private void updateXmp(Connection connection) throws SQLException {
        addColumnXmpLastModified(connection);
    }

    synchronized private void addColumnXmpLastModified(Connection connection) throws SQLException {
        if (!DatabaseMetadata.getInstance().existsColumn(connection, "xmp", "lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            stmt.execute("ALTER TABLE xmp ADD COLUMN lastmodified BIGINT"); // NOI18N
            stmt.execute("CREATE INDEX idx_xmp_lastmodified ON xmp (lastmodified)"); // NOI18N
            copyLastModifiedToXmp(connection);
        }
    }

    synchronized private void copyLastModifiedToXmp(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("UPDATE xmp set lastmodified = " +
            " (SELECT lastmodified FROM files WHERE files.id = xmp.id_files)");
    }
}
