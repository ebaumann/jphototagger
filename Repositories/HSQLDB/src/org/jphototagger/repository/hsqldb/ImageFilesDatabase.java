package org.jphototagger.repository.hsqldb;

import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;

import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.search.Join;
import org.jphototagger.domain.metadata.search.Join.Type;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcDescriptionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcTitleMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4XmpCoreDateCreatedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopAuthorspositionMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCaptionwriterMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCreditMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopHeadlineMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopInstructionsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopTransmissionReferenceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.RepositoryStatistics;
import org.jphototagger.domain.repository.SynonymsRepository;
import org.jphototagger.domain.repository.ThumbnailsRepository;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileInsertedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileMovedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.event.ThumbnailUpdatedEvent;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
final class ImageFilesDatabase extends Database {

    static final ImageFilesDatabase INSTANCE = new ImageFilesDatabase();
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final ThumbnailsRepository tnRepo = Lookup.getDefault().lookup(ThumbnailsRepository.class);

    private enum DcSubjectOption {

        INCLUDE_SYNONYMS
    }

    private ImageFilesDatabase() {
    }

    /**
     * Renames an image file.
     *
     * @param  fromImageFile old image file
     * @param  toImageFile   new renamed image file
     * @return               count of renamed files (0 or 1)
     */
    public int updateRenameImageFile(File fromImageFile, File toImageFile) {
        if (fromImageFile == null) {
            throw new NullPointerException("fromImageFile == null");
        }
        if (toImageFile == null) {
            throw new NullPointerException("toImageFile == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("UPDATE files SET filename = ? WHERE filename = ?");
            stmt.setString(1, getFilePath(toImageFile));
            stmt.setString(2, getFilePath(fromImageFile));
            logFiner(stmt);
            count = stmt.executeUpdate();
            tnRepo.renameThumbnail(fromImageFile, toImageFile);
            if (count > 0) {
                notifyImageFileMoved(fromImageFile, toImageFile);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    public List<File> getAllImageFiles() {
        List<File> files = new ArrayList<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT filename FROM files";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return files;
    }

    private long getFileCountNameStartingWith(Connection con, String start) throws SQLException {
        long count = 0;
        String sql = "SELECT COUNT(*) FROM files WHERE filename LIKE ?";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, start + "%");
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException ex) {
            throw ex;
        } finally {
            close(rs, stmt);
        }
        return count;
    }

    /**
     * Renames filenames starting with a substring. Usage: Renaming a directory
     * in the filesystem.
     *
     * @param  before          start substring of the old filenames
     * @param  after           new start substring
     * @param progressListener null or progress listener. The progress listener
     *                         can cancel renaming via
     *                         {@code ProgressEvent#setCancel(boolean)}
     *                         (no rollback).
     * @return                 count of renamed files
     */
    public synchronized int updateRenameFilenamesStartingWith(final String before, final String after, final ProgressListener progressListener) {
        if (before == null) {
            throw new NullPointerException("before == null");
        }
        if (after == null) {
            throw new NullPointerException("after == null");
        }
        if (before.equals(after)) {
            return 0;
        }
        int countRenamed = 0;
        int startLength = before.length();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ProgressEvent progressEvent = new ProgressEvent.Builder().source(this).minimum(0).maximum(0).value(0).build();
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("SELECT filename FROM files WHERE filename LIKE ?");
            stmt.setString(1, before + "%");
            logFinest(stmt);
            rs = stmt.executeQuery();
            progressEvent.setMaximum((int) getFileCountNameStartingWith(con, before));
            boolean cancel = notifyProgressListenerStart(progressListener, progressEvent);
            while (!cancel && rs.next()) {
                String from = rs.getString(1);
                String to = after + from.substring(startLength);
                updateImageFilename(con, getFile(from), getFile(to));
                countRenamed++;
                progressEvent.setValue(countRenamed);
                cancel = notifyProgressListenerPerformed(progressListener, progressEvent);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
            notifyProgressListenerEnd(progressListener, null);
        }
        return countRenamed;
    }

    private void updateImageFilename(Connection con, File fromImageFile, File toImageFile) throws SQLException {
        if (fromImageFile.equals(toImageFile)) {
            return;
        }
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE files SET filename = ? WHERE filename = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, getFilePath(toImageFile));
            stmt.setString(2, getFilePath(fromImageFile));
            logFiner(stmt);
            stmt.executeUpdate();
            notifyImageFileMoved(fromImageFile, toImageFile);
            tnRepo.renameThumbnail(fromImageFile, toImageFile);
        } finally {
            close(stmt);
        }
    }

    private int deleteRowWithFilename(Connection con, File imageFile) {
        int countDeleted = 0;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("DELETE FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFiner(stmt);
            countDeleted = stmt.executeUpdate();
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
        }
        return countDeleted;
    }

