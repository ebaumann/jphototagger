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
public class UpdateTablesXmpLastModified {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();

    synchronized void update(Connection connection) throws SQLException {
        removeColumnXmpLastModified(connection);
        addColumnXmpLastModified(connection);
    }

    synchronized private void removeColumnXmpLastModified(Connection connection) throws SQLException {
        if (DatabaseMetadata.getInstance().existsColumn(connection, "xmp", "lastmodified")) {
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTables.InfoMessage.RemoveColumnXmpLastModified"));
            stmt.execute("ALTER TABLE xmp DROP COLUMN lastmodified");
        }
    }

    synchronized private void addColumnXmpLastModified(Connection connection) throws SQLException {
        if (!DatabaseMetadata.getInstance().existsColumn(connection, "files", "xmp_lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.AddColumn"));
            stmt.execute("ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT"); // NOI18N
            copyLastModifiedToXmp(connection);
        }
    }

//      too slow and no feedback
//        stmt.executeUpdate("UPDATE xmp SET xmp.lastmodified = " +
//            " SELECT files.lastmodified FROM files WHERE xmp.id_files = files.id");
    synchronized private void copyLastModifiedToXmp(Connection connection) throws SQLException {
        setDialogCopyLastModifiedToXmp();
        Statement stmtQueryXmp = connection.createStatement();
        PreparedStatement stmtUpdate = connection.prepareStatement(
            "UPDATE files SET xmp_lastmodified = ?");
        long lastModified = -1;
        int count = 0;
        ResultSet rsXmp = stmtQueryXmp.executeQuery("SELECT lastmodified, xmp_lastmodified FROM files");
        while (rsXmp.next()) {
            lastModified = rsXmp.getLong(1);
            stmtUpdate.setLong(1, lastModified);
            stmtUpdate.execute();
            dialog.setValue(++count);
        }
        stmtQueryXmp.close();
        stmtUpdate.close();
    }

    private void setDialogCopyLastModifiedToXmp() {
        messages.message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.SetLastModified"));
        dialog.setIntermediate(false);
        dialog.setMinimum(0);
        dialog.setMaximum(DatabaseStatistics.getInstance().getXmpCount());
        dialog.setValue(0);
    }
}
