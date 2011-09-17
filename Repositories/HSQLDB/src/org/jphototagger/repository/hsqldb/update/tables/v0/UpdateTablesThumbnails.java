package org.jphototagger.repository.hsqldb.update.tables.v0;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.jphototagger.api.storage.ThumbnailsDirectoryProvider;
import org.openide.util.Lookup;

import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.image.util.ImageUtil;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.repository.hsqldb.Database;

/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesThumbnails extends Database {

    private static final int FETCH_MAX_ROWS = 1000;
    private static final String KEY_UPATED_THUMBNAILS_NAMES_HASH_1 = "Updated_Thumbnails_Names_Hash_1";    // Never change this!
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesThumbnails.class.getName());
    private final ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
    private int count;

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Writing Thumbnails from database into file system");
        writeThumbnailsFromTableIntoFilesystem(con);
        convertThumbnailIdNamesIntoHashNames(con);
    }

    public void writeThumbnailsFromTableIntoFilesystem(Connection con) throws SQLException {
        count = getCount(con);

        int current = 1;

        for (int offset = 0; offset < count; offset += FETCH_MAX_ROWS) {
            current = updateRows(con, current, count);
        }

        if (count > 0) {
            RepositoryMaintainance repo = Lookup.getDefault().lookup(RepositoryMaintainance.class);

            repo.compressRepository();
        }
    }

    private int updateRows(Connection con, int current, int cnt) throws SQLException {
        String sql = "SELECT TOP " + FETCH_MAX_ROWS + " " + "id, thumbnail FROM files WHERE thumbnail IS NOT NULL";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
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
            LOGGER.log(Level.FINER, stmt.toString());
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
                Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
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
            if (!FileLock.INSTANCE.lockLogWarning(tnFile, UpdateTablesThumbnails.class)) {
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
            Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
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
            if (appPropertiesRepo.getBoolean(KEY_UPATED_THUMBNAILS_NAMES_HASH_1)) {
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
                    LOGGER.log(Level.FINEST, sql);
                    rs = stmt.executeQuery();

                    if (rs.next()) {
                        File imageFile = Database.getFile(rs.getString(1));

                        convertThumbnail(id, imageFile);
                    } else {
                        if (!file.delete()) {    // orphaned thumbnail
                            LOGGER.log(Level.WARNING, "Can't delete orphaned Thumbnail ''{0}''!", file);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            appPropertiesRepo.setBoolean(KEY_UPATED_THUMBNAILS_NAMES_HASH_1, true);
        } catch (Exception ex) {
            Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            Database.close(rs, stmt);
        }
    }

    private File[] getThumbnailFiles() {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File dir = provider.getThumbnailsDirectory();

        if (!dir.isDirectory()) {
            return new File[0];
        }

        return dir.listFiles(new RegexFileFilter("[0-9]+", ""));
    }

    private File getOldThumbnailFile(long id) {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        String directoryName = provider.getThumbnailsDirectory().getAbsolutePath();

        try {
            FileUtil.ensureDirectoryExists(new File(directoryName));

            return new File(directoryName + File.separator + id);
        } catch (Exception ex) {
            Logger.getLogger(UpdateTablesThumbnails.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private void convertThumbnail(long oldId, File imgFile) {
        if (imgFile == null) {
            return;
        }

        File oldTnFile = getOldThumbnailFile(oldId);

        if ((oldTnFile == null) || !oldTnFile.exists()) {
            return;
        }

        ThumbnailsRepository tnRepo = Lookup.getDefault().lookup(ThumbnailsRepository.class);
        File newTnFile = tnRepo.findThumbnailFile(imgFile);

        if ((newTnFile != null) && newTnFile.exists()) {
            if (!oldTnFile.delete()) {
                LOGGER.log(Level.WARNING, "Can't delete old thumbnail ''{0}''!", oldTnFile);
            }
        } else {
            if (!oldTnFile.renameTo(newTnFile)) {
                LOGGER.log(Level.WARNING, "Can't rename thumbnail ''{0}'' to ''{1}''!", new Object[]{oldTnFile, newTnFile});
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

            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                cnt = rs.getInt(1);
            }
        } finally {
            Database.close(rs, stmt);
        }

        return cnt;
    }
}