    public synchronized boolean insertOrUpdateExif(File imageFile, Exif exif) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (exif == null) {
            throw new NullPointerException("exif == null");
        }
        Connection con = null;
        try {
            con = getConnection();
            long idFile = findIdImageFile(con, imageFile);
            if (idFile < 0) {
                return false;
            }
            insertOrUpdateExif(con, imageFile, idFile, exif);
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            free(con);
        }
        return true;
    }

    public synchronized boolean insertOrUpdateXmpOfImageFile(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            long idFile = findIdImageFile(con, imageFile);
            if (idFile < 0) {
                return false;
            }
            insertOrUpdateXmp(con, imageFile, idFile, xmp);
            setLastModifiedToXmpSidecarFileOfImageFile(imageFile, xmp.contains(XmpLastModifiedMetaDataValue.INSTANCE)
                    ? (Long) xmp.getValue(XmpLastModifiedMetaDataValue.INSTANCE)
                    : -1);
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            free(con);
        }
        return true;
    }

    /**
     * Inserts an image file into the databse. If the image already
     * existsValueInMetaDataValues it's data will be updated.
     * <p>
     * Inserts or updates this metadata:
     *
     * <ul>
     * <li>EXIF when {@code ImageFile#isInsertExifIntoDb()} is true</li>
     * <li>XMP when {@code ImageFile#isInsertXmpIntoDb()} is true</li>
     * <li>Thumbnail when {@code ImageFile#isInsertThumbnailIntoDb()} is true
     * </li>
     * </ul>
     *
     * @param  imageFile image
     * @return           true if inserted
     */
    public synchronized boolean insertOrUpdateImageFile(ImageFile imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        boolean success = false;
        if (existsImageFile(imageFile.getFile())) {
            return updateImageFile(imageFile);
        }
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sqlWithXmpLastModified = "INSERT INTO files (filename, lastmodified, xmp_lastmodified) VALUES (?, ?, ?)";
            String sqlWithoutXmpLastModified = "INSERT INTO files (filename, lastmodified) VALUES (?, ?)";
            stmt = con.prepareStatement(imageFile.isInsertXmpIntoDb()
                    ? sqlWithXmpLastModified
                    : sqlWithoutXmpLastModified);
            File imgFile = imageFile.getFile();
            stmt.setString(1, getFilePath(imgFile));
            stmt.setLong(2, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                stmt.setLong(3, getLastmodifiedXmp(imageFile));
            }
            logFiner(stmt);
            stmt.executeUpdate();
            long idFile = findIdImageFile(con, imgFile);
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(imgFile, imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertXmp(con, imgFile, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertExif(con, imgFile, idFile, imageFile.getExif());
            }
            con.commit();
            success = true;
            notifyImageFileInserted(imgFile);
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return success;
    }

    public ImageFile getImageFileOfFile(File file) {
        if (file == null) {
            throw new NullPointerException("imgFile == null");
        }
        ImageFile imageFile = new ImageFile();
        imageFile.setExif(getExifOfImageFile(file));
        imageFile.setFile(file);
        imageFile.setLastmodified(getImageFilesLastModifiedTimestamp(file));
        Image thumbnail = tnRepo.findThumbnail(file);
        if (thumbnail != null) {
            imageFile.setThumbnail(thumbnail);
        }
        imageFile.setXmp(getXmpOfImageFile(file));
        return imageFile;
    }

    /**
     * Aktualisiert ein Bild in der Datenbank.
     * <p>
     * Updates this metadata:
     *
     * <ul>
     * <li>EXIF when {@code ImageFile#isInsertExifIntoDb()} is true</li>
     * <li>XMP when {@code ImageFile#isInsertXmpIntoDb()} is true</li>
     * <li>Thumbnail when {@code ImageFile#isInsertThumbnailIntoDb()} is true
     * </li>
     * </ul>
     *
     * @param imageFile Bild
     * @return          true bei Erfolg
     */
    public boolean updateImageFile(ImageFile imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        boolean success = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sqlWithXmpLastModified = "UPDATE files SET lastmodified = ?, xmp_lastmodified = ? WHERE id = ?";
            String sqlWithoutXmpLastModified = "UPDATE files SET lastmodified = ? WHERE id = ?";
            stmt = con.prepareStatement(imageFile.isInsertXmpIntoDb()
                    ? sqlWithXmpLastModified
                    : sqlWithoutXmpLastModified);
            File imgFile = imageFile.getFile();
            long idFile = findIdImageFile(con, imgFile);
            stmt.setLong(1, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                stmt.setLong(2, getLastmodifiedXmp(imageFile));
            }
            stmt.setLong(imageFile.isInsertXmpIntoDb()
                    ? 3
                    : 2, idFile);
            logFiner(stmt);
            stmt.executeUpdate();
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(imgFile, imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertOrUpdateXmp(con, imgFile, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertOrUpdateExif(con, imgFile, idFile, imageFile.getExif());
            }
            con.commit();
            success = true;
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return success;
    }

    /**
     * Updates all thumbnails, reads the files from the file system and creates
     * thumbnails from the files.
     *
     * @param  listener progress listener or null, can cancel action via event and
     *                  receives the current filename
     * @return          count of updated thumbnails
     */
    public int updateAllThumbnails(ProgressListener listener) {
        int updated = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        RepositoryStatistics repoStatistics = Lookup.getDefault().lookup(RepositoryStatistics.class);
        try {
            int filecount = repoStatistics.getFileCount();
            ProgressEvent progressEvent = new ProgressEvent.Builder().source(this).minimum(0).maximum(filecount).value(0).info("").build();
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            String sql = "SELECT filename FROM files ORDER BY filename ASC";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            int count = 0;
            notifyProgressListenerStart(listener, progressEvent);
            while (!progressEvent.isCancel() && rs.next()) {
                File imgFile = getFile(rs.getString(1));
                Image thumbnail = ThumbnailCreatorService.INSTANCE.createScaledOrFromEmbeddedThumbnail(imgFile);
                if (thumbnail != null) {
                    updateThumbnailFile(imgFile, thumbnail);
                }
                updated++;
                count++;
                progressEvent.setValue(count);
                progressEvent.setInfo(imgFile);
                notifyProgressListenerPerformed(listener, progressEvent);
            }
            notifyProgressListenerEnd(listener, progressEvent);
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return updated;
    }

    /**
     * Updates the thumbnail of an image file.
     *
     * @param  imageFile image file
     * @param  thumbnail updated thumbnail
     * @return true if updated
     */
    public boolean updateThumbnail(File imageFile, Image thumbnail) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        if (thumbnail == null) {
            throw new NullPointerException("thumbnail == null");
        }
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            updateThumbnailFile(imageFile, thumbnail);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            free(con);
        }
        return false;
    }

    private void updateThumbnailFile(File imageFile, Image thumbnail) {
        if (thumbnail != null) {
            tnRepo.writeThumbnail(thumbnail, imageFile);
            notifyThumbnailUpdated(imageFile);
        }
    }

    /**
     * Returns the last modification time of an image file.
     *
     * @param  imageFile image file
     * @return           time in milliseconds since 1970 or -1 if the file is
     *                   not in the database or when errors occured
     */
    public long getImageFilesLastModifiedTimestamp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        long lastModified = -1;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return lastModified;
    }

    public boolean existsImageFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    public int deleteImageFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("files == null");
        }
        int countDeleted = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("DELETE FROM files WHERE filename = ?");
            for (File imageFile : imageFiles) {
                stmt.setString(1, getFilePath(imageFile));
                logFiner(stmt);
                int countAffectedRows = stmt.executeUpdate();
                countDeleted += countAffectedRows;
                if (countAffectedRows > 0) {
                    Xmp xmp = getXmpOfImageFile(imageFile);
                    Exif exif = getExifOfImageFile(imageFile);
                    tnRepo.deleteThumbnail(imageFile);
                    notifyImageFileDeleted(imageFile);
                    if (xmp != null) {
                        notifyXmpDeleted(imageFile, xmp);
                    }
                    if (exif != null) {
                        notifyExifDeleted(imageFile, exif);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return countDeleted;
    }

    public int deleteAbsentImageFiles(ProgressListener listener) {
        int countDeleted = 0;
        RepositoryStatistics repoStatistics = Lookup.getDefault().lookup(RepositoryStatistics.class);
        ProgressEvent event = new ProgressEvent.Builder().source(this).minimum(0).maximum(repoStatistics.getFileCount()).value(0).build();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            String sql = "SELECT filename FROM files";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            boolean cancel = notifyProgressListenerStart(listener, event);
            while (!cancel && rs.next()) {
                File imgFile = getFile(rs.getString(1));
                event.setInfo(null);
                if (!imgFile.exists()) {
                    Xmp xmp = getXmpOfImageFile(imgFile);
                    Exif exif = getExifOfImageFile(imgFile);
                    int deletedRows = deleteRowWithFilename(con, imgFile);
                    countDeleted += deletedRows;
                    if (deletedRows > 0) {
                        event.setInfo(Bundle.getString(ImageFilesDatabase.class, "ImageFilesDatabase.DeleteAbsentImageFiles.ImageFileDeleted", imgFile));
                        tnRepo.deleteThumbnail(imgFile);
                        notifyImageFileDeleted(imgFile);
                        if (xmp != null) {
                            notifyXmpDeleted(imgFile, xmp);
                        }
                        if (exif != null) {
                            notifyExifDeleted(imgFile, exif);
                        }
                    }
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                cancel = event.isCancel();
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        event.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, event);
        return countDeleted;
    }

    private long findIdXmpOfIdFile(Connection con, long idFile) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id FROM xmp WHERE id_file = ?");
            stmt.setLong(1, idFile);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }
        return id;
    }

    /**
     * Returns the last modification time of the xmp data.
     *
     * @param  imageFile <em>image</em> file (<em>not</em> sidecar file)
     * @return               last modification time in milliseconds since 1970
     *                       or -1 if not defined
     */
    public long getXmpFilesLastModifiedTimestamp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        long lastModified = -1;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT xmp_lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return lastModified;
    }

    /**
     * Sets the last modification time of XMP metadata.
     *
     * @param  imageFile image file
     * @param  time      milliseconds since 1970
     * @return           true if set
     */
    public boolean setLastModifiedToXmpSidecarFileOfImageFile(File imageFile, long time) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        boolean set = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("UPDATE files SET xmp_lastmodified = ? WHERE filename = ?");
            stmt.setLong(1, time);
            stmt.setString(2, getFilePath(imageFile));
            logFiner(stmt);
            int count = stmt.executeUpdate();
            set = count > 0;
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return set;
    }

    private long getLastmodifiedXmp(ImageFile imageFile) {
        Xmp xmp = imageFile.getXmp();
        return (xmp == null)
                ? -1
                : xmp.contains(XmpLastModifiedMetaDataValue.INSTANCE)
                ? (Long) xmp.getValue(XmpLastModifiedMetaDataValue.INSTANCE)
                : -1;
    }

    @SuppressWarnings("unchecked")
    private void insertXmp(Connection con, File imageFile, long idImageFile, Xmp xmp) throws SQLException {
        if ((xmp != null) && !xmp.isEmpty()) {
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(getInsertIntoXmpStatement());
                setXmpValues(stmt, idImageFile, xmp);
                logFiner(stmt);
                stmt.executeUpdate();
                long idXmp = findIdXmpOfIdFile(con, idImageFile);

                if (xmp.contains(XmpDcSubjectsSubjectMetaDataValue.INSTANCE)) {
                    insertXmpDcSubjects(con, idXmp, (List<String>) xmp.getValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE));
                }
                notifyXmpInserted(imageFile, xmp);
            } finally {
                close(stmt);
            }
        }
    }

    private void insertXmpDcSubjects(Connection con, long idXmp, List<String> dcSubjects) throws SQLException {
        for (String dcSubject : dcSubjects) {
            Long idDcSubject = ensureDcSubjectExists(con, dcSubject);
            if (idDcSubject == null) {
                throw new SQLException("Couldn't ensure ID of DC subject!");
            }
            if (!existsXmpDcSubjectsLink(idXmp, idDcSubject)) {
                insertXmpDcSubjectsLink(con, idXmp, idDcSubject);
            }
        }
    }

    private void insertXmpDcSubjectsLink(Connection con, long idXmp, long idDcSubject) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("INSERT INTO xmp_dc_subject (id_xmp, id_dc_subject) VALUES (?, ?)");
            stmt.setLong(1, idXmp);
            stmt.setLong(2, idDcSubject);
            logFiner(stmt);
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    private Long ensureDcSubjectExists(Connection con, String dcSubject) throws SQLException {
        Long idDcSubject = getIdDcSubject(dcSubject);
        if (idDcSubject == null) {
            insertDcSubject(con, dcSubject);
            idDcSubject = getIdDcSubject(dcSubject);
        }
        return idDcSubject;
    }

    private int insertDcSubject(Connection con, String dcSubject) throws SQLException {
        PreparedStatement stmt = null;
        int count = 0;
        try {
            stmt = con.prepareStatement("INSERT INTO dc_subjects (subject) VALUES (?)");
            stmt.setString(1, dcSubject);
            logFiner(stmt);
            count = stmt.executeUpdate();
        } finally {
            close(stmt);
        }
        return count;
    }

    /**
     * Inserts a Dublin core subject.
     * <p>
     * Does <em>not</em> check whether it already existsValueInColumn. In that case an
     * {@code SQLException} will be thrown and caught by this method.
     *
     * @param  dcSubject subject
     * @return           true if inserted
     */
    public boolean insertDcSubject(String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }
        boolean inserted = false;
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            int count = insertDcSubject(con, dcSubject);
            inserted = count == 1;
            if (inserted) {
                notifyDcSubjectInserted(dcSubject);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            free(con);
        }
        return inserted;
    }

    private String getUpdateXmpStatement() {
        return "UPDATE xmp SET id_file = ?" // --  1 --
                + ", id_dc_creator = ?" // --  2 --
                + ", dc_description = ?" // --  3 --
                + ", id_dc_rights = ?" // --  4 --
                + ", dc_title = ?" // --  5 --
                + ", id_iptc4xmpcore_location = ?" // --  6 --
                + ", id_photoshop_authorsposition = ?" // --  7 --
                + ", id_photoshop_captionwriter = ?" // --  8 --
                + ", id_photoshop_city = ?" // --  9 --
                + ", id_photoshop_country = ?" // -- 10 --
                + ", id_photoshop_credit = ?" // -- 11 --
                + ", photoshop_headline = ?" // -- 12 --
                + ", photoshop_instructions = ?" // -- 13 --
                + ", id_photoshop_source = ?" // -- 14 --
                + ", id_photoshop_state = ?" // -- 15 --
                + ", photoshop_transmissionReference = ?" // -- 16 --
                + ", rating = ?" // -- 17 --
                + ", iptc4xmpcore_datecreated = ?" // -- 18 --
                + " WHERE id = ?";    // -- 19 --
    }

    private String getInsertIntoXmpStatement() {
        return "INSERT INTO xmp (id_file" // --  1 --
                + ", id_dc_creator" // --  2 --
                + ", dc_description" // --  3 --
                + ", id_dc_rights" // --  4 --
                + ", dc_title" // --  5 --
                + ", id_iptc4xmpcore_location" // --  6 --
                + ", id_photoshop_authorsposition" // --  7 --
                + ", id_photoshop_captionwriter" // --  8 --
                + ", id_photoshop_city" // --  9 --
                + ", id_photoshop_country" // -- 10 --
                + ", id_photoshop_credit" // -- 11 --
                + ", photoshop_headline" // -- 12 --
                + ", photoshop_instructions" // -- 13 --
                + ", id_photoshop_source" // -- 14 --
                + ", id_photoshop_state" // -- 15 --
                + ", photoshop_transmissionReference" // -- 16 --
                + ", rating" // -- 17 --
                + ", iptc4xmpcore_datecreated" // -- 18 --
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private void setXmpValues(PreparedStatement stmt, long idImageFile, Xmp xmp) throws SQLException {
        stmt.setLong(1, idImageFile);
        setLong(ensureValueExists("dc_creators", "creator", (String) xmp.getValue(XmpDcCreatorMetaDataValue.INSTANCE)), stmt,
                2);
        setString(xmp.getValue(XmpDcDescriptionMetaDataValue.INSTANCE), stmt, 3);
        setLong(ensureValueExists("dc_rights", "rights", (String) xmp.getValue(XmpDcRightsMetaDataValue.INSTANCE)), stmt, 4);
        setString(xmp.getValue(XmpDcTitleMetaDataValue.INSTANCE), stmt, 5);
        setLong(ensureValueExists("iptc4xmpcore_locations", "location",
                (String) xmp.getValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE)), stmt, 6);
        setLong(ensureValueExists("photoshop_authorspositions", "authorsposition",
                (String) xmp.getValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE)), stmt, 7);
        setLong(ensureValueExists("photoshop_captionwriters", "captionwriter",
                (String) xmp.getValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE)), stmt, 8);
        setLong(ensureValueExists("photoshop_cities", "city", (String) xmp.getValue(XmpPhotoshopCityMetaDataValue.INSTANCE)),
                stmt, 9);
        setLong(ensureValueExists("photoshop_countries", "country",
                (String) xmp.getValue(XmpPhotoshopCountryMetaDataValue.INSTANCE)), stmt, 10);
        setLong(ensureValueExists("photoshop_credits", "credit",
                (String) xmp.getValue(XmpPhotoshopCreditMetaDataValue.INSTANCE)), stmt, 11);
        setString(xmp.getValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE), stmt, 12);
        setString(xmp.getValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE), stmt, 13);
        setLong(ensureValueExists("photoshop_sources", "source",
                (String) xmp.getValue(XmpPhotoshopSourceMetaDataValue.INSTANCE)), stmt, 14);
        setLong(ensureValueExists("photoshop_states", "state",
                (String) xmp.getValue(XmpPhotoshopStateMetaDataValue.INSTANCE)), stmt, 15);
        setString(xmp.getValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE), stmt, 16);
        setLongMinMax(xmp.getValue(XmpRatingMetaDataValue.INSTANCE), XmpRatingMetaDataValue.getMinValue(),
                XmpRatingMetaDataValue.getMaxValue(), stmt, 17);
        setString(xmp.getValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE), stmt, 18);
    }

    @SuppressWarnings("unchecked")
    private void insertOrUpdateXmp(Connection con, File imageFile, long idFile, Xmp xmp) throws SQLException {
        if (xmp != null) {
            long idXmp = findIdXmpOfIdFile(con, idFile);
            if (idXmp > 0) {
                PreparedStatement stmt = null;
                try {
                    Xmp oldXmp = getXmpOfImageFile(imageFile);
                    stmt = con.prepareStatement(getUpdateXmpStatement());
                    setXmpValues(stmt, idFile, xmp);
                    stmt.setLong(19, idXmp);
                    logFiner(stmt);
                    stmt.executeUpdate();
                    deleteXmpDcSubjects(con, idXmp);
                    if (xmp.contains(XmpDcSubjectsSubjectMetaDataValue.INSTANCE)) {
                        insertXmpDcSubjects(con, idXmp,
                                (List<String>) xmp.getValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE));
                    }
                    notifyXmpUpdated(imageFile, oldXmp, xmp);
                } finally {
                    close(stmt);
                }
            } else {
                insertXmp(con, imageFile, idFile, xmp);
            }
        }
    }

    String findXmpIptc4CoreDateCreated(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT xmp.iptc4xmpcore_datecreated FROM files INNER JOIN xmp ON files.id = xmp.id_file WHERE files.filename = ?";
        String result = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, getFilePath(file));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                result = rs.getString(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return result;
    }

    boolean existsXmpForFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) FROM files INNER JOIN xmp ON files.id = xmp.id_file WHERE files.filename = ?";
        long count = 0;
        try {
            con = getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, getFilePath(file));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count > 0;
    }

    int deleteXmpOfFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        Connection con = null;
        int countDeleted = 0;
        try {
            con = getConnection();
            countDeleted = deleteXmpOfImageFile(con, file);
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            free(con);
        }
        return countDeleted;
    }

    private void deleteXmpDcSubjects(Connection con, long idXmp) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("DELETE FROM xmp_dc_subject WHERE id_xmp = ?");
            stmt.setLong(1, idXmp);
            logFiner(stmt);
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    public int deleteAbsentXmp(ProgressListener listener) {
        int countDeleted = 0;
        RepositoryStatistics repoStatistics = Lookup.getDefault().lookup(RepositoryStatistics.class);
        ProgressEvent progressEvent = new ProgressEvent.Builder().source(this).minimum(0).maximum(repoStatistics.getXmpCount()).value(0).build();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();
            String sql = "SELECT files.filename FROM files, xmp WHERE files.id = xmp.id_file";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            File imageFile;
            boolean cancel = notifyProgressListenerStart(listener, progressEvent);
            while (!cancel && rs.next()) {
                progressEvent.setInfo(null);
                imageFile = getFile(rs.getString(1));
                if (xmpSidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(imageFile) == null) {
                    int deleteCount = deleteXmpOfImageFile(con, imageFile);
                    countDeleted += deleteCount;
                    progressEvent.setInfo(deleteCount > 0
                            ? Bundle.getString(ImageFilesDatabase.class, "ImageFilesDatabase.Info.DeleteAbsentXmp.XmpMetadataDeleted", imageFile)
                            : null);
                }
                progressEvent.setValue(progressEvent.getValue() + 1);
                notifyProgressListenerPerformed(listener, progressEvent);
                cancel = progressEvent.isCancel();
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        progressEvent.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, progressEvent);
        return countDeleted;
    }

    private int deleteXmpOfImageFile(Connection con, File imageFile) {
        int count = 0;
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("DELETE FROM xmp WHERE xmp.id_file in"
                    + " (SELECT xmp.id_file FROM xmp, files"
                    + " WHERE xmp.id_file = files.id AND files.filename = ?)");
            stmt.setString(1, getFilePath(imageFile));
            logFiner(stmt);
            Xmp xmp = getXmpOfImageFile(imageFile);
            count = stmt.executeUpdate();
            if (count > 0) {
                EventBus.publish(new XmpDeletedEvent(this, imageFile, xmp));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
        }
        return count;
    }

    private String getXmpOfImageFilesStatement(int fileCount) {
        return " SELECT files.filename" // --  1 --
                + ", dc_creators.creator" // --  2 --
                + ", xmp.dc_description" // --  3 --
                + ", dc_rights.rights" // --  4 --
                + ", xmp.dc_title" // --  5 --
                + ", iptc4xmpcore_locations.location" // --  6  --
                + ", photoshop_authorspositions.authorsposition" // --  7 --
                + ", photoshop_captionwriters.captionwriter" // --  8 --
                + ", photoshop_cities.city" // --  9 --
                + ", photoshop_countries.country" // -- 10 --
                + ", photoshop_credits.credit" // -- 11 --
                + ", xmp.photoshop_headline" // -- 12 --
                + ", xmp.photoshop_instructions" // -- 13 --
                + ", photoshop_sources.source" // -- 14 --
                + ", photoshop_states.state" // -- 15 --
                + ", xmp.photoshop_transmissionReference" // -- 16 --
                + ", dc_subjects.subject" // -- 17 --
                + ", xmp.rating" // -- 18 --
                + ", xmp.iptc4xmpcore_datecreated" // -- 19 --
                + " FROM files LEFT JOIN xmp ON files.id = xmp.id_file"
                + " LEFT JOIN dc_creators ON xmp.id_dc_creator = dc_creators.id"
                + " LEFT JOIN dc_rights ON xmp.id_dc_rights = dc_rights.id"
                + " LEFT JOIN iptc4xmpcore_locations"
                + " ON xmp.id_iptc4xmpcore_location = iptc4xmpcore_locations.id"
                + " LEFT JOIN photoshop_authorspositions"
                + " ON xmp.id_photoshop_authorsposition"
                + " = photoshop_authorspositions.id"
                + " LEFT JOIN photoshop_captionwriters"
                + " ON xmp.id_photoshop_captionwriter"
                + " = photoshop_captionwriters.id LEFT JOIN photoshop_cities"
                + " ON xmp.id_photoshop_city = photoshop_cities.id"
                + " LEFT JOIN photoshop_countries"
                + " ON xmp.id_photoshop_country = photoshop_countries.id"
                + " LEFT JOIN photoshop_credits"
                + " ON xmp.id_photoshop_credit = photoshop_credits.id"
                + " LEFT JOIN photoshop_sources"
                + " ON xmp.id_photoshop_source = photoshop_sources.id"
                + " LEFT JOIN photoshop_states"
                + " ON xmp.id_photoshop_state = photoshop_states.id"
                + " LEFT JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
                + " LEFT JOIN dc_subjects"
                + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id"
                + " WHERE files.filename IN ("
                + getPlaceholder(fileCount)
                + ")";
    }

    /**
     * Returns XMP metadata of image files.
     *
     * @param imageFiles image files
     * @return           XMP metadata where {@code FileXmp#getFile()} is the
     *                   image file and {@code FileXmp#getXmp()} the XMP metadata of that
     *                   image file
     */
    public List<FileXmp> getXmpOfImageFiles(Collection<? extends File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        List<FileXmp> list = new ArrayList<FileXmp>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = getXmpOfImageFilesStatement(imageFiles.size());
            stmt = con.prepareStatement(sql);
            setStrings(stmt, imageFiles.toArray(new File[0]), 1);
            logFinest(stmt);
            rs = stmt.executeQuery();
            String prevFilepath = "";
            Xmp xmp = new Xmp();
            while (rs.next()) {
                String filepath = rs.getString(1);
                if (!filepath.equals(prevFilepath)) {
                    xmp = new Xmp();
                }
                xmp.setValue(XmpDcCreatorMetaDataValue.INSTANCE, getString(rs, 2));
                xmp.setValue(XmpDcDescriptionMetaDataValue.INSTANCE, getString(rs, 3));
                xmp.setValue(XmpDcRightsMetaDataValue.INSTANCE, getString(rs, 4));
                xmp.setValue(XmpDcTitleMetaDataValue.INSTANCE, getString(rs, 5));
                xmp.setValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, getString(rs, 6));
                xmp.setValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, getString(rs, 7));
                xmp.setValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, getString(rs, 8));
                xmp.setValue(XmpPhotoshopCityMetaDataValue.INSTANCE, getString(rs, 9));
                xmp.setValue(XmpPhotoshopCountryMetaDataValue.INSTANCE, getString(rs, 10));
                xmp.setValue(XmpPhotoshopCreditMetaDataValue.INSTANCE, getString(rs, 11));
                xmp.setValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, getString(rs, 12));
                xmp.setValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, getString(rs, 13));
                xmp.setValue(XmpPhotoshopSourceMetaDataValue.INSTANCE, getString(rs, 14));
                xmp.setValue(XmpPhotoshopStateMetaDataValue.INSTANCE, getString(rs, 15));
                xmp.setValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, getString(rs, 16));
                String dcSubject = getString(rs, 17);
                if (dcSubject != null) {
                    xmp.setValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, dcSubject);
                }
                xmp.setValue(XmpRatingMetaDataValue.INSTANCE,
                        getLongMinMax(rs, 18, XmpRatingMetaDataValue.getMinValue(), XmpRatingMetaDataValue.getMaxValue()));
                xmp.setValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, getString(rs, 19));
                if (!filepath.equals(prevFilepath)) {
                    File file = getFile(filepath);
                    list.add(new FileXmp(file, xmp));
                }
                prevFilepath = filepath;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return list;
    }

    private void setStrings(PreparedStatement stmt, File[] files, int startIndex) throws SQLException {
        int endIndex = startIndex + files.length;
        int stringIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            stmt.setString(i, getFilePath(files[stringIndex]));
            stringIndex++;
        }
    }

    private static String getPlaceholder(int count) {
        StringBuilder sb = new StringBuilder(count * 3 - 2);    // 3: ", ?"
        for (int i = 0; i < count; i++) {
            sb.append(((i > 0) && (i < count))
                    ? ", ?"
                    : "?");
        }
        return sb.toString();
    }

    private String getXmpOfStatement() {
        return " SELECT dc_creators.creator" // --  1 --
                + ", xmp.dc_description" // --  2 --
                + ", dc_rights.rights" // --  3 --
                + ", xmp.dc_title" // --  4 --
                + ", iptc4xmpcore_locations.location" // --  5  --
                + ", photoshop_authorspositions.authorsposition" // --  6 --
                + ", photoshop_captionwriters.captionwriter" // --  7 --
                + ", photoshop_cities.city" // --  8 --
                + ", photoshop_countries.country" // --  9 --
                + ", photoshop_credits.credit" // -- 10 --
                + ", xmp.photoshop_headline" // -- 11 --
                + ", xmp.photoshop_instructions" // -- 12 --
                + ", photoshop_sources.source" // -- 13 --
                + ", photoshop_states.state" // -- 14 --
                + ", xmp.photoshop_transmissionReference" // -- 15 --
                + ", dc_subjects.subject" // -- 16 --
                + ", xmp.rating" // -- 17 --
                + ", xmp.iptc4xmpcore_datecreated" // -- 18 --
                + " FROM files INNER JOIN xmp ON files.id = xmp.id_file"
                + " LEFT JOIN dc_creators ON xmp.id_dc_creator = dc_creators.id"
                + " LEFT JOIN dc_rights ON xmp.id_dc_rights = dc_rights.id"
                + " LEFT JOIN iptc4xmpcore_locations"
                + " ON xmp.id_iptc4xmpcore_location = iptc4xmpcore_locations.id"
                + " LEFT JOIN photoshop_authorspositions"
                + " ON xmp.id_photoshop_authorsposition"
                + " = photoshop_authorspositions.id"
                + " LEFT JOIN photoshop_captionwriters"
                + " ON xmp.id_photoshop_captionwriter"
                + " = photoshop_captionwriters.id LEFT JOIN photoshop_cities"
                + " ON xmp.id_photoshop_city = photoshop_cities.id"
                + " LEFT JOIN photoshop_countries"
                + " ON xmp.id_photoshop_country = photoshop_countries.id"
                + " LEFT JOIN photoshop_credits"
                + " ON xmp.id_photoshop_credit = photoshop_credits.id"
                + " LEFT JOIN photoshop_sources"
                + " ON xmp.id_photoshop_source = photoshop_sources.id"
                + " LEFT JOIN photoshop_states"
                + " ON xmp.id_photoshop_state = photoshop_states.id"
                + " LEFT JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
                + " LEFT JOIN dc_subjects"
                + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id"
                + " WHERE files.filename = ?";
    }

    public Xmp getXmpOfImageFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        Xmp xmp = new Xmp();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getXmpOfStatement());
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                xmp.setValue(XmpDcCreatorMetaDataValue.INSTANCE, getString(rs, 1));
                xmp.setValue(XmpDcDescriptionMetaDataValue.INSTANCE, getString(rs, 2));
                xmp.setValue(XmpDcRightsMetaDataValue.INSTANCE, getString(rs, 3));
                xmp.setValue(XmpDcTitleMetaDataValue.INSTANCE, getString(rs, 4));
                xmp.setValue(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE, getString(rs, 5));
                xmp.setValue(XmpPhotoshopAuthorspositionMetaDataValue.INSTANCE, getString(rs, 6));
                xmp.setValue(XmpPhotoshopCaptionwriterMetaDataValue.INSTANCE, getString(rs, 7));
                xmp.setValue(XmpPhotoshopCityMetaDataValue.INSTANCE, getString(rs, 8));
                xmp.setValue(XmpPhotoshopCountryMetaDataValue.INSTANCE, getString(rs, 9));
                xmp.setValue(XmpPhotoshopCreditMetaDataValue.INSTANCE, getString(rs, 10));
                xmp.setValue(XmpPhotoshopHeadlineMetaDataValue.INSTANCE, getString(rs, 11));
                xmp.setValue(XmpPhotoshopInstructionsMetaDataValue.INSTANCE, getString(rs, 12));
                xmp.setValue(XmpPhotoshopSourceMetaDataValue.INSTANCE, getString(rs, 13));
                xmp.setValue(XmpPhotoshopStateMetaDataValue.INSTANCE, getString(rs, 14));
                xmp.setValue(XmpPhotoshopTransmissionReferenceMetaDataValue.INSTANCE, getString(rs, 15));
                String dcSubject = getString(rs, 16);
                if (dcSubject != null) {
                    xmp.setValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, dcSubject);
                }
                xmp.setValue(XmpRatingMetaDataValue.INSTANCE,
                        getLongMinMax(rs, 17, XmpRatingMetaDataValue.getMinValue(), XmpRatingMetaDataValue.getMaxValue()));
                xmp.setValue(XmpIptc4XmpCoreDateCreatedMetaDataValue.INSTANCE, getString(rs, 18));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return xmp;
    }

    /**
     * Returns keywords not referenced by any file.
     *
     * @return keywords or empty set
     */
    public Set<String> getNotReferencedDcSubjects() {
        Set<String> dcSubjects = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT subject FROM dc_subjects WHERE ID NOT in"
                    + " (SELECT DISTINCT id_dc_subject from xmp_dc_subject)"
                    + "ORDER BY 1";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return dcSubjects;
    }

    /**
     * Returns whether at least one subject is referenced by a XMP metadata.
     *
     * @param  subject subject
     * @return         true if that subject is referenced
     */
    public boolean isDcSubjectReferenced(String subject) {
        if (subject == null) {
            throw new NullPointerException("subject == null");
        }
        Connection con = null;
        boolean ref = false;
        try {
            con = getConnection();
            Long id = getId(con, "dc_subjects", "subject", subject);
            if (id != null) {
                ref = getCount(con, "xmp_dc_subject", "id_dc_subject", id) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            free(con);
        }
        return ref;
    }

    public Set<String> getAllDcSubjects() {
        Set<String> dcSubjects = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT subject FROM dc_subjects ORDER BY 1 ASC";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return dcSubjects;
    }

    public List<String> getDcSubjectsOfImageFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        List<String> dcSubjects = new ArrayList<String>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT DISTINCT dc_subjects.subject FROM"
                    + " files INNER JOIN xmp ON files.id = xmp.id_file"
                    + " INNER JOIN xmp_dc_subject"
                    + " ON xmp.id = xmp_dc_subject.id_xmp"
                    + " INNER JOIN dc_subjects"
                    + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id"
                    + " WHERE files.filename = ? "
                    + " ORDER BY dc_subjects.subject ASC";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return dcSubjects;
    }

    public Set<File> getImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }
        Set<File> imageFiles = new LinkedHashSet<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Set<DcSubjectOption> opts = includeSynonyms
                ? EnumSet.<DcSubjectOption>copyOf(Arrays.asList(DcSubjectOption.INCLUDE_SYNONYMS))
                : EnumSet.noneOf(DcSubjectOption.class);
        try {
            con = getConnection();
            stmt = con.prepareStatement(getGetFilenamesOfDcSubjectSql(dcSubject, opts));
            setDcSubjectSynonyms(dcSubject, opts, stmt);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    private void setDcSubjectSynonyms(String dcSubject, Set<DcSubjectOption> options, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, dcSubject);
        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            int paramIndex = 2;
            SynonymsRepository synonymsRepo = Lookup.getDefault().lookup(SynonymsRepository.class);
            for (String synonym : synonymsRepo.findSynonymsOfWord(dcSubject)) {
                stmt.setString(paramIndex, synonym);
                paramIndex++;
            }
        }
    }

    private String getGetFilenamesOfDcSubjectSql(String dcSubject, Set<DcSubjectOption> options) {
        StringBuilder sql = new StringBuilder(" SELECT DISTINCT files.filename FROM"
                + " dc_subjects INNER JOIN xmp_dc_subject"
                + " ON dc_subjects.id = xmp_dc_subject.id_dc_subject"
                + " INNER JOIN xmp ON xmp_dc_subject.id_xmp = xmp.id"
                + " INNER JOIN files ON xmp.id_file = files.id"
                + " WHERE dc_subjects.subject = ?");
        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            SynonymsRepository synonymsRepo = Lookup.getDefault().lookup(SynonymsRepository.class);
            int size = synonymsRepo.findSynonymsOfWord(dcSubject).size();
            for (int i = 0; i < size; i++) {
                sql.append(" OR dc_subjects.subject = ?");
            }
        }
        return sql.toString();
    }

    /**
     * Returns all images files which have all subjects of a list.
     *
     * E.g. If You are searching for an image with a tree AND a cloud AND
     * a car the list contains these three words.
     *
     * Because it's faster, call
     * {@code #getImageFilesContainingDcSubject(String, DcSubjectOption[])}
     * if You are searching for only one subject.
     *
     * @param  dcSubjects subjects
     * @return            images containing all of these subjects
     */
    public Set<File> getImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects) {
        if (dcSubjects == null) {
            throw new NullPointerException("dcSubjects == null");
        }
        Set<File> imageFiles = new LinkedHashSet<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            int count = dcSubjects.size();
            String sql = " SELECT files.filename FROM"
                    + " dc_subjects INNER JOIN xmp_dc_subject"
                    + " ON dc_subjects.id = xmp_dc_subject.id_dc_subject"
                    + " INNER JOIN xmp ON xmp_dc_subject.id_xmp = xmp.id"
                    + " INNER JOIN files ON xmp.id_file = files.id"
                    + " WHERE dc_subjects.subject IN "
                    + getParamsInParentheses(count)
                    + " GROUP BY files.filename HAVING COUNT(*) = "
                    + count;
            stmt = con.prepareStatement(sql);
            setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    public Set<File> getImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects) {
        if (dcSubjects == null) {
            throw new NullPointerException("dcSubjects == null");
        }
        Set<File> imageFiles = new LinkedHashSet<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            int count = dcSubjects.size();
            String sql = " SELECT DISTINCT files.filename FROM dc_subjects"
                    + " INNER JOIN xmp_dc_subject ON dc_subjects.id"
                    + " = xmp_dc_subject.id_dc_subject INNER JOIN xmp"
                    + " ON xmp_dc_subject.id_xmp = xmp.id INNER JOIN files"
                    + " ON xmp.id_file = files.id WHERE dc_subjects.subject IN "
                    + getParamsInParentheses(count);
            stmt = con.prepareStatement(sql);
            setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    /**
     * Returns all images which have all words of a list in a column.
     *
     * E.g. If You are searching for an image with a tree AND a cloud AND
     * a car the list contains these three words.
     *
     * @param  words  search words
     * @param  column column to search. The table of that column has to be
     *                joinable with table <code>"files"</code>
     *                through a column <code>id_file</code>!
     * @return        images containing all of these terms in that column
     */
    public Set<File> getImageFilesContainingAllWordsInColumn(List<? extends String> words, MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        if (words == null) {
            throw new NullPointerException("words == null");
        }
        Set<File> imageFiles = new LinkedHashSet<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String tableName = column.getCategory();
            String columnName = column.getValueName();
            int count = words.size();
            String sql = " SELECT files.filename FROM files"
                    + Join.getJoinToFiles(tableName, Type.INNER)
                    + " WHERE "
                    + tableName + "." + columnName
                    + " IN " + getParamsInParentheses(count)
                    + " GROUP BY files.filename"
                    + " HAVING COUNT(*) = " + count;
            stmt = con.prepareStatement(sql);
            setStringParams(stmt, words, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    private String getUpdateExifStatement() {
        return "UPDATE exif SET id_file = ?" // -- 1 --
                + ", id_exif_recording_equipment = ?" // -- 2 --
                + ", exif_date_time_original = ?" // -- 3 --
                + ", exif_focal_length = ?" // -- 4 --
                + ", exif_iso_speed_ratings = ?" // -- 5 --
                + ", id_exif_lens = ?" // -- 6 --
                + ", exif_date_time_original_timestamp = ?" // -- 7 --
                + " WHERE id_file = ?";    // -- 8 --
    }

    private void insertOrUpdateExif(Connection con, File imageFile, long idFile, Exif exif) throws SQLException {
        if (exif != null) {
            long idExif = findIdExifOfIdFile(con, idFile);
            if (idExif > 0) {
                PreparedStatement stmt = null;
                try {
                    Exif oldExif = getExifOfImageFile(imageFile);
                    stmt = con.prepareStatement(getUpdateExifStatement());
                    setExifValues(stmt, idFile, exif);
                    stmt.setLong(8, idFile);
                    logFiner(stmt);
                    int count = stmt.executeUpdate();
                    if (count > 0) {
                        notifyExifUpdated(imageFile, oldExif, exif);
                    }
                } finally {
                    close(stmt);
                }
            } else {
                insertExif(con, imageFile, idFile, exif);
            }
        }
    }

    private String getInsertIntoExifStatement() {
        return "INSERT INTO exif (id_file" // -- 1 --
                + ", id_exif_recording_equipment" // -- 2 --
                + ", exif_date_time_original" // -- 3 --
                + ", exif_focal_length" // -- 4 --
                + ", exif_iso_speed_ratings" // -- 5 --
                + ", id_exif_lens" // -- 6 --
                + ", exif_date_time_original_timestamp" // -- 7 --
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    private void insertExif(Connection con, File imageFile, long idFile, Exif exif) throws SQLException {
        if ((exif != null) && !exif.isEmpty()) {
            PreparedStatement stmt = null;
            try {
                stmt = con.prepareStatement(getInsertIntoExifStatement());
                setExifValues(stmt, idFile, exif);
                logFiner(stmt);
                stmt.executeUpdate();
                notifyExifInserted(imageFile, exif);
            } finally {
                close(stmt);
            }
        }
    }

    private void setExifValues(PreparedStatement stmt, long idFile, Exif exif) throws SQLException {
        stmt.setLong(1, idFile);
        setLong(ensureValueExists("exif_recording_equipment", "equipment", exif.getRecordingEquipment()), stmt, 2);
        setDate(exif.getDateTimeOriginal(), stmt, 3);
        setDouble(exif.getFocalLengthGreaterZeroOrNull(), stmt, 4);
        setShort(exif.getIsoSpeedRatings(), stmt, 5);
        setLong(ensureValueExists("exif_lenses", "lens", exif.getLens()), stmt, 6);
        setLong(exif.getDateTimeOriginalTimestamp(), stmt, 7);
    }

    private long findIdExifOfIdFile(Connection con, long idFile) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id FROM exif WHERE id_file = ?");
            stmt.setLong(1, idFile);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }
        return id;
    }

    /**
     * Returns the timeline of images where EXIF metadata date time original
     * is defined.
     *
     * @return timeline
     */
    public Timeline getTimeline() {
        Timeline timeline = new Timeline();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT exif_date_time_original FROM exif"
                    + " WHERE exif_date_time_original IS NOT NULL"
                    + " ORDER BY exif_date_time_original ASC";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs.getDate(1));
                timeline.add(cal);
            }
            addXmpDateCreatedToTimeline(con, timeline);
            timeline.addUnknownNode();
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return timeline;
    }

    private void addXmpDateCreatedToTimeline(Connection con, Timeline timeline) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT iptc4xmpcore_datecreated FROM xmp WHERE iptc4xmpcore_datecreated IS NOT NULL";
        try {
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Timeline.Date date = new Timeline.Date(-1, -1, -1);

                date.setXmpDateCreated(rs.getString(1));

                if (date.isValid()) {
                    timeline.add(date);
                }
            }
        } finally {
            close(rs, stmt);
        }
    }

    /**
     * Returns image files taken at a specific date.
     *
     * @param  year  year of the date
     * @param  month month of the date, equals or less zero if all images of
     *               that year should be returned
     * @param  day   day of the date, equals or less zero if all images of that
     *               month should be returned
     * @return       image files taken on that date
     */
    public Set<File> getImageFilesOfDateTaken(int year, int month, int day) {
        Set<File> files = new HashSet<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT files.filename FROM exif LEFT JOIN files"
                    + " ON exif.id_file = files.id"
                    + " WHERE exif.exif_date_time_original LIKE ?"
                    + " UNION SELECT files.filename"
                    + " FROM xmp LEFT JOIN files"
                    + " ON xmp.id_file = files.id"
                    + " WHERE xmp.iptc4xmpcore_datecreated LIKE ?"
                    + " ORDER BY files.filename ASC";
            stmt = con.prepareStatement(sql);
            String exifDateString = getExifSqlDateString(year, month, day);
            String xmpDateString = getXmpSqlDateString(year, month, day);
            stmt.setString(1, exifDateString);
            stmt.setString(2, xmpDateString);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return files;
    }

    // All EXIF dates considered as complete (having year and month and day)
    private String getExifSqlDateString(int year, int month, int day) {
        StringBuilder sb = new StringBuilder(String.valueOf(year));
        if (month > 0) {
            sb.append("-");
            sb.append(getMonthDayPrefix(month));
            sb.append(String.valueOf(month));
        } else {
            sb.append("-%");
        }
        if (day > 0) {
            sb.append("-");
            sb.append(getMonthDayPrefix(day));
            sb.append(String.valueOf(day));
        } else {
            sb.append("-%");
        }
        return sb.toString();
    }

    // A XMP date can have only a year, or a year and month or year and month and day
    // Ignoring queries: All specific months ("All Januaries" ignoring year) and all
    // specific days ("All 15ths" igoring year and/or month)
    private String getXmpSqlDateString(int year, int month, int day) {
        StringBuilder sb = new StringBuilder(String.valueOf(year));
        boolean hasMonth = month > 0;
        boolean hasDay = day > 0;
        boolean hasOnlyYear = !hasMonth && !hasDay;
        if (hasOnlyYear) {
            sb.append("%");
            return sb.toString();
        }
        if (hasMonth) {
            sb.append("-");
            sb.append(getMonthDayPrefix(month));
            sb.append(String.valueOf(month));
        }
        if (!hasDay) {
            sb.append("%");
            return sb.toString();
        }
        sb.append("-");
        sb.append(getMonthDayPrefix(day));
        sb.append(String.valueOf(day));
        return sb.toString();
    }

    private static String getMonthDayPrefix(int i) {
        return (i >= 10)
                ? ""
                : "0";
    }

    public List<File> getImageFilesOfUnknownDateTaken() {
        List<File> files = new ArrayList<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT files.filename FROM files LEFT JOIN exif"
                    + " ON files.id = exif.id_file"
                    + " LEFT JOIN xmp ON files.id = xmp.id_file"
                    + " WHERE (exif.id IS NOT NULL AND exif.exif_date_time_original IS NULL)"
                    + " AND (xmp.id IS NOT NULL AND xmp.iptc4xmpcore_datecreated IS NULL)"
                    + " ORDER BY files.filename ASC";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return files;
    }

    public Set<String> getAllDistinctValuesOfColumn(MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        Set<String> values = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT DISTINCT " + column.getValueName()
                    + " FROM " + column.getCategory()
                    + " WHERE " + column.getValueName() + " IS NOT NULL"
                    + " ORDER BY " + column.getValueName();
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return values;
    }

    private String getFilesNotNullInSql(String tablename, String columnName) {
        boolean isLink = !tablename.equals("xmp") && !tablename.equals("exif");
        return isLink
                ? Join.getNotNullSqlOf(tablename)
                : "SELECT DISTINCT files.filename FROM " + tablename
                + " INNER JOIN files ON " + tablename
                + ".id_file = files.id"
                + " WHERE " + tablename + "." + columnName + " IS NOT NULL"
                + " ORDER BY files.filename ASC";
    }

    public List<File> getImageFilesContainingAVauleInColumn(MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        List<File> files = new ArrayList<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String tablename = column.getCategory();
            String columnName = column.getValueName();
            String sql = getFilesNotNullInSql(tablename, columnName);
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return files;
    }

    /**
     * Returns files where:
     *
     * <ul>
     * <li>a record has an exact value in a specific column</li>
     * <li>the column's table has a foreign key that references the
     *     file's table</li>
     * <li>the column name of the foreign key column has the name
     *     <code>id_file</code></li>
     * </ul>
     *
     * This method is also unusable for one to many references (columns which
     * are foreign keys).
     *
     * @param  column     column whith the value
     * @param  exactValue exact value of the column content
     * @return            files
     */
    public List<File> getImageFilesWhereColumnHasExactValue(MetaDataValue column, String exactValue) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        List<File> files = new ArrayList<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String tableName = column.getCategory();
        String columnName = column.getValueName();
        try {
            con = getConnection();
            String sql = "SELECT files.filename FROM files" + Join.getJoinToFiles(tableName, Type.INNER)
                    + " WHERE " + tableName + "." + columnName + " = ?"
                    + " ORDER BY files.filename ASC";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, exactValue);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return files;
    }

    long findExifDateTimeOriginalTimestamp(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        long timestamp = -1;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT exif.exif_date_time_original_timestamp"
                    + " FROM files INNER JOIN exif ON files.id = exif.id_file"
                    + " WHERE files.filename = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, getFilePath(file));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                timestamp = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return timestamp;
    }

    private String getExifOfStatement() {
        return "SELECT exif_recording_equipment.equipment" // -- 1 --
                + ", exif.exif_date_time_original" // -- 2 --
                + ", exif.exif_focal_length" // -- 3 --
                + ", exif.exif_iso_speed_ratings" // -- 4 --
                + ", exif_lenses.lens" // -- 5 --
                + ", exif_date_time_original_timestamp" // -- 6 --
                + " FROM files INNER JOIN exif ON files.id = exif.id_file"
                + " LEFT JOIN exif_recording_equipment ON"
                + " exif.id_exif_recording_equipment"
                + " = exif_recording_equipment.id LEFT JOIN exif_lenses"
                + " ON exif.id_exif_lens = exif_lenses.id"
                + " WHERE files.filename = ?";
    }

    public Exif getExifOfImageFile(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }
        Exif exif = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getExifOfStatement());
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exif = new Exif();
                exif.setRecordingEquipment(rs.getString(1));
                exif.setDateTimeOriginal(rs.getDate(2));
                exif.setFocalLength(rs.getDouble(3));
                exif.setIsoSpeedRatings(rs.getShort(4));
                exif.setLens(rs.getString(5));
                exif.setDateTimeOriginalTimestamp(rs.getLong(6));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exif;
    }

    public boolean existsExifDate(java.sql.Date date) {
        if (date == null) {
            throw new NullPointerException("date == null");
        }
        boolean exists = false;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String sql = "SELECT COUNT(*) FROM exif"
                    + " WHERE exif_date_time_original LIKE '"
                    + year + "-" + getMonthDayPrefix(month) + month + "-" + getMonthDayPrefix(day) + day + "%'";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    public boolean existsXMPDateCreated(String date) {
        if (date == null) {
            throw new NullPointerException("date == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM xmp WHERE iptc4xmpcore_datecreated = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, date);
            logFinest(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    public boolean existsValueInColumn(Object value, MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM " + column.getCategory() + " WHERE " + column.getValueName() + " = ?";
            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    private String getFilenamesWithoutMetadataInSql(String tablename, String columnName) {
        boolean isLink = !tablename.equals("xmp") && !tablename.equals("exif");
        return isLink
                ? Join.getNullSqlOf(tablename)
                : "SELECT files.filename FROM files INNER JOIN "
                + tablename + " ON files.id = " + tablename + ".id_file WHERE "
                + tablename + "." + columnName + " IS NULL"
                + " UNION SELECT files.filename FROM files " + Join.getUnjoinedFilesSqlWhere(tablename);
    }

    /**
     * Returns the names of files without specific metadata.
     *
     * @param   column column where it's table has to be either table "exif"
     *                 or table <code>"xmp"</code>
     * @return         image files without metadata for that column
     */
    public List<File> getImageFilesWithoutMetadataInColumn(MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        List<File> imageFiles = new ArrayList<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String columnName = column.getValueName();
            String tablename = column.getCategory();
            String sql = getFilenamesWithoutMetadataInSql(tablename, columnName);
            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    /**
     * Returns the database ID of a filename.
     *
     * Intended for usage within other database methods.
     *
     * @param  con  connection
     * @param  file file
     * @return      database ID or -1 if the filename does not exist
     * @throws      SQLException on SQL errors
     */
    long findIdImageFile(Connection con, File file) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(file));
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }
        return id;
    }

    public Set<File> getAllThumbnailFiles() {
        Set<File> tnFiles = new HashSet<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT filename FROM files";
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            File tnFile;
            while (rs.next()) {
                tnFile = tnRepo.findThumbnailFile(getFile(rs.getString(1)));

                if (tnFile != null) {
                    tnFiles.add(tnFile);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return tnFiles;
    }

    public void deleteValueOfJoinedColumn(MetaDataValue column, String value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        String sql = Join.getDeleteSql(column.getCategory());
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(sql);
            stmt.setString(1, value);
            logFiner(stmt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
    }

    /**
     * Deletes a Dublin Core subject.
     * <p>
     * <em>Call this method only, if You are sure, that no image has that
     * subject!</em>
     *
     * @param dcSubject subject
     */
    public void deleteDcSubject(String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, dcSubject);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            con.commit();
            if (count > 0) {
                notifyDcSubjectDeleted(dcSubject);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
    }

    public boolean existsDcSubject(String subject) {
        if (subject == null) {
            throw new NullPointerException("subject == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, subject);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) == 1;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public Long getIdDcSubject(String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }
        Long id = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT id FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, dcSubject);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return id;
    }

    public boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject) {
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM xmp_dc_subject WHERE id_xmp = ? AND id_dc_subject = ?");
            stmt.setLong(1, idXmp);
            stmt.setLong(2, idDcSubject);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) == 1;
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFilesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    void notifyImageFileDeleted(File imageFile) {
        EventBus.publish(new ImageFileDeletedEvent(this, imageFile));
    }

    private void notifyImageFileInserted(File imageFile) {
        EventBus.publish(new ImageFileInsertedEvent(this, imageFile));
    }

    private void notifyImageFileMoved(File oldFile, File newFile) {
        EventBus.publish(new ImageFileMovedEvent(this, oldFile, newFile));
    }

    private void notifyXmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        EventBus.publish(new XmpUpdatedEvent(this, imageFile, oldXmp, updatedXmp));
    }

    private void notifyXmpInserted(File imageFile, Xmp xmp) {
        EventBus.publish(new XmpInsertedEvent(this, imageFile, xmp));
    }

    private void notifyXmpDeleted(File imageFile, Xmp xmp) {
        EventBus.publish(new XmpDeletedEvent(this, imageFile, xmp));
    }

    private void notifyExifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        EventBus.publish(new ExifUpdatedEvent(this, imageFile, oldExif, updatedExif));
    }

    private void notifyExifInserted(File imageFile, Exif eExif) {
        EventBus.publish(new ExifInsertedEvent(this, imageFile, eExif));
    }

    private void notifyExifDeleted(File imageFile, Exif exif) {
        EventBus.publish(new ExifDeletedEvent(exif, imageFile, exif));
    }

    private void notifyThumbnailUpdated(File imageFile) {
        EventBus.publish(new ThumbnailUpdatedEvent(this, imageFile));
    }

    private void notifyDcSubjectInserted(String dcSubject) {
        EventBus.publish(new DcSubjectInsertedEvent(this, dcSubject));
    }

    private void notifyDcSubjectDeleted(String dcSubject) {
        EventBus.publish(new DcSubjectDeletedEvent(this, dcSubject));
    }

    /**
     * Sets to a prepared statement all strings in a list in the order as they
     * appear in the list.
     *
     * @param  stmt   statement
     * @param  params parameters to set
     * @param  offset 0 if the first string is the first parameter in the
     *                statement. Greater than zero, if the first string shall
     *                be the parameter <code>1 + offset</code>.
     * @throws        SQLException
     */
    private void setStringParams(PreparedStatement stmt, List<? extends String> params, int offset) throws SQLException {
        if (offset < 0) {
            return;
        }
        int size = params.size();
        for (int i = 0; i < size; i++) {
            stmt.setString(i + 1 + offset, params.get(i));
        }
    }

    /**
     * Returns parameters (?) whitin paranteses.
     *
     * @param  count count of parameters
     * @return       parameters in parantheses, e.g. <code>"(?, ?, ?)"</code>
     *               if count equals 3
     */
    private String getParamsInParentheses(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count < 1: " + count);
        }
        StringBuilder sb = new StringBuilder(count * 2);
        sb.append("(");
        for (int i = 0; i < count; i++) {
            sb.append((i == 0)
                    ? ""
                    : ",").append("?");
        }
        sb.append(")");
        return sb.toString();
    }
}
