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
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.cache.PersistentThumbnails;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Timeline;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Join;
import de.elmar_baumann.jpt.database.metadata.Join.Type;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmp;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
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

    private DatabaseImageFiles() {
    }

    /**
     * Renames a file.
     *
     * @param  oldFilename old filename
     * @param  newFilename new filename
     * @return             count of renamed files
     */
    public int updateRenameImageFilename(
            String oldFilename, String newFilename) {

        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE files SET filename = ? WHERE filename = ?");
            stmt.setString(1, newFilename);
            stmt.setString(2, oldFilename);
            logFiner(stmt);
            count = stmt.executeUpdate();
            PersistentThumbnails.updateThumbnailName(oldFilename, newFilename);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    public List<String> getAllImageFiles() {
        List<String> files = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();

            String    sql = "SELECT filename FROM files";
            Statement stmt = connection.createStatement();

            logFinest(sql);

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                files.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }

    private long getFileCountNameStartingWith(Connection connection, String start) throws SQLException {
        long              count = 0;
        String            sql   = "SELECT COUNT(*) FROM files WHERE filename LIKE ?";
        PreparedStatement stmt  = connection.prepareStatement(sql);

        stmt.setString(1, start + "%");
        logFinest(stmt);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            count = rs.getLong(1);
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
    public synchronized int updateRenameImageFilenamesStartingWith(
            final String start, final String newStart, final ProgressListener progressListener) {

        if (start.equals(newStart)) return 0;

              int           countRenamed  = 0;
        final int           startLength   = start.length();
              Connection    connection    = null;
        final ProgressEvent progressEvent = new ProgressEvent(this, 0, 0, 0, null);
        try {
            connection = getConnection();
            connection.setAutoCommit(true);

            String            sql           = "SELECT filename FROM files WHERE filename LIKE ?";
            PreparedStatement stmt          = connection.prepareStatement(sql);

            stmt.setString(1, start + "%");
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();

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
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
            notifyProgressListenerEnd(progressListener, null);
        }

        return countRenamed;
    }

    private void updateFilename(Connection connection, String oldFileName, String newFileName) throws SQLException {

        if (oldFileName.equals(newFileName)) return;

        String            sql  = "UPDATE files SET filename = ? WHERE filename = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setString(1, newFileName);
        stmt.setString(2, oldFileName);
        logFiner(stmt);
        stmt.executeUpdate();
        stmt.close();
        renameThumbnail(oldFileName, newFileName);
    }

    private void renameThumbnail(String oldFileName, String newFileName) {

        File oldTnFile = PersistentThumbnails.getThumbnailFileOfImageFile(oldFileName);

        if (oldTnFile.exists()) {

            File newTnFile = PersistentThumbnails.getThumbnailfile(PersistentThumbnails.getMd5File(newFileName));
            oldTnFile.renameTo(newTnFile);
        }
    }

    private int deleteRowWithFilename(Connection connection, String filename) {
        int countDeleted = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFiner(stmt);
            countDeleted = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        }
        return countDeleted;
    }

    public synchronized boolean insertOrUpdateExif(String filename, Exif exif) {

        Connection connection = null;
        try {
            connection = getConnection();

            long idFile = getIdFile(connection, filename);
            if (idFile < 0) return false;

            Exif oldExif = getExifOfFile(filename);

            insertOrUpdateExif(connection, idFile, exif);

            notifyExifInsertedOrUpdated(filename, oldExif, exif);

        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return true;
    }

    private void notifyExifInsertedOrUpdated(String filename, Exif oldExif, Exif newExif) {

        ImageFile newImageFile    = new ImageFile();
        ImageFile oldImageFile = new ImageFile();

        newImageFile.setExif(newExif);
        newImageFile.setFilename(filename);

        oldImageFile.setExif(oldExif);
        oldImageFile.setFilename(filename);

        notifyDatabaseListener(DatabaseImageEvent.Type.EXIF_UPDATED, oldImageFile, newImageFile);
    }

    public synchronized boolean insertOrUpdateXmp(String filename, Xmp xmp) {

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            long idFile = getIdFile(connection, filename);
            if (idFile < 0) return false;

            Xmp oldXmp = getXmpOfFile(filename);

            insertOrUpdateXmp(connection, idFile, xmp);
            setLastModifiedXmp(filename, xmp.getLastModified());

            notifyXmpInsertedOrUpdated(filename, oldXmp, xmp);

        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
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

        notifyDatabaseListener(DatabaseImageEvent.Type.XMP_UPDATED, oldImageFile, newImageFile);
    }

    /**
     * Inserts an image file into the databse. If the image already exists
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
    public synchronized boolean insertOrUpdateImageFile(ImageFile imageFile) {
        boolean success = false;
        if (existsFilename(imageFile.getFilename())) {
            return updateImageFile(imageFile);
        }
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            String sqlWithXmpLastModified = "INSERT INTO files" +
                    " (filename, lastmodified, xmp_lastmodified)" +
                    " VALUES (?, ?, ?)";
            String sqlWithoutXmpLastModified = "INSERT INTO files" +
                    " (filename, lastmodified)" +
                    " VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(
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
            long idFile = getIdFile(connection, filename);
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(PersistentThumbnails.getMd5File(filename), imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertXmp(connection, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertExif(connection, idFile, imageFile.getExif());
            }
            connection.commit();
            success = true;
            notifyDatabaseListener(DatabaseImageEvent.Type.IMAGEFILE_INSERTED, imageFile);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
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
        imageFile.setExif(getExifOfFile(filename));
        imageFile.setFilename(filename);
        imageFile.setLastmodified(getLastModifiedImageFile(filename));
        imageFile.setThumbnail(PersistentThumbnails.getThumbnail(PersistentThumbnails.getMd5File(filename)));
        imageFile.setXmp(getXmpOfFile(filename));
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
    public boolean updateImageFile(ImageFile imageFile) {
        boolean success = false;
        Connection connection = null;
        try {
            ImageFile oldImageFile = getImageFile(imageFile.getFilename());
            connection = getConnection();
            connection.setAutoCommit(false);
            String sqlWithXmpLastModified = "UPDATE files " +
                    "SET lastmodified = ?, xmp_lastmodified = ? WHERE id = ?";
            String sqlWithoutXmpLastModified = "UPDATE files " +
                    "SET lastmodified = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(
                    imageFile.isInsertXmpIntoDb()
                    ? sqlWithXmpLastModified
                    : sqlWithoutXmpLastModified);
            String filename = imageFile.getFilename();
            long idFile = getIdFile(connection, filename);
            stmt.setLong(1, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                stmt.setLong(2, getLastmodifiedXmp(imageFile));
            }
            stmt.setLong(imageFile.isInsertXmpIntoDb()
                         ? 3
                         : 2, idFile);
            logFiner(stmt);
            stmt.executeUpdate();
            stmt.close();
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(PersistentThumbnails.getMd5File(filename), imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                insertOrUpdateXmp(connection, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                insertOrUpdateExif(connection, idFile, imageFile.getExif());
            }
            connection.commit();
            success = true;
            notifyDatabaseListener(DatabaseImageEvent.Type.IMAGEFILE_UPDATED, oldImageFile, imageFile);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
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
        try {
            int           filecount     = DatabaseStatistics.INSTANCE.getFileCount();
            ProgressEvent progressEvent = new ProgressEvent(this, 0, filecount, 0, "");
            ImageFile     imageFile     = new ImageFile();

            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String sql = "SELECT filename FROM files ORDER BY filename ASC";
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            notifyProgressListenerStart(listener, progressEvent);
            while (!progressEvent.isStop() && rs.next()) {
                String filename = rs.getString(1);
                updateThumbnailFile(PersistentThumbnails.getMd5File(filename), getThumbnailFromFile(filename));
                updated++;
                progressEvent.setValue(++count);
                progressEvent.setInfo(filename);
                notifyProgressListenerPerformed(listener, progressEvent);
                imageFile.setFilename(filename);
                notifyDatabaseListener(DatabaseImageEvent.Type.THUMBNAIL_UPDATED, imageFile);
            }
            stmt.close();
            notifyProgressListenerEnd(listener, progressEvent);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return updated;
    }

    private Image getThumbnailFromFile(String filename) {
        UserSettings settings = UserSettings.INSTANCE;
        int maxTnWidth = settings.getMaxThumbnailLength();
        boolean useEmbeddedTn = settings.isUseEmbeddedThumbnails();
        File file = new File(filename);
        if (settings.isCreateThumbnailsWithExternalApp()) {
            return ThumbnailUtil.getThumbnailFromExternalApplication(
                    file,
                    settings.getExternalThumbnailCreationCommand(),
                    maxTnWidth);
        } else {
            return ThumbnailUtil.getThumbnail(file, maxTnWidth, useEmbeddedTn);
        }
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
            updateThumbnailFile(PersistentThumbnails.getMd5File(filename), thumbnail);
            ImageFile imageFile = new ImageFile();
            imageFile.setFilename(filename);
            notifyDatabaseListener(DatabaseImageEvent.Type.THUMBNAIL_UPDATED, imageFile);
            return true;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return false;
    }

    private void updateThumbnailFile(String hash, Image thumbnail) {
        if (thumbnail != null) {
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
        long lastModified = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return lastModified;
    }

    /**
     * Returns whether an file is stored in the database.
     *
     * @param  filename  filename
     * @return true if exists
     */
    public boolean existsFilename(String filename) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM files WHERE filename = ?");
            stmt.setString(1, filename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
    public int deleteImageFiles(List<String> filenames) {
        int countDeleted = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?");
            for (String filename : filenames) {
                stmt.setString(1, filename);
                ImageFile oldImageFile = new ImageFile();
                oldImageFile.setFilename(filename);
                oldImageFile.setExif(getExifOfFile(filename));
                oldImageFile.setXmp(getXmpOfFile(filename));
                logFiner(stmt);
                int countAffectedRows = stmt.executeUpdate();
                countDeleted += countAffectedRows;
                if (countAffectedRows > 0) {
                    PersistentThumbnails.getThumbnailFileOfImageFile(filename).delete();
                    notifyDatabaseListener(DatabaseImageEvent.Type.IMAGEFILE_DELETED, oldImageFile, oldImageFile);
                }
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
        int countDeleted = 0;
        ProgressEvent event = new ProgressEvent(this, 0,
                DatabaseStatistics.INSTANCE.getFileCount(), 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String sql = "SELECT filename FROM files";
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            String filename;
            boolean stop = notifyProgressListenerStart(listener, event);
            while (!stop && rs.next()) {
                filename = rs.getString(1);
                File file = new File(filename);
                if (!file.exists()) {
                    int deletedRows = deleteRowWithFilename(connection, filename);
                    countDeleted += deletedRows;
                    if (deletedRows > 0) {
                        PersistentThumbnails.getThumbnailFileOfImageFile(filename).delete();
                        ImageFile imageFile = new ImageFile();
                        imageFile.setFilename(filename);
                        notifyDatabaseListener(DatabaseImageEvent.Type.IMAGEFILE_DELETED, imageFile);
                    }
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                stop = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        event.setInfo(new Integer(countDeleted));
        notifyProgressListenerEnd(listener, event);
        return countDeleted;
    }

    private long getIdXmpFromIdFile(Connection connection, long idFile) throws
            SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM xmp WHERE id_files = ?");
        stmt.setLong(1, idFile);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
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
        long lastModified = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT xmp_lastmodified FROM files WHERE filename = ?");
            stmt.setString(1, imageFilename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE files SET xmp_lastmodified = ? WHERE filename = ?");
            stmt.setLong(1, time);
            stmt.setString(2, imageFilename);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            set = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return set;
    }

    private long getLastmodifiedXmp(ImageFile imageFile) {
        Xmp xmp = imageFile.getXmp();
        return xmp == null
               ? -1
               : xmp.getLastModified() == null
                 ? -1
                 : xmp.getLastModified();
    }

    private void insertXmp(Connection connection, long idFile, Xmp xmp) throws SQLException {

        if (xmp != null && !xmp.isEmpty()) {
            PreparedStatement stmt = connection.prepareStatement(getInsertIntoXmpStatement());
            setXmpValues(stmt, idFile, xmp);
            logFiner(stmt);
            stmt.executeUpdate();
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            insertXmpDcSubjects(connection, idXmp, xmp.getDcSubjects());
            stmt.close();
        }
    }

    private void insertXmpDcSubjects(Connection connection, long idXmp, List<String> dcSubjects) throws SQLException {

        if (dcSubjects != null) {
            String sql = "INSERT INTO xmp_dc_subjects (id_xmp, subject)";
            insertValues(
                    connection,
                    sql,
                    idXmp,
                    dcSubjects);
        }
    }

    private void insertValues(
            Connection connection,
            String sql,
            long id,
            List<String> values) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(sql + " VALUES (?, ?)");
        for (String value : values) {
            stmt.setLong(1, id);
            stmt.setString(2, value);
            logFiner(stmt);
            stmt.executeUpdate();
        }
        stmt.close();
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

    private void setXmpValues(PreparedStatement stmt, long idFile, Xmp xmp)
            throws SQLException {
        stmt.setLong(   1, idFile);
        stmt.setString( 2, xmp.getDcCreator());
        stmt.setString( 3, xmp.getDcDescription());
        stmt.setString( 4, xmp.getDcRights());
        stmt.setString( 5, xmp.getDcTitle());
        stmt.setString( 6, xmp.getIptc4XmpCoreCountrycode());
        stmt.setString( 7, xmp.getIptc4XmpCoreLocation());
        stmt.setString( 8, xmp.getPhotoshopAuthorsposition());
        stmt.setString( 9, xmp.getPhotoshopCaptionwriter());
        stmt.setString(10, xmp.getPhotoshopCity());
        stmt.setString(11, xmp.getPhotoshopCountry());
        stmt.setString(12, xmp.getPhotoshopCredit());
        stmt.setString(13, xmp.getPhotoshopHeadline());
        stmt.setString(14, xmp.getPhotoshopInstructions());
        stmt.setString(15, xmp.getPhotoshopSource());
        stmt.setString(16, xmp.getPhotoshopState());
        stmt.setString(17, xmp.getPhotoshopTransmissionReference());
        Long rating = xmp.getRating();
        if (rating == null || rating < ColumnXmpRating.getMinValue()) {
            stmt.setNull(18, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(18, rating <= ColumnXmpRating.getMaxValue()
                             ? rating
                             : ColumnXmpRating.getMaxValue());
        }
        stmt.setString(19, xmp.getIptc4XmpCoreDateCreated());
    }

    private void insertOrUpdateXmp(Connection connection, long idFile, Xmp xmp) throws SQLException {

        if (xmp != null) {
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            if (idXmp > 0) {
                PreparedStatement stmt = connection.prepareStatement(getUpdateXmpStatement());
                setXmpValues(stmt, idFile, xmp);
                stmt.setLong(20, idXmp);
                logFiner(stmt);
                stmt.executeUpdate();
                stmt.close();
                deleteXmpDcSubjects(connection, idXmp);
                insertXmpDcSubjects(connection, idXmp, xmp.getDcSubjects());
            } else {
                insertXmp(connection, idFile, xmp);
            }
        }
    }

    private void deleteXmpDcSubjects(Connection connection, long idXmp) throws SQLException {
        String            sql  = "DELETE FROM xmp_dc_subjects WHERE id_xmp = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);

        stmt.setLong(1, idXmp);
        logFiner(stmt);
        stmt.executeUpdate();
        stmt.close();
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
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String sql = "SELECT files.filename" +
                    " FROM files, xmp" +
                    " WHERE files.id = xmp.id_files";
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, progressEvent);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                if (XmpMetadata.getSidecarFilenameOfImageFileIfExists(filename) == null) {
                    countDeleted += deleteXmpOfFilename(connection, filename);
                }
                progressEvent.setValue(progressEvent.getValue() + 1);
                notifyProgressListenerPerformed(listener, progressEvent);
                abort = progressEvent.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        progressEvent.setInfo(new Integer(countDeleted));
        notifyProgressListenerEnd(listener, progressEvent);
        return countDeleted;
    }

    private int deleteXmpOfFilename(Connection connection, String filename) {
        int count = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM xmp WHERE" +
                    " xmp.id_files in" +
                    " (SELECT xmp.id_files FROM xmp, files" +
                    " WHERE xmp.id_files = files.id AND files.filename = ?)");
            stmt.setString(1, filename);
            logFiner(stmt);
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
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
    public List<Pair<String, Xmp>> getXmpOfFiles(
            Collection<? extends String> filenames) {
        List<Pair<String, Xmp>> list = new ArrayList<Pair<String, Xmp>>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = getXmpOfFilesStatement(filenames.size());
            PreparedStatement stmt = connection.prepareStatement(sql);
            setStrings(stmt, filenames.toArray(new String[0]), 1);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            String prevFilename = "";
            Xmp xmp = new Xmp();
            while (rs.next()) {
                String filename =                      rs.getString(1);
                if (!filename.equals(prevFilename)) {
                    xmp = new Xmp();
                }
                xmp.setDcCreator(                     rs.getString(2));
                xmp.setDcDescription(                 rs.getString(3));
                xmp.setDcRights(                      rs.getString(4));
                xmp.setDcTitle(                       rs.getString(5));
                xmp.setIptc4XmpCoreCountrycode(       rs.getString(6));
                xmp.setIptc4XmpCoreLocation(          rs.getString(7));
                xmp.setPhotoshopAuthorsposition(      rs.getString(8));
                xmp.setPhotoshopCaptionwriter(        rs.getString(9));
                xmp.setPhotoshopCity(                 rs.getString(10));
                xmp.setPhotoshopCountry(              rs.getString(11));
                xmp.setPhotoshopCredit(               rs.getString(12));
                xmp.setPhotoshopHeadline(             rs.getString(13));
                xmp.setPhotoshopInstructions(         rs.getString(14));
                xmp.setPhotoshopSource(               rs.getString(15));
                xmp.setPhotoshopState(                rs.getString(16));
                xmp.setPhotoshopTransmissionReference(rs.getString(17));
                String value =                        rs.getString(18);
                if (value != null) {
                    xmp.addDcSubject(value);
                }
                long rating =                         rs.getLong(19);
                xmp.setRating(rs.wasNull() ||
                        rating < ColumnXmpRating.getMinValue()
                              ? null
                              : rating <= ColumnXmpRating.getMaxValue()
                                ? rating
                                : ColumnXmpRating.getMaxValue());
                xmp.setIptc4XmpCoreDateCreated(rs.getString(20));
                if (!filename.equals(prevFilename)) {
                    list.add(new Pair<String, Xmp>(filename, xmp));
                }
                prevFilename = filename;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return list;
    }

    private void setStrings(
            PreparedStatement stmt,
            String[] strings,
            int startIndex) throws SQLException {

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

    private String getXmpOfFileStatement() {
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
    public Xmp getXmpOfFile(String filename) {
        Xmp xmp = new Xmp();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(getXmpOfFileStatement());
            stmt.setString(1, filename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                xmp.setDcCreator(                      rs.getString(1));
                xmp.setDcDescription(                  rs.getString(2));
                xmp.setDcRights(                       rs.getString(3));
                xmp.setDcTitle(                        rs.getString(4));
                xmp.setIptc4XmpCoreCountrycode(        rs.getString(5));
                xmp.setIptc4XmpCoreLocation(           rs.getString(6));
                xmp.setPhotoshopAuthorsposition(       rs.getString(7));
                xmp.setPhotoshopCaptionwriter(         rs.getString(8));
                xmp.setPhotoshopCity(                  rs.getString(9));
                xmp.setPhotoshopCountry(              rs.getString(10));
                xmp.setPhotoshopCredit(               rs.getString(11));
                xmp.setPhotoshopHeadline(             rs.getString(12));
                xmp.setPhotoshopInstructions(         rs.getString(13));
                xmp.setPhotoshopSource(               rs.getString(14));
                xmp.setPhotoshopState(                rs.getString(15));
                xmp.setPhotoshopTransmissionReference(rs.getString(16));
                String value =                        rs.getString(17);
                if (value != null) {
                    xmp.addDcSubject(value);
                }
                long rating = rs.getLong(18);
                xmp.setRating(rs.wasNull() ||
                        rating < ColumnXmpRating.getMinValue()
                              ? null
                              : rating <= ColumnXmpRating.getMaxValue()
                                ? rating
                                : ColumnXmpRating.getMaxValue());
                xmp.setIptc4XmpCoreDateCreated(rs.getString(19));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return xmp;
    }

    /**
     * Ersetzt Strings in XMP-Spalten bestimmter Dateien.
     * Gleichzeitig werden die Sidecarfiles aktualisiert.
     *
     * @param  filenames Dateinamen
     * @param  xmpColumn Spalte
     * @param  oldValue  Alter Wert
     * @param  newValue  Neuer Wert
     * @param  listener  Beobachter oder null.
     * @return           Anzahl umbenannter Strings
     */
    public int renameXmpMetadata(
            List<String> filenames,
            Column xmpColumn,
            String oldValue,
            String newValue,
            ProgressListener listener
            ) {
        int           countRenamed = 0;
        int           filecount    = filenames.size();
        String        tableName    = xmpColumn.getTable().getName();
        String        columnName   = tableName + "." + xmpColumn.getName();
        boolean       isXmpTable   = xmpColumn.getTable().equals(TableXmp.INSTANCE);
        ProgressEvent event        = new ProgressEvent(this, 0, filecount, 0, null);
        Connection    connection   = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT DISTINCT files.id, xmp.id" +
                    " FROM xmp" +
                    (isXmpTable
                     ? ""
                     : ", " + tableName) +
                    ", files" +
                    (isXmpTable
                     ? ""
                     : " LEFT JOIN xmp ON " +
                    tableName + ".id_xmp = xmp.id") +
                    " INNER JOIN files ON xmp.id_files = files.id" +
                    " WHERE " + columnName + " = ? AND files.filename = ?");
            stmt.setString(1, oldValue);
            boolean abort = notifyProgressListenerStart(listener, event);
            ImageFile imageFile = new ImageFile();
            for (int i = 0; !abort && i < filecount; i++) {
                String filename = filenames.get(i);
                stmt.setString(2, filename);
                logFinest(stmt);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    long idFile = rs.getLong(1);
                    Xmp xmp = getXmpOfFile(filename);
                    xmp.removeValue(xmpColumn, oldValue);
                    if (!newValue.isEmpty()) {
                        xmp.setValue(xmpColumn, newValue);
                    }
                    String sidecarFilename = XmpMetadata.suggestSidecarFilenameForImageFile(filename);
                    if (XmpMetadata.writeMetadataToSidecarFile(sidecarFilename, xmp)) {
                        updateLastModifiedXmp(connection, idFile, new File(sidecarFilename).lastModified());
                        insertOrUpdateXmp(connection, idFile, xmp);
                        countRenamed++;
                        imageFile.setFilename(filename);
                        notifyDatabaseListener(DatabaseImageEvent.Type.XMP_UPDATED, imageFile);
                    }
                }
                connection.commit();
                event.setValue(i + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        event.setInfo(new Integer(countRenamed));
        notifyProgressListenerEnd(listener, event);
        return countRenamed;
    }

    private void updateLastModifiedXmp(Connection connection, long idFile, long lastModifiedXmp) throws SQLException {
        String sql = "UPDATE files set xmp_lastmodified = ? WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, lastModifiedXmp);
        stmt.setLong(2, idFile);
        logFiner(stmt);
        stmt.executeUpdate();
        stmt.close();
    }

    /**
     * Returns the dublin core subjects (keywords).
     *
     * @return dc subjects distinct ordererd ascending
     */
    public Set<String> getDcSubjects() {
        Set<String> dcSubjects = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT DISTINCT subject" +
                    " FROM xmp_dc_subjects" +
                    " ORDER BY 1 ASC";
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
    public List<String> getDcSubjectsOfFile(String filename) {
        List<String> dcSubjects = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT DISTINCT xmp_dc_subjects.subject FROM" +
                    " files INNER JOIN xmp ON files.id = xmp.id_files" +
                    " INNER JOIN xmp_dc_subjects" +
                    " ON xmp.id = xmp_dc_subjects.id_xmp" +
                    " WHERE files.filename = ? " +
                    " ORDER BY xmp_dc_subjects.subject ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return dcSubjects;
    }

    /**
     * Returns the filenames within a specific dublin core subject (keyword).
     *
     * @param  dcSubject subject
     * @return           filenames
     */
    public Set<String> getFilenamesOfDcSubject(String dcSubject) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    " SELECT DISTINCT files.filename FROM" +
                    " xmp_dc_subjects INNER JOIN xmp" +
                    " ON xmp_dc_subjects.id_xmp = xmp.id" +
                    " INNER JOIN files ON xmp.id_files = files.id" +
                    " WHERE xmp_dc_subjects.subject = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dcSubject);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return filenames;
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
    public Set<String> getFilenamesOfAllDcSubjects(
            List<? extends String> dcSubjects) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
    public Set<String> getFilenamesOfDcSubjects(
            List<? extends String> dcSubjects) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
     *                joinable with {@link TableFiles} through a column
     *                <code>id_files</code>!
     * @return        images containing all of these terms in that column
     */
    public Set<String> getFilenamesOfAll(
            Column column, List<? extends String> words) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, words, 0);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
            long idExif = getIdExifFromIdFile(connection, idFile);
            if (idExif > 0) {
                PreparedStatement stmt = connection.prepareStatement(getUpdateExifStatement());
                setExifValues(stmt, idFile, exif);
                stmt.setLong(7, idFile);
                logFiner(stmt);
                stmt.executeUpdate();
                stmt.close();
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
            PreparedStatement stmt =
                    connection.prepareStatement(getInsertIntoExifStatement());
            setExifValues(stmt, idFile, exif);
            logFiner(stmt);
            stmt.executeUpdate();
            stmt.close();
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

    private long getIdExifFromIdFile(Connection connection, long idFile) throws
            SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM exif WHERE id_files = ?");
        stmt.setLong(1, idFile);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
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
        try {
            connection = getConnection();
            String sql =
                    "SELECT exif_date_time_original" +
                    " FROM exif" +
                    " WHERE exif_date_time_original IS NOT NULL" +
                    " ORDER BY exif_date_time_original ASC";
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs.getDate(1));
                timeline.add(cal);
            }
            stmt.close();
            addXmpDateCreated(connection, timeline);
            timeline.addUnknownNode();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return timeline;
    }

    private void addXmpDateCreated(Connection connection, Timeline timeline) throws SQLException {
        String    sql  = "SELECT iptc4xmpcore_datecreated FROM xmp" +
                         " WHERE iptc4xmpcore_datecreated IS NOT NULL";
        Statement stmt = connection.createStatement();

        logFinest(sql);

        ResultSet     rs   = stmt.executeQuery(sql);

        while (rs.next()) {
            Timeline.Date date = new Timeline.Date(-1, -1, -1);
            date.setXmpDateCreated(rs.getString(1));
            if (date.isValid()) {
                timeline.add(date);
            }
        }
        stmt.close();
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, sqlDate(year, month, day));
            stmt.setString(2, xmpDate(year, month, day));
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }

    public String sqlDate(int year, int month, int day) {
        return String.valueOf(year) +
                "-" + (month > 0
                          ? getMonthDayPrefix(month) + String.valueOf(month)
                          : "%") +
                "-" + (month > 0 && day > 0
                          ? getMonthDayPrefix(day) + String.valueOf(day)
                          : "%");
    }

    public String xmpDate(int year, int month, int day) {
        return String.valueOf(year) +
                      (month > 0
                          ? "-" + getMonthDayPrefix(month) + String.valueOf(month)
                          : "%") +
                      (month > 0 && day > 0
                          ? "-" + getMonthDayPrefix(day) + String.valueOf(day)
                          : "");
    }

    private static String getMonthDayPrefix(int i) {
        return i >= 10
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
        Connection connection = null;
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
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
            addFilesWithoutExif(files, connection);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }

    // UNION can cause memory exhausting
    private void addFilesWithoutExif(List<File> files, Connection connection)
            throws SQLException {
        String sql =
                "SELECT files.filename" +
                " FROM files" +
                " WHERE files.id NOT IN " +
                " (SELECT exif.id_files FROM exif)" +
                " ORDER BY files.filename ASC";
        Statement stmt = connection.createStatement();
        logFinest(sql);
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            files.add(new File(rs.getString(1)));
        }
        stmt.close();
    }

    public Set<String> getAllDistinctValues(Column column) {
        Set<String> values = new LinkedHashSet<String>();
        Connection connection = null;
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
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                values.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
    public List<File> getFilesColumnNotNull(Column column) {
        assert !column.isForeignKey() : column;
        List<File> files = new ArrayList<File>();
        Connection connection = null;
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
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, exactValue);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
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
    public Exif getExifOfFile(String filename) {
        Exif exif = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    getExifOfFileStatement());
            stmt.setString(1, filename);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exif = new Exif();
                exif.setRecordingEquipment(rs.getString(1));
                exif.setDateTimeOriginal(rs.getDate(2));
                exif.setFocalLength(rs.getDouble(3));
                exif.setIsoSpeedRatings(rs.getShort(4));
                exif.setLens(rs.getString(5));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exif;
    }

    private String getExifOfFileStatement() {
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
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    public boolean existsXMPDateCreated(String date) {
        boolean    exists     = false;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*)" +
                    " FROM xmp" +
                    " WHERE iptc4xmpcore_datecreated = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, date);
            logFinest(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns wheter a specific value exists in a table.
     *
     * @param  column column of the table, where the value shall exist
     * @param  value  value
     * @return        true if the value exists
     */
    public boolean exists(Column column, Object value) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*)" +
                    " FROM " + column.getTable().getName() +
                    " WHERE " + column.getName() +
                    " = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns the names of files without specific metadata.
     *
     * @param   column column where it's table has to be either {@link TableExif}
     *                 or {@link TableXmp}
     * @return         names of files without metadata for that column
     */
    public List<String> getFilenamesWithoutMetadata(Column column) {
        if (!checkIsExifOrXmpColumn(column)) return new ArrayList<String>();
        List<String> files = new ArrayList<String>();
        Connection connection = null;
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
            PreparedStatement stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }

    private boolean checkIsExifOrXmpColumn(Column column) {
        boolean isExifOrXmpColumn = column.getTable().getName().startsWith(
                "exif") ||
                column.getTable().getName().startsWith("xmp");
        assert isExifOrXmpColumn : "Only EXIF or XMP table are valid, not: " +
                column.getTable();
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
    public long getIdFile(Connection connection, String filename) throws
            SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM files WHERE filename = ?");
        stmt.setString(1, filename);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Returns all thumbnail files.
     *
     * @return files
     */
    public Set<File> getAllThumbnailFiles() {
        Set<File> files = new HashSet<File>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT filename FROM files";
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            File file = null;
            String hash = null;
            while (rs.next()) {
                hash = PersistentThumbnails.getMd5File(rs.getString(1));
                file = PersistentThumbnails.getThumbnailfile(hash);
                files.add(file);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }
}
