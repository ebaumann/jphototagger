/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Timeline;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Join;
import de.elmar_baumann.jpt.database.metadata.Join.Type;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.lib.generics.Pair;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Database containing metadata of image files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseImageFiles extends Database {

    public static final DatabaseImageFiles INSTANCE = new DatabaseImageFiles();

    private final ListenerSupport<DatabaseImageFilesListener> listenerSupport = new ListenerSupport<DatabaseImageFilesListener>();

    private DatabaseImageFiles() {
    }

    /**
     * Renames a file.
     *
     * @param  oldFilename old filename
     * @param  newFilename new filename
     * @return             count of renamed files
     */
    public int updateRename(String oldFilename, String newFilename) {

        int count = 0;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(
                            "UPDATE files SET filename = ? WHERE filename = ?");
            stmt.setString(1, newFilename);
            stmt.setString(2, oldFilename);
            logFiner(stmt);
            count = stmt.executeUpdate();
            PersistentThumbnails.updateThumbnailName(oldFilename, newFilename);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return count;
    }

    public List<String> getAllFilenames() {
        List<String> files      = new ArrayList<String>();
        Connection   connection = null;
        Statement    stmt       = null;
        ResultSet    rs         = null;
        try {
            connection = getConnection();
            String sql = "SELECT filename FROM files";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                files.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return files;
    }

    private void deleteThumbnailOf(String imageFilename) {
        File tnFile = PersistentThumbnails.getThumbnailFileOfImageFile(imageFilename);
        if (tnFile == null) return;

        if (!tnFile.delete()) {
            AppLogger.logWarning(DatabaseImageFiles.class, "DatabaseImageFiles.Error.DeleteThumbnail", tnFile, imageFilename);
        }
    }

    private long getFileCountNameStartingWith(Connection connection, String start) throws SQLException {
        long              count = 0;
        String            sql   = "SELECT COUNT(*) FROM files WHERE filename LIKE ?";
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            stmt = connection.prepareStatement(sql);
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
     * @param  start           start substring of the old filenames
     * @param  newStart        new start substring
     * @param progressListener null or progress listener. The progress listener
     *                         can stop renaming via
     *                         {@link ProgressEvent#setStop(boolean)} (no rollback).
     * @return                 count of renamed files
     */
    public synchronized int updateRenameFilenamesStartingWith(
            final String start, final String newStart, final ProgressListener progressListener) {

        if (start.equals(newStart)) return 0;

        int               countRenamed  = 0;
        int               startLength   = start.length();
        Connection        connection    = null;
        PreparedStatement stmt          = null;
        ResultSet         rs            = null;
        ProgressEvent     progressEvent = new ProgressEvent(this, 0, 0, 0, null);
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement("SELECT filename FROM files WHERE filename LIKE ?");

            stmt.setString(1, start + "%");
            logFinest(stmt);
            rs = stmt.executeQuery();

            progressEvent.setMaximum((int) getFileCountNameStartingWith(connection, start));
            boolean stop = notifyProgressListenerStart(progressListener, progressEvent);

            while (!stop && rs.next()) {

                String oldFilename = rs.getString(1);
                String newFilename = newStart + oldFilename.substring(startLength);

                updateFilename(connection, oldFilename, newFilename);
                countRenamed++;

                progressEvent.setValue(countRenamed);
                stop = notifyProgressListenerPerformed(progressListener, progressEvent);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            close(rs, stmt);
            free(connection);
            notifyProgressListenerEnd(progressListener, null);
        }

        return countRenamed;
    }

    private void notifyImageFileDeleted(String imageFilename) {
        ImageFile imageFile = new ImageFile();
        imageFile.setFilename(imageFilename);
        notifyListeners(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED, imageFile);
    }

    private void updateFilename(Connection connection, String oldFileName, String newFileName) throws SQLException {

        if (oldFileName.equals(newFileName)) return;

        PreparedStatement stmt = null;
        try {
            String sql  = "UPDATE files SET filename = ? WHERE filename = ?";
            stmt = connection.prepareStatement(sql);

            stmt.setString(1, newFileName);
            stmt.setString(2, oldFileName);
            logFiner(stmt);
            stmt.executeUpdate();
            renameThumbnail(oldFileName, newFileName);
        } finally {
            close(stmt);
        }
    }

    private void renameThumbnail(String oldFileName, String newFileName) {
        File oldTnFile = PersistentThumbnails.getThumbnailFileOfImageFile(oldFileName);

        if (oldTnFile != null && oldTnFile.exists()) {
            String filename = PersistentThumbnails.getMd5Filename(newFileName);
            if (filename == null) return;
            File newTnFile = PersistentThumbnails.getThumbnailfile(filename);
            if (newTnFile == null) return;
            if (!oldTnFile.renameTo(newTnFile)) {
                AppLogger.logWarning(getClass(), "DatabaseImageFiles.Error.RenameThumbnail", oldTnFile, newTnFile);
            }
        }
    }

    private int deleteRowWithFilename(Connection connection, String filename) {
        int               countDeleted = 0;
        PreparedStatement stmt         = null;
        try {
            stmt = connection.prepareStatement("DELETE FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFiner(stmt);
            countDeleted = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
        }
        return countDeleted;
    }

    public synchronized boolean insertOrUpdateExif(String filename, Exif exif) {

        Connection connection = null;
        try {
            connection = getConnection();
            long idFile = findIdFile(connection, filename);
            if (idFile < 0) return false;
            Exif oldExif = getExifOf(filename);
            insertOrUpdateExif(connection, idFile, exif);
            notifyExifInsertedOrUpdated(filename, oldExif, exif);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return true;
    }

    private void notifyExifInsertedOrUpdated(String filename, Exif oldExif, Exif newExif) {
        ImageFile newImageFile = new ImageFile();
        ImageFile oldImageFile = new ImageFile();

        newImageFile.setExif(newExif);
        newImageFile.setFilename(filename);
        oldImageFile.setExif(oldExif);
        oldImageFile.setFilename(filename);

        notifyListeners(DatabaseImageFilesEvent.Type.EXIF_UPDATED, oldImageFile, newImageFile);
    }

    public synchronized boolean insertOrUpdateXmp(String filename, Xmp xmp) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            long idFile = findIdFile(connection, filename);
            if (idFile < 0) return false;

            Xmp oldXmp = getXmpOf(filename);

            insertOrUpdateXmp(connection, idFile, xmp);
            setLastModifiedXmp(filename, xmp.contains(ColumnXmpLastModified.INSTANCE) ? (Long) xmp.getValue(ColumnXmpLastModified.INSTANCE) : -1);

            notifyXmpInsertedOrUpdated(filename, oldXmp, xmp);

        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return true;
    }

    private void notifyXmpInsertedOrUpdated(String filename, Xmp oldXmp, Xmp newXmp) {

        ImageFile newImageFile    = new ImageFile();
        ImageFile oldImageFile = new ImageFile();

        newImageFile.setXmp(newXmp);
        newImageFile.setFilename(filename);

        oldImageFile.setXmp(oldXmp);
        oldImageFile.setFilename(filename);

        notifyListeners(DatabaseImageFilesEvent.Type.XMP_UPDATED, oldImageFile, newImageFile);
    }

    /**
     * Inserts an image file into the databse. If the image already existsValueIn
     * it's data will be updated.
     * <p>
     * Inserts or updates this metadata:
     *
     * <ul>
     * <li>EXIF when {@link ImageFile#isInsertExifIntoDb()} is true</li>
     * <li>XMP when {@link ImageFile#isInsertXmpIntoDb()} is true</li>
     * <li>Thumbnail when {@link ImageFile#isInsertThumbnailIntoDb()} is true</li>
     * </ul>
     *
     * @param  imageFile image
     * @return           true if inserted
     */
    public synchronized boolean insertOrUpdate(ImageFile imageFile) {
        boolean success = false;
        if (exists(imageFile.getFilename())) {
            return update(imageFile);
        }
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            String sqlWithXmpLastModified = "INSERT INTO files" +
                    " (filename, lastmodified, xmp_lastmodified)" +
                    " VALUES (?, ?, ?)";
            String sqlWithoutXmpLastModified = "INSERT INTO files" +
                    " (filename, lastmodified)" +
                    " VALUES (?, ?)";
            stmt = connection.prepareStatement(
                    imageFile.isInsertXmpIntoDb()
                    ? sqlWithXmpLastModified
                    : sqlWithoutXmpLastModified);
            String filename = imageFile.getFilename();
            stmt.setString(1, filename);
            stmt.setLong(2, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                stmt.setLong(3, getLastmodifiedXmp(imageFile));
            }
            logFiner(stmt);
            stmt.executeUpdate();
            long idFile = findIdFile(connection, filename);
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(PersistentThumbnails.getMd5Filename(filename), imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertXmp(connection, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertExif(connection, idFile, imageFile.getExif());
            }
            connection.commit();
            success = true;
            notifyListeners(DatabaseImageFilesEvent.Type.IMAGEFILE_INSERTED, imageFile);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return success;
    }

    /**
     * Returns an image file.
     *
     * @param  filename name of the image file
     * @return          image file
     */
    public ImageFile getImageFile(String filename) {
        ImageFile imageFile = new ImageFile();
        imageFile.setExif(getExifOf(filename));
        imageFile.setFilename(filename);
        imageFile.setLastmodified(getLastModifiedImageFile(filename));
        String md5File = PersistentThumbnails.getMd5Filename(filename);
        if (md5File != null) {
            imageFile.setThumbnail(PersistentThumbnails.getThumbnail(md5File));
        }
        imageFile.setXmp(getXmpOf(filename));
        return imageFile;
    }

    /**
     * Aktualisiert ein Bild in der Datenbank.
     * <p>
     * Updates this metadata:
     *
     * <ul>
     * <li>EXIF when {@link ImageFile#isInsertExifIntoDb()} is true</li>
     * <li>XMP when {@link ImageFile#isInsertXmpIntoDb()} is true</li>
     * <li>Thumbnail when {@link ImageFile#isInsertThumbnailIntoDb()} is true</li>
     * </ul>
     *
     * @param imageFile Bild
     * @return          true bei Erfolg
     */
    public boolean update(ImageFile imageFile) {
        boolean success = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        try {
            ImageFile oldImageFile = getImageFile(imageFile.getFilename());
            connection = getConnection();
            connection.setAutoCommit(false);
            String sqlWithXmpLastModified = "UPDATE files " +
                    "SET lastmodified = ?, xmp_lastmodified = ? WHERE id = ?";
            String sqlWithoutXmpLastModified = "UPDATE files " +
                    "SET lastmodified = ? WHERE id = ?";
            stmt = connection.prepareStatement(imageFile.isInsertXmpIntoDb()
                                                    ? sqlWithXmpLastModified
                                                    : sqlWithoutXmpLastModified);
            String filename = imageFile.getFilename();
            long idFile = findIdFile(connection, filename);
            stmt.setLong(1, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                stmt.setLong(2, getLastmodifiedXmp(imageFile));
            }
            stmt.setLong(imageFile.isInsertXmpIntoDb() ? 3 : 2, idFile);
            logFiner(stmt);
            stmt.executeUpdate();
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(PersistentThumbnails.getMd5Filename(filename), imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertOrUpdateXmp(connection, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertOrUpdateExif(connection, idFile, imageFile.getExif());
            }
            connection.commit();
            success = true;
            notifyListeners(DatabaseImageFilesEvent.Type.IMAGEFILE_UPDATED, oldImageFile, imageFile);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return success;
    }

    /**
     * Updates all thumbnails, reads the files from the file system and creates
     * thumbnails from the files.
     *
     * @param  listener progress listener, can stop action via event and receive
     *                  the current filename
     * @return          count of updated thumbnails
     */
    public int updateAllThumbnails(ProgressListener listener) {
        int updated = 0;
        Connection connection = null;
        Statement  stmt       = null;
        ResultSet  rs         = null;
        try {
            int           filecount     = DatabaseStatistics.INSTANCE.getFileCount();
            ProgressEvent progressEvent = new ProgressEvent(this, 0, filecount, 0, "");
            ImageFile     imageFile     = new ImageFile();

            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.createStatement();
            String sql = "SELECT filename FROM files ORDER BY filename ASC";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            int count = 0;
            notifyProgressListenerStart(listener, progressEvent);
            while (!progressEvent.isStop() && rs.next()) {
                String filename = rs.getString(1);
                updateThumbnailFile(PersistentThumbnails.getMd5Filename(filename), getThumbnailFromFile(filename));
                updated++;
                progressEvent.setValue(++count);
                progressEvent.setInfo(filename);
                notifyProgressListenerPerformed(listener, progressEvent);
                imageFile.setFilename(filename);
                notifyListeners(DatabaseImageFilesEvent.Type.THUMBNAIL_UPDATED, imageFile);
            }
            notifyProgressListenerEnd(listener, progressEvent);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return updated;
    }

    private Image getThumbnailFromFile(String filename) {
        return ThumbnailUtil.getThumbnail(new File(filename));
    }

    /**
     * Updates the thumbnail of an image file.
     *
     * @param  filename  filename
     * @param  thumbnail thumbnail
     * @return true if updated
     */
    public boolean updateThumbnail(String filename, Image thumbnail) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            updateThumbnailFile(PersistentThumbnails.getMd5Filename(filename), thumbnail);
            ImageFile imageFile = new ImageFile();
            imageFile.setFilename(filename);
            notifyListeners(DatabaseImageFilesEvent.Type.THUMBNAIL_UPDATED, imageFile);
            return true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return false;
    }

    private void updateThumbnailFile(String hash, Image thumbnail) {
        if (hash != null && thumbnail != null) {
            PersistentThumbnails.writeThumbnail(thumbnail, hash);
        }
    }

    /**
     * Returns the last modification time of an image file.
     *
     * @param  filename filename
     * @return time in milliseconds since 1970 or -1 if the file is not in
     *         the database or when errors occured
     */
    public long getLastModifiedImageFile(String filename) {
        long              lastModified = -1;
        Connection        connection   = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                    "SELECT lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return lastModified;
    }

    /**
     * Returns whether an file is stored in the database.
     *
     * @param  filename  filename
     * @return true if existsValueIn
     */
    public boolean exists(String filename) {
        boolean           exists     = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                               "SELECT COUNT(*) FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    /**
     * Entfernt eine Bilddatei aus der Datenbank.
     *
     * @param filenames Namen der zu löschenden Dateien
     * @return          Anzahl gelöschter Datensätze
     */
    public int delete(List<String> filenames) {
        int               countDeleted = 0;
        Connection        connection   = null;
        PreparedStatement stmt         = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(
                                        "DELETE FROM files WHERE filename = ?");
            for (String imageFilename : filenames) {
                stmt.setString(1, imageFilename);
                ImageFile oldImageFile = new ImageFile();
                oldImageFile.setFilename(imageFilename);
                oldImageFile.setExif(getExifOf(imageFilename));
                oldImageFile.setXmp(getXmpOf(imageFilename));
                logFiner(stmt);
                int countAffectedRows = stmt.executeUpdate();
                countDeleted += countAffectedRows;
                if (countAffectedRows > 0) {
                    deleteThumbnailOf(imageFilename);
                    notifyListeners(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED, oldImageFile, oldImageFile);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return countDeleted;
    }

    /**
     * Löscht aus der Datenbank alle Datensätze mit Bildern, die nicht
     * mehr im Dateisystem existieren.
     *
     * @param listener Listener oder null, falls kein Interesse am Fortschritt.
     *                 {@link de.elmar_baumann.jpt.event.listener.ProgressListener#progressEnded(de.elmar_baumann.jpt.event.ProgressEvent)}
     *                 liefert ein
     *                 {@link de.elmar_baumann.jpt.event.ProgressEvent}-Objekt,
     *                 das mit
     *                 {@link de.elmar_baumann.jpt.event.ProgressEvent#getInfo()}
     *                 ein Int-Objekt liefert mit der Anzahl der gelöschten
     *                 Datensätze.
     *                 {@link de.elmar_baumann.jpt.event.ProgressEvent#isStop()}
     *                 wird ausgewertet (Abbruch des Löschens).
     * @return         Anzahl gelöschter Datensätze
     */
    public int deleteNotExistingImageFiles(ProgressListener listener) {
        int           countDeleted = 0;
        ProgressEvent event        = new ProgressEvent(this, 0, DatabaseStatistics.INSTANCE.getFileCount(), 0, null);
        Connection    connection   = null;
        Statement     stmt         = null;
        ResultSet     rs           = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.createStatement();
            String sql = "SELECT filename FROM files";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            String imageFilename;
            boolean stop = notifyProgressListenerStart(listener, event);
            while (!stop && rs.next()) {
                imageFilename = rs.getString(1);
                File file = new File(imageFilename);
                if (!file.exists()) {
                    int deletedRows = deleteRowWithFilename(connection, imageFilename);
                    countDeleted += deletedRows;
                    if (deletedRows > 0) {
                        deleteThumbnailOf(imageFilename);
                        notifyImageFileDeleted(imageFilename);
                    }
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                stop = event.isStop();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        event.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, event);
        return countDeleted;
    }

    private long findIdXmpOfIdFile(Connection connection, long idFile) throws SQLException {
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = connection.prepareStatement("SELECT id FROM xmp WHERE id_files = ?");
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
     * @param  imageFilename <em>image</em> filename (<em>not</em> sidecar
     *                       filename)
     * @return               last modification time in milliseconds since 1970
     *                       or -1 if not defined
     */
    public long getLastModifiedXmp(String imageFilename) {
        long              lastModified = -1;
        Connection        connection   = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                       "SELECT xmp_lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, imageFilename);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return lastModified;
    }

    /**
     * Sets the last modification time of XMP metadata.
     *
     * @param  imageFilename image filename
     * @param  time          milliseconds since 1970
     * @return               true if set
     */
    public boolean setLastModifiedXmp(String imageFilename, long time) {
        boolean set = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(
                    "UPDATE files SET xmp_lastmodified = ? WHERE filename = ?");
            stmt.setLong(1, time);
            stmt.setString(2, imageFilename);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            set = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return set;
    }

    private long getLastmodifiedXmp(ImageFile imageFile) {
        Xmp xmp = imageFile.getXmp();
        return xmp == null
               ? -1
               : xmp.contains(ColumnXmpLastModified.INSTANCE)
               ? (Long) xmp.getValue(ColumnXmpLastModified.INSTANCE)
               : -1;
    }

    @SuppressWarnings("unchecked")
    private void insertXmp(Connection connection, long idFile, Xmp xmp) throws SQLException {

        if (xmp != null && !xmp.isEmpty()) {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertIntoXmpStatement());
                setXmpValues(stmt, idFile, xmp);
                logFiner(stmt);
                stmt.executeUpdate();
                long idXmp = findIdXmpOfIdFile(connection, idFile);
                if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                    insertXmpDcSubjects(connection, idXmp, (List<String>) xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE));
                }
            } finally {
                close(stmt);
            }
        }
    }

    private void insertXmpDcSubjects(Connection connection, long idXmp, List<String> dcSubjects) throws SQLException {

        String sql = "INSERT INTO xmp_dc_subjects (id_xmp, subject)";
        insertValues(
                connection,
                sql,
                idXmp,
                dcSubjects);
    }

    private void insertValues(
            Connection   connection,
            String       sql,
            long         id,
            List<String> values
            )
            throws SQLException {

        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(sql + " VALUES (?, ?)");
            for (String value : values) {
                stmt.setLong(1, id);
                stmt.setString(2, value);
                logFiner(stmt);
                stmt.executeUpdate();
            }
        } finally {
            close(stmt);
        }
    }

    private String getUpdateXmpStatement() {
        return "UPDATE xmp SET" +
                " id_files = ?" +                         // --  1 --
                ", dc_creator = ?" +                      // --  2 --
                ", dc_description = ?" +                  // --  3 --
                ", dc_rights = ?" +                       // --  4 --
                ", dc_title = ?" +                        // --  5 --
                ", iptc4xmpcore_countrycode = ?" +        // --  6 --
                ", iptc4xmpcore_location = ?" +           // --  7 --
                ", photoshop_authorsposition = ?" +       // --  8 --
                ", photoshop_captionwriter = ?" +         // --  9 --
                ", photoshop_city = ?" +                  // -- 10 --
                ", photoshop_country = ?" +               // -- 11 --
                ", photoshop_credit = ?" +                // -- 12 --
                ", photoshop_headline = ?" +              // -- 13 --
                ", photoshop_instructions = ?" +          // -- 14 --
                ", photoshop_source = ?" +                // -- 15 --
                ", photoshop_state = ?" +                 // -- 16 --
                ", photoshop_transmissionReference = ?" + // -- 17 --
                ", rating = ?" +                          // -- 18 --
                ", iptc4xmpcore_datecreated = ?" +        // -- 19 --
                " WHERE id = ?";                          // -- 20 --
    }

    private String getInsertIntoXmpStatement() {
        return "INSERT INTO xmp " +
                "(" +
                "id_files" +                          // --  1 --
                ", dc_creator" +                      // --  2 --
                ", dc_description" +                  // --  3 --
                ", dc_rights" +                       // --  4 --
                ", dc_title" +                        // --  5 --
                ", iptc4xmpcore_countrycode" +        // --  6 --
                ", iptc4xmpcore_location" +           // --  7 --
                ", photoshop_authorsposition" +       // --  8 --
                ", photoshop_captionwriter" +         // --  9 --
                ", photoshop_city" +                  // -- 10 --
                ", photoshop_country" +               // -- 11 --
                ", photoshop_credit" +                // -- 12 --
                ", photoshop_headline" +              // -- 13 --
                ", photoshop_instructions" +          // -- 14 --
                ", photoshop_source" +                // -- 15 --
                ", photoshop_state" +                 // -- 16 --
                ", photoshop_transmissionReference" + // -- 17 --
                ", rating" +                          // -- 18 --
                ", iptc4xmpcore_datecreated" +        // -- 19 --
                ")" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private void setXmpValues(PreparedStatement stmt, long idFile, Xmp xmp) throws SQLException {
        stmt.setLong(1, idFile);
        setString(xmp.getValue(ColumnXmpDcCreator.INSTANCE)                     , stmt,  2);
        setString(xmp.getValue(ColumnXmpDcDescription.INSTANCE)                 , stmt,  3);
        setString(xmp.getValue(ColumnXmpDcRights.INSTANCE)                      , stmt,  4);
        setString(xmp.getValue(ColumnXmpDcTitle.INSTANCE)                       , stmt,  5);
        setString(xmp.getValue(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE)       , stmt,  6);
        setString(xmp.getValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE)          , stmt,  7);
        setString(xmp.getValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE)      , stmt,  8);
        setString(xmp.getValue(ColumnXmpPhotoshopCaptionwriter.INSTANCE)        , stmt,  9);
        setString(xmp.getValue(ColumnXmpPhotoshopCity.INSTANCE)                 , stmt, 10);
        setString(xmp.getValue(ColumnXmpPhotoshopCountry.INSTANCE)              , stmt, 11);
        setString(xmp.getValue(ColumnXmpPhotoshopCredit.INSTANCE)               , stmt, 12);
        setString(xmp.getValue(ColumnXmpPhotoshopHeadline.INSTANCE)             , stmt, 13);
        setString(xmp.getValue(ColumnXmpPhotoshopInstructions.INSTANCE)         , stmt, 14);
        setString(xmp.getValue(ColumnXmpPhotoshopSource.INSTANCE)               , stmt, 15);
        setString(xmp.getValue(ColumnXmpPhotoshopState.INSTANCE)                , stmt, 16);
        setString(xmp.getValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE), stmt, 17);
        setLongMinMax(xmp.getValue(ColumnXmpRating.INSTANCE), ColumnXmpRating.getMinValue(), ColumnXmpRating.getMaxValue(), stmt, 18);
        setString(xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE)       , stmt, 19);
    }

    @SuppressWarnings("unchecked")
    private void insertOrUpdateXmp(Connection connection, long idFile, Xmp xmp) throws SQLException {

        if (xmp != null) {
            long idXmp = findIdXmpOfIdFile(connection, idFile);
            if (idXmp > 0) {
                PreparedStatement stmt = null;
                try {
                    stmt = connection.prepareStatement(getUpdateXmpStatement());
                    setXmpValues(stmt, idFile, xmp);
                    stmt.setLong(20, idXmp);
                    logFiner(stmt);
                    stmt.executeUpdate();
                    deleteXmpDcSubjects(connection, idXmp);
                    if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                        insertXmpDcSubjects(connection, idXmp, (List<String>) xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE));
                    }
                } finally {
                    close(stmt);
                }
            } else {
                insertXmp(connection, idFile, xmp);
            }
        }
    }

    private void deleteXmpDcSubjects(Connection connection, long idXmp) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement("DELETE FROM xmp_dc_subjects WHERE id_xmp = ?");
            stmt.setLong(1, idXmp);
            logFiner(stmt);
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }
    /**
     * Deletes XMP-Data of image files when a XMP sidecar file does not
     * exist but in the database is XMP data for this image file.
     *
     * @param  listener  progress listener
     * @return           count of deleted XMP data (one per image file)
     */
    public int deleteOrphanedXmp(ProgressListener listener) {
        int countDeleted = 0;
        ProgressEvent progressEvent = new ProgressEvent(this, 0, DatabaseStatistics.INSTANCE.getXmpCount(), 0, null);
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.createStatement();
            String sql = "SELECT files.filename" +
                    " FROM files, xmp" +
                    " WHERE files.id = xmp.id_files";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, progressEvent);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                if (XmpMetadata.getSidecarFilename(filename) == null) {
                    countDeleted += deleteXmpOfFilename(connection, filename);
                }
                progressEvent.setValue(progressEvent.getValue() + 1);
                notifyProgressListenerPerformed(listener, progressEvent);
                abort = progressEvent.isStop();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        progressEvent.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, progressEvent);
        return countDeleted;
    }

    private int deleteXmpOfFilename(Connection connection, String filename) {
        int count = 0;
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement(
                    "DELETE FROM xmp WHERE" +
                    " xmp.id_files in" +
                    " (SELECT xmp.id_files FROM xmp, files" +
                    " WHERE xmp.id_files = files.id AND files.filename = ?)");
            stmt.setString(1, filename);
            logFiner(stmt);
            count = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
        }
        return count;
    }

    private String getXmpOfFilesStatement(int fileCount) {
        return " SELECT" +
                " files.filename" +                                             // --  1 --
                ", xmp.dc_creator" +                                            // --  2 --
                ", xmp.dc_description" +                                        // --  3 --
                ", xmp.dc_rights" +                                             // --  4 --
                ", xmp.dc_title" +                                              // --  5 --
                ", xmp.iptc4xmpcore_countrycode" +                              // --  6 --
                ", xmp.iptc4xmpcore_location" +                                 // --  7  --
                ", xmp.photoshop_authorsposition" +                             // --  8 --
                ", xmp.photoshop_captionwriter" +                               // --  9 --
                ", xmp.photoshop_city" +                                        // -- 10 --
                ", xmp.photoshop_country" +                                     // -- 11 --
                ", xmp.photoshop_credit" +                                      // -- 12 --
                ", xmp.photoshop_headline" +                                    // -- 13 --
                ", xmp.photoshop_instructions" +                                // -- 14 --
                ", xmp.photoshop_source" +                                      // -- 15 --
                ", xmp.photoshop_state" +                                       // -- 16 --
                ", xmp.photoshop_transmissionReference" +                       // -- 17 --
                ", xmp_dc_subjects.subject" +                                   // -- 18 --
                ", xmp.rating" +                                                // -- 19 --
                ", xmp.iptc4xmpcore_datecreated" +                              // -- 20 --
                " FROM" +
                " files LEFT JOIN xmp" +
                " ON files.id = xmp.id_files" +
                " LEFT JOIN xmp_dc_subjects" +
                " ON xmp.id = xmp_dc_subjects.id_xmp" +
                " WHERE files.filename IN" +
                " (" + getPlaceholder(fileCount) + ")";
    }

    /**
     * Returns XMP metadata of files.
     *
     * @param filenames filenames
     * @return          XMP metadata where the first element of a pair is the
     *                  name of a file and the second the XMP metadata of that
     *                  file
     */
    public List<Pair<String, Xmp>> getXmpOf(Collection<? extends String> filenames) {
        List<Pair<String, Xmp>> list       = new ArrayList<Pair<String, Xmp>>();
        Connection              connection = null;
        PreparedStatement       stmt       = null;
        ResultSet               rs         = null;
        try {
            connection = getConnection();
            String            sql  = getXmpOfFilesStatement(filenames.size());
            stmt = connection.prepareStatement(sql);
            setStrings(stmt, filenames.toArray(new String[0]), 1);
            logFinest(stmt);
            rs = stmt.executeQuery();
            String prevFilename = "";
            Xmp xmp = new Xmp();
            while (rs.next()) {
                String filename = rs.getString(1);
                if (!filename.equals(prevFilename)) {
                    xmp = new Xmp();
                }
                xmp.setValue(ColumnXmpDcCreator.INSTANCE                     , getString(rs,  2));
                xmp.setValue(ColumnXmpDcDescription.INSTANCE                 , getString(rs,  3));
                xmp.setValue(ColumnXmpDcRights.INSTANCE                      , getString(rs,  4));
                xmp.setValue(ColumnXmpDcTitle.INSTANCE                       , getString(rs,  5));
                xmp.setValue(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE       , getString(rs,  6));
                xmp.setValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE          , getString(rs,  7));
                xmp.setValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE      , getString(rs,  8));
                xmp.setValue(ColumnXmpPhotoshopCaptionwriter.INSTANCE        , getString(rs,  9));
                xmp.setValue(ColumnXmpPhotoshopCity.INSTANCE                 , getString(rs, 10));
                xmp.setValue(ColumnXmpPhotoshopCountry.INSTANCE              , getString(rs, 11));
                xmp.setValue(ColumnXmpPhotoshopCredit.INSTANCE               , getString(rs, 12));
                xmp.setValue(ColumnXmpPhotoshopHeadline.INSTANCE             , getString(rs, 13));
                xmp.setValue(ColumnXmpPhotoshopInstructions.INSTANCE         , getString(rs, 14));
                xmp.setValue(ColumnXmpPhotoshopSource.INSTANCE               , getString(rs, 15));
                xmp.setValue(ColumnXmpPhotoshopState.INSTANCE                , getString(rs, 16));
                xmp.setValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE, getString(rs, 17));
                String dcSubject = getString(rs, 18);
                if (dcSubject != null) {
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubject);
                }
                xmp.setValue(ColumnXmpRating.INSTANCE                 , getLongMinMax(rs, 19, ColumnXmpRating.getMinValue(), ColumnXmpRating.getMaxValue()));
                xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, getString(rs, 20));
                if (!filename.equals(prevFilename)) {
                    list.add(new Pair<String, Xmp>(filename, xmp));
                }
                prevFilename = filename;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return list;
    }

    private void setStrings(
            PreparedStatement stmt,
            String[]          strings,
            int startIndex
            )
            throws SQLException {

        assert startIndex >= 1 : "Invalid SQL statement position: " + startIndex;

        int endIndex = startIndex + strings.length;
        int stringIndex = 0;
        for (int i = startIndex; i < endIndex; i++) {
            stmt.setString(i, strings[stringIndex++]);
        }
    }

    private static String getPlaceholder(int count) {
        StringBuilder sb = new StringBuilder(count * 3 - 2); // 3: ", ?"
        for (int i = 0; i < count; i++) {
            sb.append(i > 0 && i < count
                      ? ", ?"
                      : "?");
        }
        return sb.toString();
    }

    private String getXmpOfStatement() {
        return " SELECT" +
                " xmp.dc_creator" +                                             // --  1 --
                ", xmp.dc_description" +                                        // --  2 --
                ", xmp.dc_rights" +                                             // --  3 --
                ", xmp.dc_title" +                                              // --  4 --
                ", xmp.iptc4xmpcore_countrycode" +                              // --  5 --
                ", xmp.iptc4xmpcore_location" +                                 // --  6  --
                ", xmp.photoshop_authorsposition" +                             // --  7 --
                ", xmp.photoshop_captionwriter" +                               // --  8 --
                ", xmp.photoshop_city" +                                        // --  9 --
                ", xmp.photoshop_country" +                                     // -- 10 --
                ", xmp.photoshop_credit" +                                      // -- 11 --
                ", xmp.photoshop_headline" +                                    // -- 12 --
                ", xmp.photoshop_instructions" +                                // -- 13 --
                ", xmp.photoshop_source" +                                      // -- 14 --
                ", xmp.photoshop_state" +                                       // -- 15 --
                ", xmp.photoshop_transmissionReference" +                       // -- 16 --
                ", xmp_dc_subjects.subject" +                                   // -- 17 --
                ", xmp.rating" +                                                // -- 18 --
                ", xmp.iptc4xmpcore_datecreated" +                              // -- 19 --
                " FROM" +
                " files INNER JOIN xmp" +
                " ON files.id = xmp.id_files" +
                " LEFT JOIN xmp_dc_subjects" +
                " ON xmp.id = xmp_dc_subjects.id_xmp" +
                " WHERE files.filename = ?";
    }

    /**
     * Liefert die XMP-Daten einer Datei.
     *
     * @param  filename Dateiname
     * @return          XMP-Daten der Datei
     */
    public Xmp getXmpOf(String filename) {
        Xmp xmp = new Xmp();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(getXmpOfStatement());
            stmt.setString(1, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                xmp.setValue(ColumnXmpDcCreator.INSTANCE                     , getString(rs,  1));
                xmp.setValue(ColumnXmpDcDescription.INSTANCE                 , getString(rs,  2));
                xmp.setValue(ColumnXmpDcRights.INSTANCE                      , getString(rs,  3));
                xmp.setValue(ColumnXmpDcTitle.INSTANCE                       , getString(rs,  4));
                xmp.setValue(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE       , getString(rs,  5));
                xmp.setValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE          , getString(rs,  6));
                xmp.setValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE      , getString(rs,  7));
                xmp.setValue(ColumnXmpPhotoshopCaptionwriter.INSTANCE        , getString(rs,  8));
                xmp.setValue(ColumnXmpPhotoshopCity.INSTANCE                 , getString(rs,  9));
                xmp.setValue(ColumnXmpPhotoshopCountry.INSTANCE              , getString(rs, 10));
                xmp.setValue(ColumnXmpPhotoshopCredit.INSTANCE               , getString(rs, 11));
                xmp.setValue(ColumnXmpPhotoshopHeadline.INSTANCE             , getString(rs, 12));
                xmp.setValue(ColumnXmpPhotoshopInstructions.INSTANCE         , getString(rs, 13));
                xmp.setValue(ColumnXmpPhotoshopSource.INSTANCE               , getString(rs, 14));
                xmp.setValue(ColumnXmpPhotoshopState.INSTANCE                , getString(rs, 15));
                xmp.setValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE, getString(rs, 16));
                String dcSubject = getString(rs, 17);
                if (dcSubject != null) {
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE, dcSubject);
                }
                xmp.setValue(ColumnXmpRating.INSTANCE                 , getLongMinMax(rs, 18, ColumnXmpRating.getMinValue(), ColumnXmpRating.getMaxValue()));
                xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, getString(rs, 19));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return xmp;
    }

    /**
     * Returns the dublin core subjects (keywords).
     *
     * @return dc subjects distinct ordererd ascending
     */
    public Set<String> getAllDcSubjects() {
        Set<String> dcSubjects = new LinkedHashSet<String>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT DISTINCT subject" +
                    " FROM xmp_dc_subjects" +
                    " ORDER BY 1 ASC";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return dcSubjects;
    }

    /**
     * Returns all dublin core subjects (keywords) of a file.
     *
     * @param  filename name of the file
     * @return          dc subjects (keywords) ordered ascending
     */
    public List<String> getDcSubjectsOf(String filename) {
        List<String> dcSubjects = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT DISTINCT xmp_dc_subjects.subject FROM" +
                    " files INNER JOIN xmp ON files.id = xmp.id_files" +
                    " INNER JOIN xmp_dc_subjects" +
                    " ON xmp.id = xmp_dc_subjects.id_xmp" +
                    " WHERE files.filename = ? " +
                    " ORDER BY xmp_dc_subjects.subject ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return dcSubjects;
    }

    public enum DcSubjectOption {
        INCLUDE_SYNONYMS
    }

    /**
     * Returns the filenames within a specific dublin core subject (keyword).
     *
     * @param  dcSubject subject
     * @param options    options
     * @return           filenames
     */
    public Set<String> getFilenamesOfDcSubject(String dcSubject, DcSubjectOption... options) {
        Set<String> filenames  = new LinkedHashSet<String>();
        Connection  connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Set<DcSubjectOption> opts = new HashSet<DcSubjectOption>(Arrays.asList(options));
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(getGetFilenamesOfDcSubjectSql(dcSubject, opts));
            setDcSubjectSynonyms(dcSubject, opts, stmt);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return filenames;
    }
    
    private void setDcSubjectSynonyms(String dcSubject, Set<DcSubjectOption> options, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, dcSubject);
        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            int paramIndex = 2;
            for (String synonym : DatabaseSynonyms.INSTANCE.getSynonymsOf(dcSubject)) {
                stmt.setString(paramIndex++, synonym);
            }
        }
    }

    private String getGetFilenamesOfDcSubjectSql(String dcSubject, Set<DcSubjectOption> options) {
        StringBuilder sql = new StringBuilder(" SELECT DISTINCT files.filename FROM" +
                    " xmp_dc_subjects INNER JOIN xmp" +
                    " ON xmp_dc_subjects.id_xmp = xmp.id" +
                    " INNER JOIN files ON xmp.id_files = files.id" +
                    " WHERE xmp_dc_subjects.subject = ?");

        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            int size = DatabaseSynonyms.INSTANCE.getSynonymsOf(dcSubject).size();
            for (int i = 0; i < size; i++) {
                sql.append(" OR xmp_dc_subjects.subject = ?");
            }
        }

        return sql.toString();
    }

    /**
     * Returns all images which have all subjects of a list.
     *
     * E.g. If You are searching for an image with a tree AND a cloud AND
     * a car the list contains these three words.
     *
     * Because it's faster, call {@link #getFilenamesOfDcSubject(java.lang.String)}
     * if You are searching for only one subject.
     *
     * @param  dcSubjects subjects
     * @return            images containing all of these subjects
     */
    public Set<String> getFilenamesOfAllDcSubjects(List<? extends String> dcSubjects) {
        Set<String> filenames  = new LinkedHashSet<String>();
        Connection  connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            int count = dcSubjects.size();
            String sql =
                    " SELECT files.filename FROM" +
                    " xmp_dc_subjects INNER JOIN xmp" +
                    " ON xmp_dc_subjects.id_xmp = xmp.id" +
                    " INNER JOIN files ON xmp.id_files = files.id" +
                    " WHERE xmp_dc_subjects.subject IN " +
                    Util.getParamsInParentheses(count) +
                    " GROUP BY files.filename" +
                    " HAVING COUNT(*) = " + count;
            stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return filenames;
    }

    /**
     * Returns all images which have at least one of subjects in a list.
     *
     * Because it's faster, call {@link #getFilenamesOfDcSubject(java.lang.String)}
     * if You are searching for only one subject.
     *
     * @param  dcSubjects subjects
     * @return            images containing one or more of these subjects
     */
    public Set<String> getFilenamesOfDcSubjects(List<? extends String> dcSubjects) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            int count = dcSubjects.size();
            String sql =
                    " SELECT DISTINCT files.filename FROM" +
                    " xmp_dc_subjects INNER JOIN xmp" +
                    " ON xmp_dc_subjects.id_xmp = xmp.id" +
                    " INNER JOIN files ON xmp.id_files = files.id" +
                    " WHERE xmp_dc_subjects.subject IN " +
                    Util.getParamsInParentheses(count);
            stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return filenames;
    }

    /**
     * Returns all images which have all words of a list in a column.
     *
     * E.g. If You are searching for an image with a tree AND a cloud AND
     * a car the list contains these three words.
     *
     * @param  words  search words
     * @param  column column to search. The table of that column has to be
     *                joinable with {@link de.elmar_baumann.jpt.database.metadata.file.TableFiles}
     *                through a column <code>id_files</code>!
     * @return        images containing all of these terms in that column
     */
    public Set<String> getFilenamesOfAll(Column column, List<? extends String> words) {
        Set<String>       filenames  = new LinkedHashSet<String>();
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            int count = words.size();
            String sql =
                    " SELECT files.filename FROM " +
                    tableName + " INNER JOIN files" +
                    " ON " + tableName + ".id_files = files.id" +
                    " WHERE" + tableName + "." + columnName + " IN " +
                    Util.getParamsInParentheses(count) +
                    " GROUP BY files.filename" +
                    " HAVING COUNT(*) = " + count;
            stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, words, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return filenames;
    }

    private String getUpdateExifStatement() {
        return "UPDATE exif" +
                " SET id_files = ?" +              // -- 1 --
                ", exif_recording_equipment = ?" + // -- 2 --
                ", exif_date_time_original = ?" +  // -- 3 --
                ", exif_focal_length = ?" +        // -- 4 --
                ", exif_iso_speed_ratings = ?" +   // -- 5 --
                ", exif_lens = ?" +                // -- 6 --
                " WHERE id_files = ?";             // -- 7 --
    }

    private void insertOrUpdateExif(Connection connection, long idFile, Exif exif) throws SQLException {

        if (exif != null) {
            long idExif = findIdExifOfIdFile(connection, idFile);
            if (idExif > 0) {
                PreparedStatement stmt = null;
                try {
                    stmt = connection.prepareStatement(getUpdateExifStatement());
                    setExifValues(stmt, idFile, exif);
                    stmt.setLong(7, idFile);
                    logFiner(stmt);
                    stmt.executeUpdate();
                } finally {
                    close(stmt);
                }
            } else {
                insertExif(connection, idFile, exif);
            }
        }
    }

    private String getInsertIntoExifStatement() {
        return "INSERT INTO exif" +
                " (" +
                "id_files" +                   // -- 1 --
                ", exif_recording_equipment" + // -- 2 --
                ", exif_date_time_original" +  // -- 3 --
                ", exif_focal_length" +        // -- 4 --
                ", exif_iso_speed_ratings" +   // -- 5 --
                ", exif_lens" +                // -- 6 --
                ")" +
                " VALUES (?, ?, ?, ?, ?, ?)";
    }

    private void insertExif(Connection connection, long idFile, Exif exif) throws SQLException {

        if (exif != null && !exif.isEmpty()) {
            PreparedStatement stmt = null;
            try {
                stmt = connection.prepareStatement(getInsertIntoExifStatement());
                setExifValues(stmt, idFile, exif);
                logFiner(stmt);
                stmt.executeUpdate();
            } finally {
                close(stmt);
            }
        }
    }


    private void setExifValues(PreparedStatement stmt, long idFile, Exif exif) throws SQLException {

        stmt.setLong(1, idFile);
        String recordingEquipment = exif.getRecordingEquipment();
        if (recordingEquipment == null || recordingEquipment.trim().isEmpty()) {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(2, recordingEquipment);
        }
        Date date = exif.getDateTimeOriginal();
        if (date == null) {
            stmt.setNull(2, java.sql.Types.DATE);
        } else {
            stmt.setDate(3, date);
        }
        double focalLength = exif.getFocalLength();
        if (focalLength > 0) {
            stmt.setDouble(4, focalLength);
        } else {
            stmt.setNull(4, java.sql.Types.DOUBLE);
        }
        short iso = exif.getIsoSpeedRatings();
        if (iso > 0) {
            stmt.setShort(5, iso);
        } else {
            stmt.setNull(5, java.sql.Types.SMALLINT);
        }
        String lens = exif.getLens();
        if (lens == null) {
            stmt.setNull(6, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(6, lens);
        }
    }

    private long findIdExifOfIdFile(Connection connection, long idFile) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        try {
            stmt = connection.prepareStatement("SELECT id FROM exif WHERE id_files = ?");
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
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT exif_date_time_original" +
                    " FROM exif" +
                    " WHERE exif_date_time_original IS NOT NULL" +
                    " ORDER BY exif_date_time_original ASC";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs.getDate(1));
                timeline.add(cal);
            }
            addXmpDateCreated(connection, timeline);
            timeline.addUnknownNode();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return timeline;
    }

    private void addXmpDateCreated(Connection connection, Timeline timeline) throws SQLException {
        Statement stmt = null;
        ResultSet rs   = null;
        String    sql  = "SELECT iptc4xmpcore_datecreated FROM xmp" +
                         " WHERE iptc4xmpcore_datecreated IS NOT NULL";
        try {
            stmt = connection.createStatement();
            logFinest(sql);
            rs   = stmt.executeQuery(sql);
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
    public Set<File> getFilesOf(int year, int month, int day) {
        Set<File> files = new HashSet<File>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT files.filename" +
                    " FROM exif LEFT JOIN files" +
                    " ON exif.id_files = files.id" +
                    " WHERE exif.exif_date_time_original LIKE ?" +
                    " UNION" +
                    " SELECT files.filename" +
                    " FROM xmp LEFT JOIN files" +
                    " ON xmp.id_files = files.id" +
                    " WHERE xmp.iptc4xmpcore_datecreated LIKE ?" +
                    " ORDER BY files.filename ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, getSqlDate(year, month, day));
            stmt.setString(2, getXmpDate(year, month, day));
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return files;
    }

    public String getSqlDate(int year, int month, int day) {
        return String.valueOf(year) +
                "-" + (month > 0
                          ? getMonthDayPrefix(month) + String.valueOf(month)
                          : "%") +
                "-" + (month > 0 && day > 0
                          ? getMonthDayPrefix(day) + String.valueOf(day)
                          : "%");
    }

    public String getXmpDate(int year, int month, int day) {
        return String.valueOf(year) +
                      (month > 0
                          ? "-" + getMonthDayPrefix(month) + String.valueOf(month)
                          : "%") +
                      (month > 0 && day > 0
                          ? "-" + getMonthDayPrefix(day) + String.valueOf(day)
                          : "");
    }

    private static String getMonthDayPrefix(int i) {
        return i >= 10 ? "" : "0";
    }

    /**
     * Returns image files without EXIF date time taken or without XMP date
     * created.
     *
     * @return image files
     */
    public List<File> getFilesOfUnknownDate() {
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT files.filename" +
                    " FROM exif INNER JOIN files" +
                    " ON exif.id_files = files.id" +
                    " WHERE exif.exif_date_time_original IS NULL" +
                    " UNION " +
                    " SELECT files.filename" +
                    " FROM xmp INNER JOIN files" +
                    " ON xmp.id_files = files.id" +
                    " WHERE xmp.iptc4xmpcore_datecreated IS NULL" +
                    " ORDER BY files.filename ASC"
                    ;
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            addFilesWithoutExif(files, connection);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return files;
    }

    // UNION can cause memory exhausting
    private void addFilesWithoutExif(List<File> files, Connection connection) throws SQLException {
        String sql =
                "SELECT files.filename" +
                " FROM files" +
                " WHERE files.id NOT IN " +
                " (SELECT exif.id_files FROM exif)" +
                " ORDER BY files.filename ASC";
        Statement stmt = null;
        ResultSet rs   = null;
        try {
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
        } finally {
            close(rs, stmt);
        }
    }

    public Set<String> getAllDistinctValuesOf(Column column) {
        Set<String> values = new LinkedHashSet<String>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT DISTINCT " +
                    column.getName() +
                    " FROM " +
                    column.getTable().getName() +
                    " WHERE " +
                    column.getName() +
                    " IS NOT NULL" +
                    " ORDER BY " +
                    column.getName();
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return values;
    }

    /**
     * Returns files with specific values (where the column is not null), e.g.
     * files with ISO speed ratings in the EXIF table.
     *
     * @param  column column of a table which can be joined through a column
     *                named <code>id_files</code> with the table files, column
     *                <code>id</code>
     * @return        all distinct files with values in that column
     */
    public List<File> getFilesNotNullIn(Column column) {
        assert !column.isForeignKey() : column;
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            String sql =
                    "SELECT DISTINCT files.filename" +
                    " FROM " + tableName +
                    " INNER JOIN files" +
                    " ON " + tableName + ".id_files = files.id" +
                    " WHERE " + tableName + "." + columnName +
                    " IS NOT NULL" +
                    " ORDER BY files.filename ASC";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
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
     *     <code>id_files</code></li>
     * </ul>
     *
     * This method is also unusable for one to many references (columns which
     * are foreign keys).
     *
     * @param  column     column whith the value
     * @param  exactValue exact value of the column content
     * @return            files
     */
    public List<File> getFilesJoinTable(Column column, String exactValue) {
        assert !column.isForeignKey() : column;
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            String sql =
                    "SELECT files.filename" +
                    " FROM " + tableName +
                    " INNER JOIN files" +
                    " ON " + tableName + ".id_files = files.id" +
                    " WHERE " + tableName + "." + columnName +
                    " = ?" +
                    " ORDER BY files.filename ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, exactValue);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return files;
    }

    /**
     * Returns exif metadata of a specific file.
     *
     * @param  filename filenam
     * @return          EXIF metadata or null if that file has no EXIF metadata
     */
    public Exif getExifOf(String filename) {
        Exif exif = null;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs =null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(getExifOfStatement());
            stmt.setString(1, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exif = new Exif();
                exif.setRecordingEquipment(rs.getString(1));
                exif.setDateTimeOriginal(rs.getDate(2));
                exif.setFocalLength(rs.getDouble(3));
                exif.setIsoSpeedRatings(rs.getShort(4));
                exif.setLens(rs.getString(5));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exif;
    }

    private String getExifOfStatement() {
        return "SELECT" +
                " exif_recording_equipment" +      // -- 1 --
                ", exif.exif_date_time_original" + // -- 2 --
                ", exif.exif_focal_length" +       // -- 3 --
                ", exif.exif_iso_speed_ratings" +  // -- 4 --
                ", exif.exif_lens" +               // -- 5 --
                " FROM files INNER JOIN exif" +
                " ON files.id = exif.id_files" +
                " AND files.filename = ?";
    }

    public boolean existsExifDate(java.sql.Date date) {
        boolean exists = false;
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            String sql =
                    "SELECT COUNT(*)" +
                    " FROM exif" +
                    " WHERE exif_date_time_original" +
                    " LIKE '" + year + "-" + getMonthDayPrefix(month) + month +
                    "-" + getMonthDayPrefix(day) + day + "%'";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    public boolean existsXMPDateCreated(String date) {
        boolean    exists     = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*)" +
                    " FROM xmp" +
                    " WHERE iptc4xmpcore_datecreated = ?";
            stmt = connection.prepareStatement(sql);

            stmt.setString(1, date);
            logFinest(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    /**
     * Returns wheter a specific value existsValueIn in a table.
     *
     * @param  column column of the table, where the value shall exist
     * @param  value  value
     * @return        true if the value existsValueIn
     */
    public boolean exists(Column column, Object value) {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*)" +
                    " FROM " + column.getTable().getName() +
                    " WHERE " + column.getName() +
                    " = ?";
            stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            stmt.setObject(1, value);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    /**
     * Returns the names of files without specific metadata.
     *
     * @param   column column where it's table has to be either {@link de.elmar_baumann.jpt.database.metadata.exif.TableExif}
     *                 or {@link TableXmp}
     * @return         names of files without metadata for that column
     */
    public List<String> getFilenamesWithoutMetadataIn(Column column) {
        if (!checkIsExifOrXmpColumn(column)) return new ArrayList<String>();
        List<String> files = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String columnName = column.getName();
            String tableName = column.getTable().getName();
            String sql = "SELECT" +
                    " files.filename" +
                    " FROM" +
                    (tableName.startsWith("exif")
                     ? Join.getSqlFilesExifJoin(Type.LEFT, Arrays.asList(
                    tableName))
                     : Join.getSqlFilesXmpJoin(Type.LEFT, Type.LEFT, Arrays.
                    asList(tableName))) +
                    " WHERE " + tableName + "." + columnName + " IS NULL";
            stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return files;
    }

    private boolean checkIsExifOrXmpColumn(Column column) {
        boolean isExifOrXmpColumn = column.getTable().getName().startsWith("exif") ||
                column.getTable().getName().startsWith("xmp");
        assert isExifOrXmpColumn : "Only EXIF or XMP table are valid, not: " + column.getTable();
        return isExifOrXmpColumn;
    }

    /**
     * Returns the database ID of a filename.
     *
     * Intended for usage within other database methods.
     *
     * @param  connection   connection
     * @param  filename     filename
     * @return              database ID or -1 if the filename does not exist
     * @throws SQLException on SQL errors
     */
    long findIdFile(Connection connection, String filename) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT id FROM files WHERE filename = ?");
            stmt.setString(1, filename);
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
     * Returns all thumbnail files.
     *
     * @return files
     */
    public Set<File> getAllThumbnailFiles() {
        Set<File> tnFiles = new HashSet<File>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT filename FROM files";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            File tnFile = null;
            String hash = null;
            while (rs.next()) {
                hash = PersistentThumbnails.getMd5Filename(rs.getString(1));
                if (hash != null) {
                    tnFile = PersistentThumbnails.getThumbnailfile(hash);
                    if (tnFile != null) {
                        tnFiles.add(tnFile);
                    }
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return tnFiles;
    }

    public void addListener(DatabaseImageFilesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseImageFilesListener listener) {
        listenerSupport.remove(listener);
    }

    void notifyListeners(
            DatabaseImageFilesEvent.Type  type,
            ImageFile                     oldImageFile,
            ImageFile                     newImageFile) {

        DatabaseImageFilesEvent         event     = new DatabaseImageFilesEvent(type);
        Set<DatabaseImageFilesListener> listeners = listenerSupport.get();

        event.setImageFile(newImageFile);
        event.setOldImageFile(oldImageFile);

        synchronized(listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.actionPerformed(event);
            }
        }
    }

    void notifyListeners(
            DatabaseImageFilesEvent.Type type,
            ImageFile               imageFile
            ) {
        DatabaseImageFilesEvent         event     = new DatabaseImageFilesEvent(type);
        Set<DatabaseImageFilesListener> listeners = listenerSupport.get();

        event.setImageFile(imageFile);

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.actionPerformed(event);
            }
        }
    }
}
