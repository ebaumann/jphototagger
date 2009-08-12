package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import de.elmar_baumann.lib.image.util.ImageUtil;
import de.elmar_baumann.lib.io.FileLock;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.filefilter.RegexFileFilter;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
 * @version 2009-04-29
 */
final class UpdateTablesThumbnails extends Database {

    private static final UpdateTablesMessages UPDATE_TABLES_MESSAGES =
            UpdateTablesMessages.INSTANCE;
    private static final ProgressDialog PROGRESS_DIALOG =
            UPDATE_TABLES_MESSAGES.getProgressDialog();
    private static final int FETCH_MAX_ROWS = 1000;

    static void update(Connection connection) throws SQLException {
        writeThumbnailsFromTableIntoFilesystem(connection);
        convertThumbnailIdNamesIntoHashNames(connection);
    }

    public static void writeThumbnailsFromTableIntoFilesystem(
            Connection connection)
            throws SQLException {
        int count = getCount(connection);
        int current = 1;
        setProgressDialogRange(count);
        for (int offset = 0; offset < count;
                offset += FETCH_MAX_ROWS) {
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
            setMessageCurrentFile(id, current, count,
                    "UpdateTablesThumbnails.Info.WriteCurrentThumbnail.Table"); // NOI18N
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
                writeThumbnail(thumbnail, id);
            } catch (IOException ex) {
                AppLog.logSevere(UpdateTablesThumbnails.class, ex);
            }
        }
    }

    public static void writeThumbnail(Image thumbnail, long id) {
        FileOutputStream fos = null;
        File tnFile = getThumbnailfile(id);
        try {
            if (!IoUtil.lockLogWarning(tnFile, UpdateTablesThumbnails.class))
                return;
            fos = new FileOutputStream(tnFile);
            fos.getChannel().lock();
            ByteArrayInputStream is =
                    ImageUtil.getByteArrayInputStream(thumbnail, "jpeg"); // NOI18N
            if (is != null) {
                int nextByte;
                while ((nextByte = is.read()) != -1) {
                    fos.write(nextByte);
                }
            }
        } catch (Exception ex) {
            AppLog.logSevere(ThumbnailUtil.class, ex);
        } finally {
            FileLock.INSTANCE.unlock(tnFile, UpdateTablesThumbnails.class);
            closeStream(fos);
        }
    }

    private static void closeStream(FileOutputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception ex) {
                AppLog.logSevere(ThumbnailUtil.class, ex);
            }
        }
    }

    /**
     * Convert all thumbnail names to new format using hashes.
     */
    private static void convertThumbnailIdNamesIntoHashNames(
            Connection connection) {
        try {
            File[] thumbnailFiles = getThumbnailFiles();
            int filecount = thumbnailFiles.length;
            setProgressDialogRange(filecount);
            String sql = "SELECT filename FROM files WHERE id = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            int fileIndex = 0;
            for (File file : thumbnailFiles) {
                try {
                    long id = Long.parseLong(file.getName());
                    stmt.setLong(1, id);
                    AppLog.logFinest(UpdateTablesThumbnails.class,
                            AppLog.USE_STRING, sql);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String filename = rs.getString(1);
                        convertThumbnailName(
                                id, ThumbnailUtil.getMd5File(filename));
                    } else {
                        file.delete(); // orphaned thumbnail
                    }
                    setMessageCurrentFile(id, ++fileIndex, filecount,
                            "UpdateTablesThumbnails.Info.WriteCurrentThumbnail.Hash"); // NOI18N
                } catch (NumberFormatException ex) {
                    AppLog.logSevere(UpdateTablesThumbnails.class, ex);
                }
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(UpdateTablesThumbnails.class, ex);
        }
    }

    private static File[] getThumbnailFiles() {
        File dir = new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());
        if (!dir.isDirectory()) return new File[0];
        return dir.listFiles(new RegexFileFilter("[0-9]+", ""));
    }

    private static File getThumbnailfile(long id) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();
        FileUtil.ensureDirectoryExists(new File(dir));
        return new File(dir + File.separator + id);
    }

    private static void convertThumbnailName(long oldId, String newHash) {
        File oldFile = getThumbnailfile(oldId);
        File newFile = ThumbnailUtil.getThumbnailfile(newHash);
        if (newFile.exists()) {
            oldFile.delete();
        } else {
            oldFile.renameTo(newFile);
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
                "UpdateTablesThumbnails.Info.CompressDatabase")); // NOI18N
        PROGRESS_DIALOG.setIndeterminate(true);
        DatabaseMaintainance.INSTANCE.compressDatabase();
        PROGRESS_DIALOG.setIndeterminate(false);
    }

    private static void setProgressDialogRange(long count) {
        PROGRESS_DIALOG.setIndeterminate(false);
        PROGRESS_DIALOG.setMinimum(0);
        PROGRESS_DIALOG.setMaximum((int) count);
        PROGRESS_DIALOG.setValue(0);
    }

    private static void setMessageCurrentFile(
            long id, long current, long count, String message) {
        UPDATE_TABLES_MESSAGES.message(
                Bundle.getString(message, id, current, count));
        PROGRESS_DIALOG.setValue((int) current);
    }
}
