/*
 * @(#)DatabaseImageFiles.java    Created on 2008-10-21
 *
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
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpIptc4XmpCoreDateCreated;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpLastModified;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopTransmissionReference;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.lib.generics.Pair;

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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Database containing metadata of image files.
 *
 * @author  Elmar Baumann
 */
public final class DatabaseImageFiles extends Database {
    public static final DatabaseImageFiles                    INSTANCE =
        new DatabaseImageFiles();
    private final ListenerSupport<DatabaseImageFilesListener> ls       =
        new ListenerSupport<DatabaseImageFilesListener>();

    public enum DcSubjectOption { INCLUDE_SYNONYMS }

    private DatabaseImageFiles() {}

    /**
     * Renames an image file.
     *
     * @param  fromImageFile old image file
     * @param  toImageFile   new renamed image file
     * @return               count of renamed files (0 or 1)
     */
    public int updateRename(File fromImageFile, File toImageFile) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE files SET filename = ? WHERE filename = ?");
            stmt.setString(1, getFilePath(toImageFile));
            stmt.setString(2, getFilePath(fromImageFile));
            logFiner(stmt);
            count = stmt.executeUpdate();

            if (PersistentThumbnails.renameThumbnailOfImageFile(fromImageFile,
                    toImageFile)) {
                notifyImageFileRenamed(fromImageFile, toImageFile);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public List<File> getAllImageFiles() {
        List<File> files = new ArrayList<File>();
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return files;
    }

    private long getFileCountNameStartingWith(Connection con, String start)
            throws SQLException {
        long              count = 0;
        String            sql   =
            "SELECT COUNT(*) FROM files WHERE filename LIKE ?";
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

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
     * @param  start           start substring of the old filenames
     * @param  newStart        new start substring
     * @param progressListener null or progress listener. The progress listener
     *                         can stop renaming via
     *                         {@link ProgressEvent#setStop(boolean)}
     *                         (no rollback).
     * @return                 count of renamed files
     */
    public synchronized int updateRenameFilenamesStartingWith(
            final String start, final String newStart,
            final ProgressListener progressListener) {
        if (start.equals(newStart)) {
            return 0;
        }

        int               countRenamed  = 0;
        int               startLength   = start.length();
        Connection        con           = null;
        PreparedStatement stmt          = null;
        ResultSet         rs            = null;
        ProgressEvent     progressEvent = new ProgressEvent(this, 0, 0, 0,
                                              null);

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "SELECT filename FROM files WHERE filename LIKE ?");
            stmt.setString(1, start + "%");
            logFinest(stmt);
            rs = stmt.executeQuery();
            progressEvent.setMaximum((int) getFileCountNameStartingWith(con,
                    start));

            boolean stop = notifyProgressListenerStart(progressListener,
                               progressEvent);

            while (!stop && rs.next()) {
                String from = rs.getString(1);
                String to   = newStart + from.substring(startLength);

                updateImageFilename(con, getFile(from), getFile(to));
                countRenamed++;
                progressEvent.setValue(countRenamed);
                stop = notifyProgressListenerPerformed(progressListener,
                        progressEvent);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(con);
        } finally {
            close(rs, stmt);
            free(con);
            notifyProgressListenerEnd(progressListener, null);
        }

        return countRenamed;
    }

    private void updateImageFilename(Connection con, File fromImageFile,
                                     File toImageFile)
            throws SQLException {
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
            notifyImageFileRenamed(fromImageFile, toImageFile);
            PersistentThumbnails.renameThumbnailOfImageFile(fromImageFile,
                    toImageFile);
        } finally {
            close(stmt);
        }
    }

    private int deleteRowWithFilename(Connection con, File imageFile) {
        int               countDeleted = 0;
        PreparedStatement stmt         = null;

        try {
            stmt = con.prepareStatement("DELETE FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFiner(stmt);
            countDeleted = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
        }

        return countDeleted;
    }

    public synchronized boolean insertOrUpdateExif(File imageFile, Exif exif) {
        Connection con = null;

        try {
            con = getConnection();

            long idFile = findIdImageFile(con, imageFile);

            if (idFile < 0) {
                return false;
            }

            insertOrUpdateExif(con, imageFile, idFile, exif);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(con);
        } finally {
            free(con);
        }

        return true;
    }

    public synchronized boolean insertOrUpdateXmp(File imageFile, Xmp xmp) {
        Connection con = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            long idFile = findIdImageFile(con, imageFile);

            if (idFile < 0) {
                return false;
            }

            insertOrUpdateXmp(con, imageFile, idFile, xmp);
            setLastModifiedXmp(imageFile,
                               xmp.contains(ColumnXmpLastModified.INSTANCE)
                               ? (Long) xmp.getValue(
                                   ColumnXmpLastModified.INSTANCE)
                               : -1);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(con);
        } finally {
            free(con);
        }

        return true;
    }

    /**
     * Inserts an image file into the databse. If the image already
     * existsValueIn it's data will be updated.
     * <p>
     * Inserts or updates this metadata:
     *
     * <ul>
     * <li>EXIF when {@link ImageFile#isInsertExifIntoDb()} is true</li>
     * <li>XMP when {@link ImageFile#isInsertXmpIntoDb()} is true</li>
     * <li>Thumbnail when {@link ImageFile#isInsertThumbnailIntoDb()} is true
     * </li>
     * </ul>
     *
     * @param  imageFile image
     * @return           true if inserted
     */
    public synchronized boolean insertOrUpdate(ImageFile imageFile) {
        boolean success = false;

        if (exists(imageFile.getFile())) {
            return update(imageFile);
        }

        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            String sqlWithXmpLastModified =
                "INSERT INTO files"
                + " (filename, lastmodified, xmp_lastmodified)"
                + " VALUES (?, ?, ?)";
            String sqlWithoutXmpLastModified = "INSERT INTO files"
                                               + " (filename, lastmodified)"
                                               + " VALUES (?, ?)";

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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return success;
    }

    /**
     * Returns an image file.
     *
     * @param  imgFile image file
     * @return         image file
     */
    public ImageFile getImageFile(File imgFile) {
        ImageFile imageFile = new ImageFile();

        imageFile.setExif(getExifOfImageFile(imgFile));
        imageFile.setFile(imgFile);
        imageFile.setLastmodified(getImageFileLastModified(imgFile));

        Image thumbnail = PersistentThumbnails.getThumbnailOfImageFile(imgFile);

        if (thumbnail != null) {
            imageFile.setThumbnail(thumbnail);
        }

        imageFile.setXmp(getXmpOfImageFile(imgFile));

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
     * <li>Thumbnail when {@link ImageFile#isInsertThumbnailIntoDb()} is true
     * </li>
     * </ul>
     *
     * @param imageFile Bild
     * @return          true bei Erfolg
     */
    public boolean update(ImageFile imageFile) {
        boolean           success = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            String sqlWithXmpLastModified =
                "UPDATE files SET lastmodified = ?, xmp_lastmodified = ?"
                + " WHERE id = ?";
            String sqlWithoutXmpLastModified =
                "UPDATE files SET lastmodified = ? WHERE id = ?";

            stmt = con.prepareStatement(imageFile.isInsertXmpIntoDb()
                                        ? sqlWithXmpLastModified
                                        : sqlWithoutXmpLastModified);

            File imgFile = imageFile.getFile();
            long idFile  = findIdImageFile(con, imgFile);

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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
     * @param  listener progress listener, can stop action via event and receive
     *                  the current filename
     * @return          count of updated thumbnails
     */
    public int updateAllThumbnails(ProgressListener listener) {
        int        updated = 0;
        Connection con     = null;
        Statement  stmt    = null;
        ResultSet  rs      = null;

        try {
            int           filecount     =
                DatabaseStatistics.INSTANCE.getFileCount();
            ProgressEvent progressEvent = new ProgressEvent(this, 0, filecount,
                                              0, "");

            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();

            String sql = "SELECT filename FROM files ORDER BY filename ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            int count = 0;

            notifyProgressListenerStart(listener, progressEvent);

            while (!progressEvent.isStop() && rs.next()) {
                File imgFile = getFile(rs.getString(1));

                updateThumbnailFile(
                    imgFile,
                    PersistentThumbnails.getThumbnailOfImageFile(imgFile));
                updated++;
                progressEvent.setValue(++count);
                progressEvent.setInfo(imgFile);
                notifyProgressListenerPerformed(listener, progressEvent);
            }

            notifyProgressListenerEnd(listener, progressEvent);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
        Connection con = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            updateThumbnailFile(imageFile, thumbnail);

            return true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(con);
        }

        return false;
    }

    private void updateThumbnailFile(File imageFile, Image thumbnail) {
        if (thumbnail != null) {
            PersistentThumbnails.writeThumbnailOfImageFile(thumbnail,
                    imageFile);
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
    public long getImageFileLastModified(File imageFile) {
        long              lastModified = -1;
        Connection        con          = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return lastModified;
    }

    /**
     * Returns whether an file is stored in the database.
     *
     * @param  file file
     * @return      true if existsValueIn
     */
    public boolean exists(File file) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(file));
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Removes an image file from the database.
     *
     * @param files full qualified paths of the images files to delete
     * @return          Count of deleted files
     */
    public int delete(List<File> files) {
        int               countDeleted = 0;
        Connection        con          = null;
        PreparedStatement stmt         = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("DELETE FROM files WHERE filename = ?");

            for (File imageFile : files) {
                stmt.setString(1, getFilePath(imageFile));
                logFiner(stmt);

                int countAffectedRows = stmt.executeUpdate();

                countDeleted += countAffectedRows;

                if (countAffectedRows > 0) {
                    Xmp  xmp  = getXmpOfImageFile(imageFile);
                    Exif exif = getExifOfImageFile(imageFile);

                    PersistentThumbnails.deleteThumbnailOfImageFile(imageFile);
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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return countDeleted;
    }

    /**
     * Löscht aus der Datenbank alle Datensätze mit Bildern, die nicht
     * mehr im Dateisystem existieren.
     *
     * @param listener Listener oder null, falls kein Interesse am Fortschritt.
     *                 {@link ProgressListener#progressEnded(ProgressEvent)}
     *                 liefert ein
     *                 {@link de.elmar_baumann.jpt.event.ProgressEvent}-Objekt,
     *                 das mit
     *                 {@link ProgressEvent#getInfo()}
     *                 ein Int-Objekt liefert mit der Anzahl der gelöschten
     *                 Datensätze.
     *                 {@link de.elmar_baumann.jpt.event.ProgressEvent#isStop()}
     *                 wird ausgewertet (Abbruch des Löschens).
     * @return         Anzahl gelöschter Datensätze
     */
    public int deleteNotExistingImageFiles(ProgressListener listener) {
        int           countDeleted = 0;
        ProgressEvent event        =
            new ProgressEvent(this, 0,
                              DatabaseStatistics.INSTANCE.getFileCount(), 0,
                              null);
        Connection con  = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();

            String sql = "SELECT filename FROM files";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            boolean stop = notifyProgressListenerStart(listener, event);

            while (!stop && rs.next()) {
                File imgFile = getFile(rs.getString(1));

                if (!imgFile.exists()) {
                    Xmp  xmp         = getXmpOfImageFile(imgFile);
                    Exif exif        = getExifOfImageFile(imgFile);
                    int  deletedRows = deleteRowWithFilename(con, imgFile);

                    countDeleted += deletedRows;

                    if (deletedRows > 0) {
                        PersistentThumbnails.deleteThumbnailOfImageFile(
                            imgFile);
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
                stop = event.isStop();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        event.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, event);

        return countDeleted;
    }

    private long findIdXmpOfIdFile(Connection con, long idFile)
            throws SQLException {
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT id FROM xmp WHERE id_files = ?");
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
    public long getLastModifiedXmp(File imageFile) {
        long              lastModified = -1;
        Connection        con          = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT xmp_lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
    public boolean setLastModifiedXmp(File imageFile, long time) {
        boolean           set  = false;
        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE files SET xmp_lastmodified = ? WHERE filename = ?");
            stmt.setLong(1, time);
            stmt.setString(2, getFilePath(imageFile));
            logFiner(stmt);

            int count = stmt.executeUpdate();

            set = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
               : xmp.contains(ColumnXmpLastModified.INSTANCE)
                 ? (Long) xmp.getValue(ColumnXmpLastModified.INSTANCE)
                 : -1;
    }

    @SuppressWarnings("unchecked")
    private void insertXmp(Connection con, File imageFile, long idImageFile,
                           Xmp xmp)
            throws SQLException {
        if ((xmp != null) &&!xmp.isEmpty()) {
            PreparedStatement stmt = null;

            try {
                stmt = con.prepareStatement(getInsertIntoXmpStatement());
                setXmpValues(stmt, idImageFile, xmp);
                logFiner(stmt);
                stmt.executeUpdate();

                long idXmp = findIdXmpOfIdFile(con, idImageFile);

                if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                    insertXmpDcSubjects(
                        con, idXmp,
                        (List<String>) xmp.getValue(
                            ColumnXmpDcSubjectsSubject.INSTANCE));
                }

                notifyXmpInserted(imageFile, xmp);
            } finally {
                close(stmt);
            }
        }
    }

    private void insertXmpDcSubjects(Connection con, long idXmp,
                                     List<String> dcSubjects)
            throws SQLException {
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

    private void insertXmpDcSubjectsLink(Connection con, long idXmp,
            long idDcSubject)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(
                "INSERT INTO xmp_dc_subject"
                + " (id_xmp, id_dc_subject) VALUES (?, ?)");
            stmt.setLong(1, idXmp);
            stmt.setLong(2, idDcSubject);
            logFiner(stmt);
            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    private Long ensureDcSubjectExists(Connection con, String dcSubject)
            throws SQLException {
        Long idDcSubject = getIdDcSubject(dcSubject);

        if (idDcSubject == null) {
            insertDcSubject(con, dcSubject);
            idDcSubject = getIdDcSubject(dcSubject);
        }

        return idDcSubject;
    }

    private int insertDcSubject(Connection con, String dcSubject)
            throws SQLException {
        PreparedStatement stmt  = null;
        int               count = 0;

        try {
            stmt = con.prepareStatement(
                "INSERT INTO dc_subjects (subject) VALUES (?)");
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
     * Does <em>not</em> check whether it already exists. In that case an
     * {@link SQLException} will be thrown and caught by this method.
     *
     * @param  dcSubject subject
     * @return           true if inserted
     * @see              #existsDcSubject(java.lang.String)
     */
    public boolean insertDcSubject(String dcSubject) {
        boolean    inserted = false;
        Connection con      = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            int count = insertDcSubject(con, dcSubject);

            inserted = count == 1;

            if (inserted) {
                notifyDcSubjectInserted(dcSubject);
            }
        } catch (SQLException ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(con);
        }

        return inserted;
    }

    private String getUpdateXmpStatement() {
        return "UPDATE xmp SET id_files = ?"                // --  1 --
               + ", id_dc_creator = ?"                      // --  2 --
               + ", dc_description = ?"                     // --  3 --
               + ", id_dc_rights = ?"                       // --  4 --
               + ", dc_title = ?"                           // --  5 --
               + ", id_iptc4xmpcore_location = ?"           // --  6 --
               + ", id_photoshop_authorsposition = ?"       // --  7 --
               + ", id_photoshop_captionwriter = ?"         // --  8 --
               + ", id_photoshop_city = ?"                  // --  9 --
               + ", id_photoshop_country = ?"               // -- 10 --
               + ", id_photoshop_credit = ?"                // -- 11 --
               + ", photoshop_headline = ?"                 // -- 12 --
               + ", photoshop_instructions = ?"             // -- 13 --
               + ", id_photoshop_source = ?"                // -- 14 --
               + ", id_photoshop_state = ?"                 // -- 15 --
               + ", photoshop_transmissionReference = ?"    // -- 16 --
               + ", rating = ?"                             // -- 17 --
               + ", iptc4xmpcore_datecreated = ?"           // -- 18 --
               + " WHERE id = ?";                           // -- 19 --
    }

    private String getInsertIntoXmpStatement() {
        return "INSERT INTO xmp (id_files"              // --  1 --
               + ", id_dc_creator"                      // --  2 --
               + ", dc_description"                     // --  3 --
               + ", id_dc_rights"                       // --  4 --
               + ", dc_title"                           // --  5 --
               + ", id_iptc4xmpcore_location"           // --  6 --
               + ", id_photoshop_authorsposition"       // --  7 --
               + ", id_photoshop_captionwriter"         // --  8 --
               + ", id_photoshop_city"                  // --  9 --
               + ", id_photoshop_country"               // -- 10 --
               + ", id_photoshop_credit"                // -- 11 --
               + ", photoshop_headline"                 // -- 12 --
               + ", photoshop_instructions"             // -- 13 --
               + ", id_photoshop_source"                // -- 14 --
               + ", id_photoshop_state"                 // -- 15 --
               + ", photoshop_transmissionReference"    // -- 16 --
               + ", rating"                             // -- 17 --
               + ", iptc4xmpcore_datecreated"           // -- 18 --
               + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
               + ", ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private void setXmpValues(PreparedStatement stmt, long idImageFile, Xmp xmp)
            throws SQLException {
        stmt.setLong(1, idImageFile);
        setLong(
            ensureValueExists(
                "dc_creator", "creator",
                (String) xmp.getValue(ColumnXmpDcCreator.INSTANCE)), stmt, 2);
        setString(xmp.getValue(ColumnXmpDcDescription.INSTANCE), stmt, 3);
        setLong(
            ensureValueExists(
                "dc_rights", "rights",
                (String) xmp.getValue(ColumnXmpDcRights.INSTANCE)), stmt, 4);
        setString(xmp.getValue(ColumnXmpDcTitle.INSTANCE), stmt, 5);
        setLong(
            ensureValueExists(
                "iptc4xmpcore_location", "location", (String) xmp.getValue(
                    ColumnXmpIptc4xmpcoreLocation.INSTANCE)), stmt, 6);
        setLong(
            ensureValueExists(
                "photoshop_authorsposition", "authorsposition",
                (String) xmp.getValue(
                    ColumnXmpPhotoshopAuthorsposition.INSTANCE)), stmt, 7);
        setLong(
            ensureValueExists(
                "photoshop_captionwriter", "captionwriter",
                (String) xmp.getValue(
                    ColumnXmpPhotoshopCaptionwriter.INSTANCE)), stmt, 8);
        setLong(
            ensureValueExists(
                "photoshop_city", "city",
                (String) xmp.getValue(ColumnXmpPhotoshopCity.INSTANCE)), stmt,
                    9);
        setLong(
            ensureValueExists(
                "photoshop_country", "country", (String) xmp.getValue(
                    ColumnXmpPhotoshopCountry.INSTANCE)), stmt, 10);
        setLong(
            ensureValueExists(
                "photoshop_credit", "credit", (String) xmp.getValue(
                    ColumnXmpPhotoshopCredit.INSTANCE)), stmt, 11);
        setString(xmp.getValue(ColumnXmpPhotoshopHeadline.INSTANCE), stmt, 12);
        setString(xmp.getValue(ColumnXmpPhotoshopInstructions.INSTANCE), stmt,
                  13);
        setLong(
            ensureValueExists(
                "photoshop_source", "source", (String) xmp.getValue(
                    ColumnXmpPhotoshopSource.INSTANCE)), stmt, 14);
        setLong(
            ensureValueExists(
                "photoshop_state", "state",
                (String) xmp.getValue(ColumnXmpPhotoshopState.INSTANCE)), stmt,
                    15);
        setString(
            xmp.getValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE),
            stmt, 16);
        setLongMinMax(xmp.getValue(ColumnXmpRating.INSTANCE),
                      ColumnXmpRating.getMinValue(),
                      ColumnXmpRating.getMaxValue(), stmt, 17);
        setString(xmp.getValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE),
                  stmt, 18);
    }

    @SuppressWarnings("unchecked")
    private void insertOrUpdateXmp(Connection con, File imageFile, long idFile,
                                   Xmp xmp)
            throws SQLException {
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

                    if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                        insertXmpDcSubjects(
                            con, idXmp,
                            (List<String>) xmp.getValue(
                                ColumnXmpDcSubjectsSubject.INSTANCE));
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

    private void deleteXmpDcSubjects(Connection con, long idXmp)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(
                "DELETE FROM xmp_dc_subject WHERE id_xmp = ?");
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
        int           countDeleted  = 0;
        ProgressEvent progressEvent =
            new ProgressEvent(this, 0,
                              DatabaseStatistics.INSTANCE.getXmpCount(), 0,
                              null);
        Connection con  = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.createStatement();

            String sql = "SELECT files.filename FROM files, xmp"
                         + " WHERE files.id = xmp.id_files";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            File    imageFile = null;
            boolean abort     = notifyProgressListenerStart(listener,
                                    progressEvent);

            while (!abort && rs.next()) {
                imageFile = getFile(rs.getString(1));

                if (XmpMetadata.getSidecarFile(imageFile) == null) {
                    countDeleted += deleteXmpOfImageFile(con, imageFile);
                }

                progressEvent.setValue(progressEvent.getValue() + 1);
                notifyProgressListenerPerformed(listener, progressEvent);
                abort = progressEvent.isStop();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        progressEvent.setInfo(countDeleted);
        notifyProgressListenerEnd(listener, progressEvent);

        return countDeleted;
    }

    private int deleteXmpOfImageFile(Connection con, File imageFile) {
        int               count = 0;
        PreparedStatement stmt  = null;

        try {
            stmt = con.prepareStatement(
                "DELETE FROM xmp WHERE xmp.id_files in"
                + " (SELECT xmp.id_files FROM xmp, files"
                + " WHERE xmp.id_files = files.id AND files.filename = ?)");
            stmt.setString(1, getFilePath(imageFile));
            logFiner(stmt);
            count = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(stmt);
        }

        return count;
    }

    private String getXmpOfImageFilesStatement(int fileCount) {
        return " SELECT files.filename"                           // --  1 --
               + ", dc_creator.creator"                           // --  2 --
               + ", xmp.dc_description"                           // --  3 --
               + ", dc_rights.rights"                             // --  4 --
               + ", xmp.dc_title"                                 // --  5 --
               + ", iptc4xmpcore_location.location"               // --  6  --
               + ", photoshop_authorsposition.authorsposition"    // --  7 --
               + ", photoshop_captionwriter.captionwriter"        // --  8 --
               + ", photoshop_city.city"                          // -- 9 --
               + ", photoshop_country.country"                    // -- 10 --
               + ", photoshop_credit.credit"                      // -- 11 --
               + ", xmp.photoshop_headline"                       // -- 12 --
               + ", xmp.photoshop_instructions"                   // -- 13 --
               + ", photoshop_source.source"                      // -- 14 --
               + ", photoshop_state.state"                        // -- 15 --
               + ", xmp.photoshop_transmissionReference"          // -- 16 --
               + ", dc_subjects.subject"                          // -- 17 --
               + ", xmp.rating"                                   // -- 18 --
               + ", xmp.iptc4xmpcore_datecreated"                 // -- 19 --
               + " FROM files LEFT JOIN xmp ON files.id = xmp.id_files"
               + " LEFT JOIN dc_creator ON xmp.id_dc_creator = dc_creator.id"
               + " LEFT JOIN dc_rights ON xmp.id_dc_rights = dc_rights.id"
               + " LEFT JOIN iptc4xmpcore_location"
               + " ON xmp.id_iptc4xmpcore_location = iptc4xmpcore_location.id"
               + " LEFT JOIN photoshop_authorsposition"
               + " ON xmp.id_photoshop_authorsposition"
               + " = photoshop_authorsposition.id"
               + " LEFT JOIN photoshop_captionwriter"
               + " ON xmp.id_photoshop_captionwriter"
               + " = photoshop_captionwriter.id LEFT JOIN photoshop_city"
               + " ON xmp.id_photoshop_city = photoshop_city.id"
               + " LEFT JOIN photoshop_country"
               + " ON xmp.id_photoshop_country = photoshop_country.id"
               + " LEFT JOIN photoshop_credit"
               + " ON xmp.id_photoshop_credit = photoshop_credit.id"
               + " LEFT JOIN photoshop_source"
               + " ON xmp.id_photoshop_source = photoshop_source.id"
               + " LEFT JOIN photoshop_state"
               + " ON xmp.id_photoshop_state = photoshop_state.id"
               + " LEFT JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
               + " LEFT JOIN dc_subjects"
               + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id"
               + " WHERE files.filename IN (" + getPlaceholder(fileCount) + ")";
    }

    /**
     * Returns XMP metadata of image files.
     *
     * @param imageFiles image files
     * @return           XMP metadata where the first element of a pair is the
     *                   image file and the second the XMP metadata of that
     *                   file
     */
    public List<Pair<File, Xmp>> getXmpOfImageFiles(
            Collection<? extends File> imageFiles) {
        List<Pair<File, Xmp>> list = new ArrayList<Pair<File, Xmp>>();
        Connection            con  = null;
        PreparedStatement     stmt = null;
        ResultSet             rs   = null;

        try {
            con = getConnection();

            String sql = getXmpOfImageFilesStatement(imageFiles.size());

            stmt = con.prepareStatement(sql);
            setStrings(stmt, imageFiles.toArray(new File[0]), 1);
            logFinest(stmt);
            rs = stmt.executeQuery();

            String prevFilepath = "";
            Xmp    xmp          = new Xmp();

            while (rs.next()) {
                String filepath = rs.getString(1);

                if (!filepath.equals(prevFilepath)) {
                    xmp = new Xmp();
                }

                xmp.setValue(ColumnXmpDcCreator.INSTANCE, getString(rs, 2));
                xmp.setValue(ColumnXmpDcDescription.INSTANCE, getString(rs, 3));
                xmp.setValue(ColumnXmpDcRights.INSTANCE, getString(rs, 4));
                xmp.setValue(ColumnXmpDcTitle.INSTANCE, getString(rs, 5));
                xmp.setValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                             getString(rs, 6));
                xmp.setValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE,
                             getString(rs, 7));
                xmp.setValue(ColumnXmpPhotoshopCaptionwriter.INSTANCE,
                             getString(rs, 8));
                xmp.setValue(ColumnXmpPhotoshopCity.INSTANCE, getString(rs, 9));
                xmp.setValue(ColumnXmpPhotoshopCountry.INSTANCE,
                             getString(rs, 10));
                xmp.setValue(ColumnXmpPhotoshopCredit.INSTANCE,
                             getString(rs, 11));
                xmp.setValue(ColumnXmpPhotoshopHeadline.INSTANCE,
                             getString(rs, 12));
                xmp.setValue(ColumnXmpPhotoshopInstructions.INSTANCE,
                             getString(rs, 13));
                xmp.setValue(ColumnXmpPhotoshopSource.INSTANCE,
                             getString(rs, 14));
                xmp.setValue(ColumnXmpPhotoshopState.INSTANCE,
                             getString(rs, 15));
                xmp.setValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE,
                             getString(rs, 16));

                String dcSubject = getString(rs, 17);

                if (dcSubject != null) {
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE,
                                 dcSubject);
                }

                xmp.setValue(ColumnXmpRating.INSTANCE,
                             getLongMinMax(rs, 18,
                                           ColumnXmpRating.getMinValue(),
                                           ColumnXmpRating.getMaxValue()));
                xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                             getString(rs, 19));

                if (!filepath.equals(prevFilepath)) {
                    list.add(new Pair<File, Xmp>(getFile(filepath), xmp));
                }

                prevFilepath = filepath;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return list;
    }

    private void setStrings(PreparedStatement stmt, File[] files,
                            int startIndex)
            throws SQLException {
        assert startIndex >= 1 :
               "Invalid SQL statement position: " + startIndex;

        int endIndex    = startIndex + files.length;
        int stringIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            stmt.setString(i, getFilePath(files[stringIndex++]));
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
        return " SELECT dc_creator.creator"                       // --  1 --
               + ", xmp.dc_description"                           // --  2 --
               + ", dc_rights.rights"                             // --  3 --
               + ", xmp.dc_title"                                 // --  4 --
               + ", iptc4xmpcore_location.location"               // --  5  --
               + ", photoshop_authorsposition.authorsposition"    // --  6 --
               + ", photoshop_captionwriter.captionwriter"        // --  7 --
               + ", photoshop_city.city"                          // --  8 --
               + ", photoshop_country.country"                    // -- 9 --
               + ", photoshop_credit.credit"                      // -- 10 --
               + ", xmp.photoshop_headline"                       // -- 11 --
               + ", xmp.photoshop_instructions"                   // -- 12 --
               + ", photoshop_source.source"                      // -- 13 --
               + ", photoshop_state.state"                        // -- 14 --
               + ", xmp.photoshop_transmissionReference"          // -- 15 --
               + ", dc_subjects.subject"                          // -- 16 --
               + ", xmp.rating"                                   // -- 17 --
               + ", xmp.iptc4xmpcore_datecreated"                 // -- 18 --
               + " FROM files INNER JOIN xmp ON files.id = xmp.id_files"
               + " LEFT JOIN dc_creator ON xmp.id_dc_creator = dc_creator.id"
               + " LEFT JOIN dc_rights ON xmp.id_dc_rights = dc_rights.id"
               + " LEFT JOIN iptc4xmpcore_location"
               + " ON xmp.id_iptc4xmpcore_location = iptc4xmpcore_location.id"
               + " LEFT JOIN photoshop_authorsposition"
               + " ON xmp.id_photoshop_authorsposition"
               + " = photoshop_authorsposition.id"
               + " LEFT JOIN photoshop_captionwriter"
               + " ON xmp.id_photoshop_captionwriter"
               + " = photoshop_captionwriter.id LEFT JOIN photoshop_city"
               + " ON xmp.id_photoshop_city = photoshop_city.id"
               + " LEFT JOIN photoshop_country"
               + " ON xmp.id_photoshop_country = photoshop_country.id"
               + " LEFT JOIN photoshop_credit"
               + " ON xmp.id_photoshop_credit = photoshop_credit.id"
               + " LEFT JOIN photoshop_source"
               + " ON xmp.id_photoshop_source = photoshop_source.id"
               + " LEFT JOIN photoshop_state"
               + " ON xmp.id_photoshop_state = photoshop_state.id"
               + " LEFT JOIN xmp_dc_subject ON xmp.id = xmp_dc_subject.id_xmp"
               + " LEFT JOIN dc_subjects"
               + " ON xmp_dc_subject.id_dc_subject = dc_subjects.id"
               + " WHERE files.filename = ?";
    }

    public Xmp getXmpOfImageFile(File imageFile) {
        Xmp               xmp  = new Xmp();
        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(getXmpOfStatement());
            stmt.setString(1, getFilePath(imageFile));
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                xmp.setValue(ColumnXmpDcCreator.INSTANCE, getString(rs, 1));
                xmp.setValue(ColumnXmpDcDescription.INSTANCE, getString(rs, 2));
                xmp.setValue(ColumnXmpDcRights.INSTANCE, getString(rs, 3));
                xmp.setValue(ColumnXmpDcTitle.INSTANCE, getString(rs, 4));
                xmp.setValue(ColumnXmpIptc4xmpcoreLocation.INSTANCE,
                             getString(rs, 5));
                xmp.setValue(ColumnXmpPhotoshopAuthorsposition.INSTANCE,
                             getString(rs, 6));
                xmp.setValue(ColumnXmpPhotoshopCaptionwriter.INSTANCE,
                             getString(rs, 7));
                xmp.setValue(ColumnXmpPhotoshopCity.INSTANCE, getString(rs, 8));
                xmp.setValue(ColumnXmpPhotoshopCountry.INSTANCE,
                             getString(rs, 9));
                xmp.setValue(ColumnXmpPhotoshopCredit.INSTANCE,
                             getString(rs, 10));
                xmp.setValue(ColumnXmpPhotoshopHeadline.INSTANCE,
                             getString(rs, 11));
                xmp.setValue(ColumnXmpPhotoshopInstructions.INSTANCE,
                             getString(rs, 12));
                xmp.setValue(ColumnXmpPhotoshopSource.INSTANCE,
                             getString(rs, 13));
                xmp.setValue(ColumnXmpPhotoshopState.INSTANCE,
                             getString(rs, 14));
                xmp.setValue(ColumnXmpPhotoshopTransmissionReference.INSTANCE,
                             getString(rs, 15));

                String dcSubject = getString(rs, 16);

                if (dcSubject != null) {
                    xmp.setValue(ColumnXmpDcSubjectsSubject.INSTANCE,
                                 dcSubject);
                }

                xmp.setValue(ColumnXmpRating.INSTANCE,
                             getLongMinMax(rs, 17,
                                           ColumnXmpRating.getMinValue(),
                                           ColumnXmpRating.getMaxValue()));
                xmp.setValue(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE,
                             getString(rs, 18));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
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
        Connection  con        = null;
        Statement   stmt       = null;
        ResultSet   rs         = null;

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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return dcSubjects;
    }

    /**
     * Returns all dublin core subjects (keywords) of a image file.
     *
     * @param  imageFile image file
     * @return           dc subjects (keywords) ordered ascending
     */
    public List<String> getDcSubjectsOf(File imageFile) {
        List<String>      dcSubjects = new ArrayList<String>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con = getConnection();

            String sql = "SELECT DISTINCT dc_subjects.subject FROM"
                         + " files INNER JOIN xmp ON files.id = xmp.id_files"
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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return dcSubjects;
    }

    /**
     * Returns the filenames within a specific dublin core subject (keyword).
     *
     * @param  dcSubject subject
     * @param options    options
     * @return           filenames
     */
    public Set<File> getImageFilesOfDcSubject(String dcSubject,
            DcSubjectOption... options) {
        Set<File>            imageFiles = new LinkedHashSet<File>();
        Connection           con        = null;
        PreparedStatement    stmt       = null;
        ResultSet            rs         = null;
        Set<DcSubjectOption> opts       =
            new HashSet<DcSubjectOption>(Arrays.asList(options));

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                getGetFilenamesOfDcSubjectSql(dcSubject, opts));
            setDcSubjectSynonyms(dcSubject, opts, stmt);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return imageFiles;
    }

    private void setDcSubjectSynonyms(String dcSubject,
                                      Set<DcSubjectOption> options,
                                      PreparedStatement stmt)
            throws SQLException {
        stmt.setString(1, dcSubject);

        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            int paramIndex = 2;

            for (String synonym :
                    DatabaseSynonyms.INSTANCE.getSynonymsOf(dcSubject)) {
                stmt.setString(paramIndex++, synonym);
            }
        }
    }

    private String getGetFilenamesOfDcSubjectSql(String dcSubject,
            Set<DcSubjectOption> options) {
        StringBuilder sql =
            new StringBuilder(
                " SELECT DISTINCT files.filename FROM"
                + " dc_subjects INNER JOIN xmp_dc_subject"
                + " ON dc_subjects.id = xmp_dc_subject.id_dc_subject"
                + " INNER JOIN xmp ON xmp_dc_subject.id_xmp = xmp.id"
                + " INNER JOIN files ON xmp.id_files = files.id"
                + " WHERE dc_subjects.subject = ?");

        if (options.contains(DcSubjectOption.INCLUDE_SYNONYMS)) {
            int size =
                DatabaseSynonyms.INSTANCE.getSynonymsOf(dcSubject).size();

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
     * {@link #getImageFilesOfDcSubject(String, DcSubjectOption[])}
     * if You are searching for only one subject.
     *
     * @param  dcSubjects subjects
     * @return            images containing all of these subjects
     */
    public Set<File> getImageFilesOfAllDcSubjects(
            List<? extends String> dcSubjects) {
        Set<File>         imageFiles = new LinkedHashSet<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con = getConnection();

            int    count = dcSubjects.size();
            String sql   =
                " SELECT files.filename FROM"
                + " dc_subjects INNER JOIN xmp_dc_subject"
                + " ON dc_subjects.id = xmp_dc_subject.id_dc_subject"
                + " INNER JOIN xmp ON xmp_dc_subject.id_xmp = xmp.id"
                + " INNER JOIN files ON xmp.id_files = files.id"
                + " WHERE dc_subjects.subject IN "
                + Util.getParamsInParentheses(count)
                + " GROUP BY files.filename HAVING COUNT(*) = " + count;

            stmt = con.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return imageFiles;
    }

    /**
     * Returns all images which have at least one of subjects in a list.
     *
     * Because it's faster, call
     * {@link #getImageFilesOfDcSubject(String, DcSubjectOption[])}
     * if You are searching for only one subject.
     *
     * @param  dcSubjects subjects
     * @return            images containing one or more of these subjects
     */
    public Set<File> getImageFilesOfDcSubjects(
            List<? extends String> dcSubjects) {
        Set<File>         imageFiles = new LinkedHashSet<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con = getConnection();

            int    count = dcSubjects.size();
            String sql   =
                " SELECT DISTINCT files.filename FROM dc_subjects"
                + " INNER JOIN xmp_dc_subject ON dc_subjects.id"
                + " = xmp_dc_subject.id_dc_subject INNER JOIN xmp"
                + " ON xmp_dc_subject.id_xmp = xmp.id INNER JOIN files"
                + " ON xmp.id_files = files.id WHERE dc_subjects.subject IN "
                + Util.getParamsInParentheses(count);

            stmt = con.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
     *                through a column <code>id_files</code>!
     * @return        images containing all of these terms in that column
     */
    public Set<File> getImageFilesOfAll(Column column,
            List<? extends String> words) {
        Set<File>         imageFiles = new LinkedHashSet<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con = getConnection();

            String tableName  = column.getTablename();
            String columnName = column.getName();
            int    count      = words.size();
            String sql        = " SELECT files.filename FROM files "
                                + Join.getJoinToFiles(tableName, Type.INNER)
                                + " WHERE " + tableName + "." + columnName
                                + " IN " + Util.getParamsInParentheses(count)
                                + " GROUP BY files.filename"
                                + " HAVING COUNT(*) = " + count;

            stmt = con.prepareStatement(sql);
            Util.setStringParams(stmt, words, 0);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return imageFiles;
    }

    private String getUpdateExifStatement() {
        return "UPDATE exif SET id_files = ?"           // -- 1 --
               + ", id_exif_recording_equipment = ?"    // -- 2 --
               + ", exif_date_time_original = ?"        // -- 3 --
               + ", exif_focal_length = ?"              // -- 4 --
               + ", exif_iso_speed_ratings = ?"         // -- 5 --
               + ", id_exif_lens = ?"                   // -- 6 --
               + " WHERE id_files = ?";                 // -- 7 --
    }

    private void insertOrUpdateExif(Connection con, File imageFile,
                                    long idFile, Exif exif)
            throws SQLException {
        if (exif != null) {
            long idExif = findIdExifOfIdFile(con, idFile);

            if (idExif > 0) {
                PreparedStatement stmt = null;

                try {
                    Exif oldExif = getExifOfImageFile(imageFile);

                    stmt = con.prepareStatement(getUpdateExifStatement());
                    setExifValues(stmt, idFile, exif);
                    stmt.setLong(7, idFile);
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
        return "INSERT INTO exif (id_files"         // -- 1 --
               + ", id_exif_recording_equipment"    // -- 2 --
               + ", exif_date_time_original"        // -- 3 --
               + ", exif_focal_length"              // -- 4 --
               + ", exif_iso_speed_ratings"         // -- 5 --
               + ", id_exif_lens"                   // -- 6 --
               + ") VALUES (?, ?, ?, ?, ?, ?)";
    }

    private void insertExif(Connection con, File imageFile, long idFile,
                            Exif exif)
            throws SQLException {
        if ((exif != null) &&!exif.isEmpty()) {
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

    private void setExifValues(PreparedStatement stmt, long idFile, Exif exif)
            throws SQLException {
        stmt.setLong(1, idFile);
        setLong(ensureValueExists("exif_recording_equipment", "equipment",
                                  exif.getRecordingEquipment()), stmt, 2);
        setDate(exif.getDateTimeOriginal(), stmt, 3);
        setDouble(exif.getFocalLength(), stmt, 4);
        setShort(exif.getIsoSpeedRatings(), stmt, 5);
        setLong(ensureValueExists("exif_lens", "lens", exif.getLens()), stmt,
                6);
    }

    private long findIdExifOfIdFile(Connection con, long idFile)
            throws SQLException {
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT id FROM exif WHERE id_files = ?");
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
        Timeline   timeline = new Timeline();
        Connection con      = null;
        Statement  stmt     = null;
        ResultSet  rs       = null;

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

            addXmpDateCreated(con, timeline);
            timeline.addUnknownNode();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return timeline;
    }

    private void addXmpDateCreated(Connection con, Timeline timeline)
            throws SQLException {
        Statement stmt = null;
        ResultSet rs   = null;
        String    sql  = "SELECT iptc4xmpcore_datecreated FROM xmp"
                         + " WHERE iptc4xmpcore_datecreated IS NOT NULL";

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
    public Set<File> getFilesOf(int year, int month, int day) {
        Set<File>         files = new HashSet<File>();
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            con = getConnection();

            String sql = "SELECT files.filename FROM exif LEFT JOIN files"
                         + " ON exif.id_files = files.id"
                         + " WHERE exif.exif_date_time_original LIKE ?"
                         + " UNION SELECT files.filename"
                         + " FROM xmp LEFT JOIN files"
                         + " ON xmp.id_files = files.id"
                         + " WHERE xmp.iptc4xmpcore_datecreated LIKE ?"
                         + " ORDER BY files.filename ASC";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, getSqlDateString(year, month, day));
            stmt.setString(2, getXmpDateString(year, month, day));
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return files;
    }

    public String getSqlDateString(int year, int month, int day) {
        return String.valueOf(year) + "-" + ((month > 0)
                ? getMonthDayPrefix(month) + String.valueOf(month)
                : "%") + "-" + (((month > 0) && (day > 0))
                                ? getMonthDayPrefix(day) + String.valueOf(day)
                                : "%");
    }

    public String getXmpDateString(int year, int month, int day) {
        return String.valueOf(year) + ((month > 0)
                                       ? "-" + getMonthDayPrefix(month)
                                         + String.valueOf(month)
                                       : "%") + (((month > 0) && (day > 0))
                ? "-" + getMonthDayPrefix(day) + String.valueOf(day)
                : "");
    }

    private static String getMonthDayPrefix(int i) {
        return (i >= 10)
               ? ""
               : "0";
    }

    /**
     * Returns image files without EXIF date time taken or without XMP date
     * created.
     *
     * @return image files
     */
    public List<File> getFilesOfUnknownDate() {
        List<File> files = new ArrayList<File>();
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con = getConnection();

            String sql = "SELECT files.filename"
                         + " FROM exif INNER JOIN files"
                         + " ON exif.id_files = files.id"
                         + " WHERE exif.exif_date_time_original IS NULL"
                         + " UNION SELECT files.filename"
                         + " FROM xmp INNER JOIN files"
                         + " ON xmp.id_files = files.id"
                         + " WHERE xmp.iptc4xmpcore_datecreated IS NULL"
                         + " ORDER BY files.filename ASC"
            ;

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }

            addFilesWithoutExif(files, con);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return files;
    }

    // UNION can cause memory exhausting
    private void addFilesWithoutExif(List<File> files, Connection con)
            throws SQLException {
        String sql = "SELECT files.filename FROM files"
                     + " WHERE files.id NOT IN "
                     + " (SELECT exif.id_files FROM exif)"
                     + " ORDER BY files.filename ASC";
        Statement stmt = null;
        ResultSet rs   = null;

        try {
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } finally {
            close(rs, stmt);
        }
    }

    public Set<String> getAllDistinctValuesOf(Column column) {
        Set<String> values = new LinkedHashSet<String>();
        Connection  con    = null;
        Statement   stmt   = null;
        ResultSet   rs     = null;

        try {
            con = getConnection();

            String sql = "SELECT DISTINCT " + column.getName() + " FROM "
                         + column.getTablename() + " WHERE " + column.getName()
                         + " IS NOT NULL ORDER BY " + column.getName();

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return values;
    }

    private String getFilesNotNullInSql(String tablename, String columnName) {
        boolean isLink = !tablename.equals("xmp") &&!tablename.equals("exif");

        return isLink
               ? Join.getNotNullSqlOf(tablename)
               : "SELECT DISTINCT files.filename FROM " + tablename
                 + " INNER JOIN files ON " + tablename + ".id_files = files.id"
                 + " WHERE " + tablename + "." + columnName + " IS NOT NULL"
                 + " ORDER BY files.filename ASC";
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
        List<File> files = new ArrayList<File>();
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con = getConnection();

            String tablename  = column.getTablename();
            String columnName = column.getName();
            String sql        = getFilesNotNullInSql(tablename, columnName);

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                files.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
    public List<File> getImageFilesWithColumnContent(Column column,
            String exactValue) {
        List<File>        files      = new ArrayList<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        String            tableName  = column.getTablename();
        String            columnName = column.getName();

        try {
            con = getConnection();

            String sql = "SELECT files.filename FROM files "
                         + Join.getJoinToFiles(tableName, Type.INNER)
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
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return files;
    }

    private String getExifOfStatement() {
        return "SELECT exif_recording_equipment.equipment"    // -- 1 --
               + ", exif.exif_date_time_original"             // -- 2 --
               + ", exif.exif_focal_length"                   // -- 3 --
               + ", exif.exif_iso_speed_ratings"              // -- 4 --
               + ", exif_lens.lens"                           // -- 5 --
               + " FROM files INNER JOIN exif ON files.id = exif.id_files"
               + " LEFT JOIN exif_recording_equipment ON"
               + " exif.id_exif_recording_equipment"
               + " = exif_recording_equipment.id LEFT JOIN exif_lens"
               + " ON exif.id_exif_lens = exif_lens.id"
               + " WHERE files.filename = ?";
    }

    /**
     * Returns exif metadata of a specific file.
     *
     * @param  imageFile image file
     * @return           EXIF metadata or null if that image file has no EXIF
     *                   metadata
     */
    public Exif getExifOfImageFile(File imageFile) {
        Exif              exif = null;
        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            con  = getConnection();
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
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exif;
    }

    public boolean existsExifDate(java.sql.Date date) {
        boolean    exists = false;
        Connection con    = null;
        Statement  stmt   = null;
        ResultSet  rs     = null;

        try {
            con = getConnection();

            Calendar cal = Calendar.getInstance();

            cal.setTime(date);

            int    year  = cal.get(Calendar.YEAR);
            int    month = cal.get(Calendar.MONTH) + 1;
            int    day   = cal.get(Calendar.DAY_OF_MONTH);
            String sql   = "SELECT COUNT(*) FROM exif"
                           + " WHERE exif_date_time_original LIKE '" + year
                           + "-" + getMonthDayPrefix(month) + month + "-"
                           + getMonthDayPrefix(day) + day + "%'";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public boolean existsXMPDateCreated(String date) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM xmp"
                         + " WHERE iptc4xmpcore_datecreated = ?";

            stmt = con.prepareStatement(sql);
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
            free(con);
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
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM " + column.getTablename()
                         + " WHERE " + column.getName() + " = ?";

            stmt = con.prepareStatement(sql);
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
            free(con);
        }

        return exists;
    }

    private String getFilenamesWithoutMetadataInSql(String tablename,
            String columnName) {
        boolean isLink = !tablename.equals("xmp") &&!tablename.equals("exif");

        return isLink
               ? Join.getNullSqlOf(tablename)
               : "SELECT files.filename FROM files INNER JOIN " + tablename
                 + " ON files.id = " + tablename + ".id_files WHERE "
                 + tablename + "." + columnName + " IS NULL"
                 + " UNION SELECT files.filename FROM files "
                 + Join.getUnjoinedFilesSqlWhere(tablename);
    }

    /**
     * Returns the names of files without specific metadata.
     *
     * @param   column column where it's table has to be either table "exif"
     *                 or table <code>"xmp"</code>
     * @return         image files without metadata for that column
     */
    public List<File> getImageFilesWithoutMetadataIn(Column column) {
        List<File>        imageFiles = new ArrayList<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con = getConnection();

            String columnName = column.getName();
            String tablename  = column.getTablename();
            String sql        = getFilenamesWithoutMetadataInSql(tablename,
                                    columnName);

            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
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
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT id FROM files WHERE filename = ?");
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

    /**
     * Returns all thumbnail files.
     *
     * @return files
     */
    public Set<File> getAllThumbnailFiles() {
        Set<File>  tnFiles = new HashSet<File>();
        Connection con     = null;
        Statement  stmt    = null;
        ResultSet  rs      = null;

        try {
            con = getConnection();

            String sql = "SELECT filename FROM files";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            File tnFile = null;

            while (rs.next()) {
                tnFile = PersistentThumbnails.getThumbnailFileOfImageFile(
                    getFile(rs.getString(1)));

                if (tnFile != null) {
                    tnFiles.add(tnFile);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return tnFiles;
    }

    public void deleteValueOfJoinedColumn(Column column, String value) {
        String            sql  = Join.getDeleteSql(column.getTablename());
        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(sql);
            stmt.setString(1, value);
            logFiner(stmt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            AppLogger.logSevere(getClass(), ex);
        } finally {
            close(stmt);
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
        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, dcSubject);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();

            if (count > 0) {
                notifyDcSubjectDeleted(dcSubject);
            }
        } catch (SQLException ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
    }

    public boolean existsDcSubject(String subject) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, subject);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) == 1;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public Long getIdDcSubject(String subject) {
        Long              id   = null;
        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT id FROM dc_subjects WHERE subject = ?");
            stmt.setString(1, subject);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return id;
    }

    public boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM xmp_dc_subject"
                + " WHERE id_xmp = ? AND id_dc_subject = ?");
            stmt.setLong(1, idXmp);
            stmt.setLong(2, idDcSubject);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) == 1;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public void addListener(DatabaseImageFilesListener listener) {
        ls.add(listener);
    }

    public void removeListener(DatabaseImageFilesListener listener) {
        ls.remove(listener);
    }

    void notifyImageFileDeleted(File imageFile) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.imageFileDeleted(imageFile);
            }
        }
    }

    private void notifyImageFileInserted(File imageFile) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.imageFileInserted(imageFile);
            }
        }
    }

    private void notifyImageFileRenamed(File oldFile, File newFile) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.imageFileRenamed(oldFile, newFile);
            }
        }
    }

    private void notifyXmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.xmpUpdated(imageFile, oldXmp, updatedXmp);
            }
        }
    }

    private void notifyXmpInserted(File imageFile, Xmp xmp) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.xmpInserted(imageFile, xmp);
            }
        }
    }

    private void notifyXmpDeleted(File imageFile, Xmp xmp) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.xmpDeleted(imageFile, xmp);
            }
        }
    }

    private void notifyExifUpdated(File imageFile, Exif oldExif,
                                   Exif updatedExif) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.exifUpdated(imageFile, oldExif, updatedExif);
            }
        }
    }

    private void notifyExifInserted(File imageFile, Exif eExif) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.exifInserted(imageFile, eExif);
            }
        }
    }

    private void notifyExifDeleted(File imageFile, Exif eExif) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.exifDeleted(imageFile, eExif);
            }
        }
    }

    private void notifyThumbnailUpdated(File imageFile) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.thumbnailUpdated(imageFile);
            }
        }
    }

    private void notifyDcSubjectInserted(String dcSubject) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.dcSubjectInserted(dcSubject);
            }
        }
    }

    private void notifyDcSubjectDeleted(String dcSubject) {
        Set<DatabaseImageFilesListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageFilesListener listener : listeners) {
                listener.dcSubjectDeleted(dcSubject);
            }
        }
    }
}
