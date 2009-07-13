package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/29
 */
final class UpdateTablesXmpLastModified {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog dialog = messages.getProgressDialog();

    void update(Connection connection) throws SQLException {
        removeColumnXmpLastModifiedFromTableXmp(connection);
        addColumnXmpLastModifiedToTableFiles(connection);
    }

    private void removeColumnXmpLastModifiedFromTableXmp(Connection connection) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(
            connection, "xmp", "lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTablesXmpLastModified.InformationMessage.RemoveColumnXmpLastModified")); // NOI18N
            stmt.execute("ALTER TABLE xmp DROP COLUMN lastmodified"); // NOI18N
        }
    }

    private void addColumnXmpLastModifiedToTableFiles(Connection connection) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(
            connection, "files", "xmp_lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTablesXmpLastModified.InformationMessage.AddColumnXmpLastModified.AddColumn")); // NOI18N
            stmt.execute("ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT"); // NOI18N
            copyLastModifiedToXmp(connection);
        }
    }

    // too slow and no feedback: "UPDATE files SET xmp_lastmodified = lastmodified"
    private void copyLastModifiedToXmp(Connection connection) throws SQLException {
        setProgressDialog();
        Statement stmtQueryXmp = connection.createStatement();
        PreparedStatement stmtUpdate = connection.prepareStatement(
            "UPDATE files SET xmp_lastmodified = ? WHERE id = ?"); // NOI18N
        long lastModified = -1;
        long idFiles = -1;
        int count = 0;
        ResultSet rsQuery = stmtQueryXmp.executeQuery(
            "SELECT id, lastmodified FROM files"); // NOI18N
        while (rsQuery.next()) {
            idFiles = rsQuery.getLong(1);
            lastModified = rsQuery.getLong(2);
            stmtUpdate.setLong(1, lastModified);
            stmtUpdate.setLong(2, idFiles);
            stmtUpdate.execute();
            dialog.setValue(++count);
        }
        stmtQueryXmp.close();
        stmtUpdate.close();
    }

    private void setProgressDialog() {
        messages.message(Bundle.getString("UpdateTablesXmpLastModified.InformationMessage.AddColumnXmpLastModified.SetLastModified")); // NOI18N
        dialog.setIndeterminate(false);
        dialog.setMinimum(0);
        dialog.setMaximum(DatabaseStatistics.INSTANCE.getXmpCount());
        dialog.setValue(0);
    }
}
