package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    ProgressDialog dialog;

    static UpdateTables getInstance() {
        return instance;
    }

    private UpdateTables() {
        initDialog();
    }

    private void initDialog() {
        dialog = new ProgressDialog(null);
        dialog.setEnabledClose(false);
        dialog.setEnabledStop(false);
        dialog.setTitle(Bundle.getString("UpdateTables.InfoMessage.Title"));
        dialog.setIntermediate(true);
    }

    synchronized void update(Connection connection) throws SQLException {
        updateXmp(connection);
        dialog.setVisible(false);
    }

    private void message(String text) {
        dialog.setInfoText(text);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    synchronized private void updateXmp(Connection connection) throws SQLException {
        addColumnXmpLastModified(connection);
    }

    synchronized private void addColumnXmpLastModified(Connection connection) throws SQLException {
        if (!DatabaseMetadata.getInstance().existsColumn(connection, "xmp", "lastmodified")) { // NOI18N
            Statement stmt = connection.createStatement();
            message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.AddColumn"));
            stmt.execute("ALTER TABLE xmp ADD COLUMN lastmodified BIGINT"); // NOI18N
            message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.AddIndex"));
            stmt.execute("CREATE INDEX idx_xmp_lastmodified ON xmp (lastmodified)"); // NOI18N
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
            "UPDATE xmp SET lastmodified = ? WHERE id = ?");
        DatabaseImageFiles dbFiles = DatabaseImageFiles.getInstance();
        long idXmp = -1;
        long idFiles = -1;
        int count = 0;
        ResultSet rsXmp = stmtQueryXmp.executeQuery("SELECT id, id_files FROM xmp");
        while (rsXmp.next()) {
            idXmp = rsXmp.getLong(1);
            idFiles = rsXmp.getLong(2);
            stmtUpdate.setLong(1, dbFiles.getLastModifiedImageFile(idFiles));
            stmtUpdate.setLong(2, idXmp);
            stmtUpdate.execute();
            dialog.setValue(++count);
        }
        stmtQueryXmp.close();
        stmtUpdate.close();
    }

    private void setDialogCopyLastModifiedToXmp() {
        message(Bundle.getString("UpdateTables.InfoMessage.AddColumnXmpLastModified.SetLastModified"));
        dialog.setIntermediate(false);
        dialog.setMinimum(0);
        dialog.setMaximum(DatabaseStatistics.getInstance().getXmpCount());
        dialog.setValue(0);
    }
}
