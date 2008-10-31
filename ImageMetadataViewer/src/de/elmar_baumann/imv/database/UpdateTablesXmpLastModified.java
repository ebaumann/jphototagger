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
class UpdateTablesXmpLastModified {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();

    synchronized void update(Connection connection) throws SQLException {
        removeColumnXmpLastModifiedFromTableXmp(connection);
        addColumnXmpLastModifiedToTableFiles(connection);
    }

    synchronized private void removeColumnXmpLastModifiedFromTableXmp(
        Connection connection) throws SQLException {
        if (DatabaseMetadata.getInstance().existsColumn(
            connection, "xmp", "lastmodified")) {
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTables.InfoMessage.RemoveColumnXmpLastModified"));
            stmt.execute("ALTER TABLE xmp DROP COLUMN lastmodified"); // NOI18N
        }
    }

    synchronized private void addColumnXmpLastModifiedToTableFiles(
        Connection connection) throws SQLException {
        if (!DatabaseMetadata.getInstance().existsColumn(
            connection, "files", "xmp_lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.AddColumn"));
            stmt.execute("ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT"); // NOI18N
            copyLastModifiedToXmp(connection);
        }
    }

    // too slow and no feedback: "UPDATE files SET xmp_lastmodified = lastmodified"
    synchronized private void copyLastModifiedToXmp(
        Connection connection) throws SQLException {
        setProgressDialog();
        Statement stmtQueryXmp = connection.createStatement();
        PreparedStatement stmtUpdate = connection.prepareStatement(
            "UPDATE files SET xmp_lastmodified = ? WHERE id = ?");
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
        messages.message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.SetLastModified"));
        dialog.setIntermediate(false);
        dialog.setMinimum(0);
        dialog.setMaximum(DatabaseStatistics.getInstance().getXmpCount());
        dialog.setValue(0);
    }
}
