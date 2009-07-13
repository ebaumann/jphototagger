package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/04/29
 */
final class UpdateTablesThumbnails extends Database {

    private static final UpdateTablesMessages UPDATE_TABLES_MESSAGES =
            UpdateTablesMessages.INSTANCE;
    private static final ProgressDialog PROGRESS_DIALOG =
            UPDATE_TABLES_MESSAGES.getProgressDialog();
    private static final int FETCH_MAX_ROWS = 1000;

    static void update(Connection connection) throws SQLException {
        int count = getCount(connection);
        int current = 1;
        initDialog(count);
        for (int offset = 0; offset < count; offset += FETCH_MAX_ROWS) {
            current = updateRows(connection, current, count);
        }
        if (count > 0) {
            compress();
        }
    }

    private static int updateRows(Connection connection, int current, int count)
            throws SQLException {
        String sql = "SELECT TOP " + FETCH_MAX_ROWS + " " + // NOI18N
                "id, thumbnail FROM files WHERE thumbnail IS NOT NULL"; // NOI18N
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            long id = rs.getInt(1);
            InputStream inputStream = rs.getBinaryStream(2);
            setThumbnailNull(connection, id);
            setMessage(id, current, count);
            writeThumbnail(inputStream, id);
            PROGRESS_DIALOG.setValue(current++);
        }
        clean(stmt, rs);
        return current;
    }

    private static void setThumbnailNull(Connection connection, long id) throws
            SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE files SET thumbnail = NULL WHERE id = ?"); // NOI18N
        stmt.setLong(1, id);
        AppLog.logFiner(UpdateTablesThumbnails.class, stmt.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private static void writeThumbnail(InputStream inputStream, long id) {
        if (inputStream != null) {
            try {
                int bytecount = inputStream.available();
                byte[] bytes = new byte[bytecount];
                inputStream.read(bytes, 0, bytecount);
                ImageIcon icon = new ImageIcon(bytes);
                Image thumbnail = icon.getImage();
                ThumbnailUtil.writeThumbnail(thumbnail, id);
            } catch (IOException ex) {
                AppLog.logWarning(UpdateTablesThumbnails.class, ex);
            }
        }
    }

    private static void clean(Statement stmt, ResultSet rs) throws SQLException {
        stmt.close();
        stmt = null;
        rs = null;
        System.gc();
    }

    private static int getCount(Connection connection) throws SQLException {
        int count = 0;
        Statement stmt = connection.createStatement();
        String sql = "SELECT  COUNT(*) FROM files WHERE thumbnail IS NOT NULL"; // NOI18N
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    private static void compress() {
        UPDATE_TABLES_MESSAGES.message(Bundle.getString(
                "UpdateTablesThumbnails.Information.CompressDatabase")); // NOI18N
        PROGRESS_DIALOG.setIndeterminate(true);
        DatabaseMaintainance.INSTANCE.compressDatabase();
        PROGRESS_DIALOG.setIndeterminate(false);
    }

    private static void initDialog(long count) {
        PROGRESS_DIALOG.setIndeterminate(false);
        PROGRESS_DIALOG.setMinimum(0);
        PROGRESS_DIALOG.setMaximum((int) count);
        PROGRESS_DIALOG.setValue(0);
    }

    private static void setMessage(long id, long current, long count) {
        UPDATE_TABLES_MESSAGES.message(Bundle.getString(
                "UpdateTablesThumbnails.Information.WriteCurrentThumbnail", // NOI18N
                id, current, count));
    }
}
