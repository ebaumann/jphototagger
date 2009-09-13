/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.cache.PersistentThumbnails;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.DatabaseApplication;
import de.elmar_baumann.imv.database.DatabaseMaintainance;
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

    private static final String KEY_UPATED_THUMBNAILS_NAMES_HASH_1 =
            "Updated_Thumbnails_Names_Hash_1"; // NOI18N Never change this!
    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog progress = messages.getProgressDialog();
    private static final int FETCH_MAX_ROWS = 1000;

    void update(Connection connection) throws SQLException {
        writeThumbnailsFromTableIntoFilesystem(connection);
        convertThumbnailIdNamesIntoHashNames(connection);
    }

    public void writeThumbnailsFromTableIntoFilesystem(Connection connection)
            throws SQLException {
        int count = getCount(connection);
        int current = 1;
        setProgressDialogRange(count);
        for (int offset = 0; offset < count; offset += FETCH_MAX_ROWS) {
            current = updateRows(connection, current, count);
        }
        if (count > 0) {
            compress();
        }
    }

    private int updateRows(Connection connection, int current, int count)
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
            progress.setValue(current++);
        }
        clean(stmt, rs);
        return current;
    }

    private void setThumbnailNull(Connection connection, long id)
            throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE files SET thumbnail = NULL WHERE id = ?"); // NOI18N
        stmt.setLong(1, id);
        AppLog.logFiner(UpdateTablesThumbnails.class, stmt.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private void writeThumbnail(InputStream inputStream, long id) {
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

    public void writeThumbnail(Image thumbnail, long id) {
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
            AppLog.logSevere(UpdateTablesThumbnails.class, ex);
        } finally {
            FileLock.INSTANCE.unlock(tnFile, UpdateTablesThumbnails.class);
            closeStream(fos);
        }
    }

    private void closeStream(FileOutputStream fis) {
        if (fis != null) {
            try {
                fis.close();
            } catch (Exception ex) {
                AppLog.logSevere(UpdateTablesThumbnails.class, ex);
            }
        }
    }

    /**
     * Convert all thumbnail names to new format using hashes.
     */
    private void convertThumbnailIdNamesIntoHashNames(Connection connection) {
        try {
            if (DatabaseApplication.INSTANCE.getBoolean(
                    KEY_UPATED_THUMBNAILS_NAMES_HASH_1)) return;
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
                                id, PersistentThumbnails.getMd5File(filename));
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
            DatabaseApplication.INSTANCE.setBoolean(
                    KEY_UPATED_THUMBNAILS_NAMES_HASH_1, true);
        } catch (SQLException ex) {
            AppLog.logSevere(UpdateTablesThumbnails.class, ex);
        }
    }

    private File[] getThumbnailFiles() {
        File dir = new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());
        if (!dir.isDirectory()) return new File[0];
        return dir.listFiles(new RegexFileFilter("[0-9]+", "")); // NOI18N
    }

    private File getThumbnailfile(long id) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();
        FileUtil.ensureDirectoryExists(new File(dir));
        return new File(dir + File.separator + id);
    }

    private void convertThumbnailName(long oldId, String newHash) {
        File oldFile = getThumbnailfile(oldId);
        File newFile = PersistentThumbnails.getThumbnailfile(newHash);
        if (newFile.exists()) {
            oldFile.delete();
        } else {
            oldFile.renameTo(newFile);
        }
    }

    private void clean(Statement stmt, ResultSet rs) throws SQLException {
        stmt.close();
        stmt = null;
        rs = null;
        System.gc();
    }

    private int getCount(Connection connection) throws SQLException {
        int count = 0;
        Statement stmt = connection.createStatement();
        String sql = "SELECT  COUNT(*) FROM files WHERE thumbnail IS NOT NULL"; // NOI18N
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

    private void compress() {
        messages.message(Bundle.getString(
                "UpdateTablesThumbnails.Info.CompressDatabase")); // NOI18N
        progress.setIndeterminate(true);
        DatabaseMaintainance.INSTANCE.compressDatabase();
        progress.setIndeterminate(false);
    }

    private void setProgressDialogRange(long count) {
        progress.setIndeterminate(false);
        progress.setMinimum(0);
        progress.setMaximum((int) count);
        progress.setValue(0);
    }

    private void setMessageCurrentFile(
            long id, long current, long count, String message) {
        messages.message(Bundle.getString(message, id, current, count));
        progress.setValue((int) current);
    }
}
