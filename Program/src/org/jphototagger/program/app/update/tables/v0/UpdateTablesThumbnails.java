package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.lib.image.util.ImageUtil;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.cache.PersistentThumbnails;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseApplicationProperties;
import org.jphototagger.program.database.DatabaseMaintainance;
import org.jphototagger.program.io.RuntimeUtil;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.awt.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
 * @author Elmar Baumann
 */
final class UpdateTablesThumbnails extends Database {
    private static final int FETCH_MAX_ROWS = 1000;
    private static final String KEY_UPATED_THUMBNAILS_NAMES_HASH_1 =
        "Updated_Thumbnails_Names_Hash_1";    // Never change this!
    private int count;

    void update(Connection con) throws SQLException {
        startMessage();
        writeThumbnailsFromTableIntoFilesystem(con);
        convertThumbnailIdNamesIntoHashNames(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    public void writeThumbnailsFromTableIntoFilesystem(Connection con) throws SQLException {
        count = getCount(con);

        int current = 1;

        for (int offset = 0; offset < count; offset += FETCH_MAX_ROWS) {
            current = updateRows(con, current, count);
        }

        if (count > 0) {
            DatabaseMaintainance.INSTANCE.compressDatabase();
        }
    }

    private int updateRows(Connection con, int current, int cnt) throws SQLException {
        String sql = "SELECT TOP " + FETCH_MAX_ROWS + " " + "id, thumbnail FROM files WHERE thumbnail IS NOT NULL";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                long id = rs.getInt(1);
                InputStream inputStream = rs.getBinaryStream(2);

                setThumbnailNull(con, id);
                writeThumbnail(inputStream, id);
            }
        } finally {
            Database.close(rs, stmt);
        }

        return current;
    }

    private void setThumbnailNull(Connection con, long id) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement("UPDATE files SET thumbnail = NULL WHERE id = ?");
            stmt.setLong(1, id);
            AppLogger.logFiner(UpdateTablesThumbnails.class, stmt.toString());
            stmt.executeUpdate();
        } finally {
            Database.close(stmt);
        }
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
            } catch (Exception ex) {
                AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
            }
        }
    }

    public void writeThumbnail(Image thumbnail, long id) {
        FileOutputStream fos = null;
        File tnFile = getOldThumbnailFile(id);

        if (tnFile == null) {
            return;
        }

        try {
            if (!RuntimeUtil.lockLogWarning(tnFile, UpdateTablesThumbnails.class)) {
                return;
            }

            fos = new FileOutputStream(tnFile);
            fos.getChannel().lock();

            ByteArrayInputStream is = ImageUtil.getByteArrayInputStream(thumbnail, "jpeg");

            if (is != null) {
                int nextByte;

                while ((nextByte = is.read()) != -1) {
                    fos.write(nextByte);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
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
                AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
            }
        }
    }

    /**
     * Convert all thumbnail names to new format using hashes.
     */
    private void convertThumbnailIdNamesIntoHashNames(Connection con) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            if (DatabaseApplicationProperties.INSTANCE.getBoolean(KEY_UPATED_THUMBNAILS_NAMES_HASH_1)) {
                return;
            }

            File[] thumbnailFiles = getThumbnailFiles();
            int filecount = thumbnailFiles.length;

            count = filecount;

            String sql = "SELECT filename FROM files WHERE id = ?";

            stmt = con.prepareStatement(sql);

            for (File file : thumbnailFiles) {
                try {
                    long id = Long.parseLong(file.getName());

                    stmt.setLong(1, id);
                    AppLogger.logFinest(UpdateTablesThumbnails.class, AppLogger.USE_STRING, sql);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        File imageFile = Database.getFile(rs.getString(1));

                        convertThumbnail(id, imageFile);
                    } else {
                        if (!file.delete()) {    // orphaned thumbnail
                            AppLogger.logWarning(getClass(), "UpdateTablesThumbnails.Error.DeleteThumbnail", file);
                        }
                    }
                } catch (Exception ex) {
                    AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
                }
            }

            DatabaseApplicationProperties.INSTANCE.setBoolean(KEY_UPATED_THUMBNAILS_NAMES_HASH_1, true);
        } catch (Exception ex) {
            AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
        } finally {
            Database.close(rs, stmt);
        }
    }

    private File[] getThumbnailFiles() {
        File dir = new File(UserSettings.INSTANCE.getThumbnailsDirectoryName());

        if (!dir.isDirectory()) {
            return new File[0];
        }

        return dir.listFiles(new RegexFileFilter("[0-9]+", ""));
    }

    private File getOldThumbnailFile(long id) {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();

        try {
            FileUtil.ensureDirectoryExists(dir);

            return new File(dir + File.separator + id);
        } catch (Exception ex) {
            AppLogger.logSevere(UpdateTablesThumbnails.class, ex);
        }

        return null;
    }

    private void convertThumbnail(long oldId, File imgFile) {
        if (imgFile == null) {
            return;
        }

        File oldTnFile = getOldThumbnailFile(oldId);

        if ((oldTnFile == null) ||!oldTnFile.exists()) {
            return;
        }

        File newTnFile = PersistentThumbnails.getThumbnailFile(imgFile);

        if ((newTnFile != null) && newTnFile.exists()) {
            if (!oldTnFile.delete()) {
                AppLogger.logWarning(getClass(), "UpdateTablesThumbnails.Error.DeleteOld", oldTnFile);
            }
        } else {
            if (!oldTnFile.renameTo(newTnFile)) {
                AppLogger.logWarning(getClass(), "UpdateTablesThumbnails.Error.Rename", oldTnFile, newTnFile);
            }
        }
    }

    private int getCount(Connection con) throws SQLException {
        int cnt = 0;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();

            String sql = "SELECT  COUNT(*) FROM files WHERE thumbnail IS NOT NULL";

            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                cnt = rs.getInt(1);
            }
        } finally {
            Database.close(rs, stmt);
        }

        return cnt;
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("UpdateTablesThumbnails.Info"));
    }
}
