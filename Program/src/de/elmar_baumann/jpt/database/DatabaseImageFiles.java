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
import de.elmar_baumann.jpt.database.metadata.file.ColumnFilesFilename;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.database.metadata.xmp.TableXmp;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.jpt.types.SubstringPosition;
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
                    "UPDATE files SET filename = ? WHERE filename = ?"); // NOI18N
            stmt.setString(1, newFilename);
            stmt.setString(2, oldFilename);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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

    /**
     * Renames filenames starting with a substring. Usage: Renaming a directory
     * in the filesystem.
     *
     * @param  start    start substring of the old filenames
     * @param  newStart new start substring
     * @return          count of renamed files
     */
    public int updateRenameImageFilenamesStartingWith(
            String start, String newStart) {
        return DatabaseMaintainance.INSTANCE.replaceString(
                ColumnFilesFilename.INSTANCE, start, newStart,
                SubstringPosition.BEGIN);
    }

    private int deleteRowWithFilename(Connection connection, String filename) {
        int countDeleted = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            countDeleted = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        }
        return countDeleted;
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
            String sqlWithXmpLastModified = "INSERT INTO files" + // NOI18N
                    " (filename, lastmodified, xmp_lastmodified)" + // NOI18N
                    " VALUES (?, ?, ?)"; // NOI18N
            String sqlWithoutXmpLastModified = "INSERT INTO files" + // NOI18N
                    " (filename, lastmodified)" + // NOI18N
                    " VALUES (?, ?)"; // NOI18N
            PreparedStatement preparedStatement = connection.prepareStatement(
                    imageFile.isInsertXmpIntoDb()
                    ? sqlWithXmpLastModified
                    : sqlWithoutXmpLastModified);
            String filename = imageFile.getFilename();
            preparedStatement.setString(1, filename);
            preparedStatement.setLong(2, imageFile.getLastmodified());
            if (imageFile.isInsertXmpIntoDb()) {
                preparedStatement.setLong(3, getLastmodifiedXmp(imageFile));
            }
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, preparedStatement.toString());
            preparedStatement.executeUpdate();
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
            preparedStatement.close();
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
            String sqlWithXmpLastModified = "UPDATE files " + // NOI18N
                    "SET lastmodified = ?, xmp_lastmodified = ? WHERE id = ?"; // NOI18N
            String sqlWithoutXmpLastModified = "UPDATE files " + // NOI18N
                    "SET lastmodified = ? WHERE id = ?"; // NOI18N
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
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            stmt.executeUpdate();
            stmt.close();
            if (imageFile.isInsertThumbnailIntoDb()) {
                updateThumbnailFile(PersistentThumbnails.getMd5File(filename), imageFile.getThumbnail());
            }
            if (imageFile.isInsertXmpIntoDb()) {
                updateXmp(connection, idFile, imageFile.getXmp());
            }
            if (imageFile.isInsertExifIntoDb()) {
                updateExif(connection, idFile, imageFile.getExif());
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
            int filecount = DatabaseStatistics.INSTANCE.getFileCount();
            ProgressEvent event = new ProgressEvent(this, 0, filecount, 0, ""); // NOI18N
            connection = getConnection();
            connection.setAutoCommit(true);
            String sql = "SELECT filename FROM files ORDER BY filename ASC"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            notifyProgressListenerStart(listener, event);
            while (!event.isStop() && rs.next()) {
                String filename = rs.getString(1);
                updateThumbnailFile(PersistentThumbnails.getMd5File(filename), getThumbnailFromFile(filename));
                updated++;
                event.setValue(++count);
                event.setInfo(filename);
                notifyProgressListenerPerformed(listener, event);
            }
            stmt.close();
            notifyProgressListenerEnd(listener, event);
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
                    "SELECT lastmodified FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    "SELECT COUNT(*) FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    "DELETE FROM files WHERE filename = ?"); // NOI18N
            for (String filename : filenames) {
                stmt.setString(1, filename);
                ImageFile oldImageFile = new ImageFile();
                oldImageFile.setFilename(filename);
                oldImageFile.setExif(getExifOfFile(filename));
                oldImageFile.setXmp(getXmpOfFile(filename));
                AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
            String sql = "SELECT filename FROM files"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
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
                        notifyDatabaseListener(DatabaseImageEvent.Type.NOT_EXISTING_IMAGEFILES_DELETED, imageFile);
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
                "SELECT id FROM xmp WHERE id_files = ?"); // NOI18N
        stmt.setLong(1, idFile);
        AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    "SELECT xmp_lastmodified FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, imageFilename);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    "UPDATE files SET xmp_lastmodified = ? WHERE filename = ?"); // NOI18N
            stmt.setLong(1, time);
            stmt.setString(2, imageFilename);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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

    private void insertXmp(Connection connection, long idFile, Xmp xmp) throws
            SQLException {

        if (xmp != null && !xmp.isEmpty()) {
            PreparedStatement stmt = connection.prepareStatement(
                    getInsertIntoXmpStatement());
            setXmpValues(stmt, idFile, xmp);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            stmt.executeUpdate();
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            insertXmpDcSubjects(connection, idXmp, xmp.getDcSubjects());
            insertXmpPhotoshopSupplementalcategories(
                    connection, idXmp, xmp.getPhotoshopSupplementalCategories());
            stmt.close();
        }
    }

    private void insertXmpDcSubjects(
            Connection connection, long idXmp, List<String> dcSubjects) throws
            SQLException {

        if (dcSubjects != null) {
            String sql =
                    "INSERT INTO xmp_dc_subjects (id_xmp, subject)"; // NOI18N
            insertValues(
                    connection,
                    sql,
                    idXmp,
                    dcSubjects);
        }
    }

    private void insertXmpPhotoshopSupplementalcategories(
            Connection connection,
            long idXmp,
            List<String> photoshopSupplementalCategories) throws SQLException {

        String sql =
                "INSERT INTO xmp_photoshop_supplementalcategories" + // NOI18N
                " (id_xmp, supplementalcategory)"; // NOI18N
        if (photoshopSupplementalCategories != null) {
            insertValues(
                    connection,
                    sql,
                    idXmp,
                    photoshopSupplementalCategories);
        }
    }

    private void insertValues(
            Connection connection,
            String sql,
            long id,
            List<String> values) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(sql +
                " VALUES (?, ?)"); // NOI18N
        for (String value : values) {
            stmt.setLong(1, id);
            stmt.setString(2, value);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            stmt.executeUpdate();
        }
        stmt.close();
    }

    private String getInsertIntoXmpStatement() {
        return "INSERT INTO xmp " + // NOI18N
                "(" + // NOI18N
                "id_files" + // NOI18N -- 1 --
                ", dc_creator" + // NOI18N -- 2 --
                ", dc_description" + // NOI18N --3  --
                ", dc_rights" + // NOI18N -- 4 --
                ", dc_title" + // NOI18N -- 5 --
                ", iptc4xmpcore_countrycode" + // NOI18N -- 6 --
                ", iptc4xmpcore_location" + // NOI18N -- 7 --
                ", photoshop_authorsposition" + // NOI18N -- 8 --
                ", photoshop_captionwriter" + // NOI18N -- 9 --
                ", photoshop_category" + // NOI18N -- 10 --
                ", photoshop_city" + // NOI18N -- 11 --
                ", photoshop_country" + // NOI18N -- 12 --
                ", photoshop_credit" + // NOI18N -- 13 --
                ", photoshop_headline" + // NOI18N -- 14 --
                ", photoshop_instructions" + // NOI18N -- 15 --
                ", photoshop_source" + // NOI18N -- 16 --
                ", photoshop_state" + // NOI18N -- 17 --
                ", photoshop_transmissionReference" + // NOI18N -- 18 --
                ", rating" + // NOI18N -- 19 --
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // NOI18N
    }

    private void setXmpValues(PreparedStatement stmt, long idFile, Xmp xmp)
            throws SQLException {
        stmt.setLong(1, idFile);
        stmt.setString(2, xmp.getDcCreator());
        stmt.setString(3, xmp.getDcDescription());
        stmt.setString(4, xmp.getDcRights());
        stmt.setString(5, xmp.getDcTitle());
        stmt.setString(6, xmp.getIptc4xmpcoreCountrycode());
        stmt.setString(7, xmp.getIptc4xmpcoreLocation());
        stmt.setString(8, xmp.getPhotoshopAuthorsposition());
        stmt.setString(9, xmp.getPhotoshopCaptionwriter());
        stmt.setString(10, xmp.getPhotoshopCategory());
        stmt.setString(11, xmp.getPhotoshopCity());
        stmt.setString(12, xmp.getPhotoshopCountry());
        stmt.setString(13, xmp.getPhotoshopCredit());
        stmt.setString(14, xmp.getPhotoshopHeadline());
        stmt.setString(15, xmp.getPhotoshopInstructions());
        stmt.setString(16, xmp.getPhotoshopSource());
        stmt.setString(17, xmp.getPhotoshopState());
        stmt.setString(18, xmp.getPhotoshopTransmissionReference());
        Long rating = xmp.getRating();
        if (rating == null || rating < ColumnXmpRating.getMinValue()) {
            stmt.setNull(19, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(19, rating <= ColumnXmpRating.getMaxValue()
                             ? rating
                             : ColumnXmpRating.getMaxValue());
        }
    }

    private void updateXmp(Connection connection, long idFile, Xmp xmp)
            throws SQLException {

        if (xmp != null) {
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            if (idXmp > 0) {
                PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM xmp where id = ?"); // NOI18N
                stmt.setLong(1, idXmp);
                AppLog.logFiner(getClass(), AppLog.USE_STRING, stmt.toString());
                stmt.executeUpdate();
                stmt.close();
            }
            insertXmp(connection, idFile, xmp);
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
        ProgressEvent event = new ProgressEvent(this, 0,
                DatabaseStatistics.INSTANCE.getXmpCount(), 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String sql = "SELECT files.filename" +// NOI18N
                    " FROM files, xmp" + // NOI18N
                    " WHERE files.id = xmp.id_files"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, event);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                if (XmpMetadata.getSidecarFilenameOfImageFileIfExists(filename) == null) {
                    countDeleted += deleteXmpOfFilename(connection, filename);
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
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

    private int deleteXmpOfFilename(Connection connection, String filename) {
        int count = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM xmp WHERE" + // NOI18N
                    " xmp.id_files in" + // NOI18N
                    " (SELECT xmp.id_files FROM xmp, files" + // NOI18N
                    " WHERE xmp.id_files = files.id AND files.filename = ?)"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        }
        return count;
    }

    private String getXmpOfFilesStatement(int fileCount) {
        return " SELECT" + // NOI18N
                " files.filename" + // NOI18N -- 1 --
                ", xmp.dc_creator" + // NOI18N -- 2 --
                ", xmp.dc_description" + // NOI18N -- 3 --
                ", xmp.dc_rights" + // NOI18N -- 4 --
                ", xmp.dc_title" + // NOI18N -- 5 --
                ", xmp.iptc4xmpcore_countrycode" + // NOI18N -- 6 --
                ", xmp.iptc4xmpcore_location" + // NOI18N -- 7  --
                ", xmp.photoshop_authorsposition" + // NOI18N -- 8 --
                ", xmp.photoshop_captionwriter" + // NOI18N -- 9 --
                ", xmp.photoshop_category" + // NOI18N -- 10 --
                ", xmp.photoshop_city" + // NOI18N -- 11 --
                ", xmp.photoshop_country" + // NOI18N -- 12 --
                ", xmp.photoshop_credit" + // NOI18N -- 13 --
                ", xmp.photoshop_headline" + // NOI18N -- 14 --
                ", xmp.photoshop_instructions" + // NOI18N -- 15 --
                ", xmp.photoshop_source" + // NOI18N -- 16 --
                ", xmp.photoshop_state" + // NOI18N -- 17 --
                ", xmp.photoshop_transmissionReference" + // NOI18N -- 18 --
                ", xmp_dc_subjects.subject" + // NOI18N -- 19 --
                ", xmp_photoshop_supplementalcategories.supplementalcategory" + // NOI18N -- 20 --
                ", xmp.rating" + // NOI18N -- 21 --
                " FROM" + // NOI18N
                " files LEFT JOIN xmp" + // NOI18N
                " ON files.id = xmp.id_files" + // NOI18N
                " LEFT JOIN xmp_dc_subjects" + // NOI18N
                " ON xmp.id = xmp_dc_subjects.id_xmp" + // NOI18N
                " LEFT JOIN xmp_photoshop_supplementalcategories" + // NOI18N
                " ON xmp.id = xmp_photoshop_supplementalcategories.id_xmp" + // NOI18N
                " WHERE files.filename IN" + // NOI18N
                " (" + getPlaceholder(fileCount) + ")"; // NOI18N
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
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            String prevFilename = ""; // NOI18N
            Xmp xmp = new Xmp();
            while (rs.next()) {
                String filename = rs.getString(1);
                if (!filename.equals(prevFilename)) {
                    xmp = new Xmp();
                }
                xmp.setDcCreator(rs.getString(2));
                xmp.setDcDescription(rs.getString(3));
                xmp.setDcRights(rs.getString(4));
                xmp.setDcTitle(rs.getString(5));
                xmp.setIptc4xmpcoreCountrycode(rs.getString(6));
                xmp.setIptc4xmpcoreLocation(rs.getString(7));
                xmp.setPhotoshopAuthorsposition(rs.getString(8));
                xmp.setPhotoshopCaptionwriter(rs.getString(9));
                xmp.setPhotoshopCategory(rs.getString(10));
                xmp.setPhotoshopCity(rs.getString(11));
                xmp.setPhotoshopCountry(rs.getString(12));
                xmp.setPhotoshopCredit(rs.getString(13));
                xmp.setPhotoshopHeadline(rs.getString(14));
                xmp.setPhotoshopInstructions(rs.getString(15));
                xmp.setPhotoshopSource(rs.getString(16));
                xmp.setPhotoshopState(rs.getString(17));
                xmp.setPhotoshopTransmissionReference(rs.getString(18));
                String value = rs.getString(19);
                if (value != null) {
                    xmp.addDcSubject(value);
                }
                value = rs.getString(20);
                if (value != null) {
                    xmp.addPhotoshopSupplementalCategory(value);
                }
                long rating = rs.getLong(21);
                xmp.setRating(rs.wasNull() ||
                        rating < ColumnXmpRating.getMinValue()
                              ? null
                              : rating <= ColumnXmpRating.getMaxValue()
                                ? rating
                                : ColumnXmpRating.getMaxValue());
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

        assert startIndex >= 1 : "Invalid SQL statement position: " + startIndex; // NOI18N

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
                      ? ", ?" // NOI18N
                      : "?"); // NOI18N
        }
        return sb.toString();
    }

    private String getXmpOfFileStatement() {
        return " SELECT" + // NOI18N
                " xmp.dc_creator" + // NOI18N -- 1 --
                ", xmp.dc_description" + // NOI18N -- 2 --
                ", xmp.dc_rights" + // NOI18N --3  --
                ", xmp.dc_title" + // NOI18N -- 4 --
                ", xmp.iptc4xmpcore_countrycode" + // NOI18N -- 5 --
                ", xmp.iptc4xmpcore_location" + // NOI18N -- 6  --
                ", xmp.photoshop_authorsposition" + // NOI18N -- 7 --
                ", xmp.photoshop_captionwriter" + // NOI18N -- 8 --
                ", xmp.photoshop_category" + // NOI18N -- 9 --
                ", xmp.photoshop_city" + // NOI18N -- 10 --
                ", xmp.photoshop_country" + // NOI18N -- 11 --
                ", xmp.photoshop_credit" + // NOI18N -- 12 --
                ", xmp.photoshop_headline" + // NOI18N -- 13 --
                ", xmp.photoshop_instructions" + // NOI18N -- 14 --
                ", xmp.photoshop_source" + // NOI18N -- 15 --
                ", xmp.photoshop_state" + // NOI18N -- 16 --
                ", xmp.photoshop_transmissionReference" + // NOI18N -- 17 --
                ", xmp_dc_subjects.subject" + // NOI18N -- 18 --
                ", xmp_photoshop_supplementalcategories.supplementalcategory" + // NOI18N -- 19 --
                ", xmp.rating" + // NOI18N -- 20 --
                " FROM" + // NOI18N
                " files INNER JOIN xmp" + // NOI18N
                " ON files.id = xmp.id_files" + // NOI18N
                " LEFT JOIN xmp_dc_subjects" + // NOI18N
                " ON xmp.id = xmp_dc_subjects.id_xmp" + // NOI18N
                " LEFT JOIN xmp_photoshop_supplementalcategories" + // NOI18N
                " ON xmp.id = xmp_photoshop_supplementalcategories.id_xmp" + // NOI18N
                " WHERE files.filename = ?"; // NOI18N
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
            PreparedStatement stmt = connection.prepareStatement(
                    getXmpOfFileStatement());
            stmt.setString(1, filename);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                xmp.setDcCreator(rs.getString(1));
                xmp.setDcDescription(rs.getString(2));
                xmp.setDcRights(rs.getString(3));
                xmp.setDcTitle(rs.getString(4));
                xmp.setIptc4xmpcoreCountrycode(rs.getString(5));
                xmp.setIptc4xmpcoreLocation(rs.getString(6));
                xmp.setPhotoshopAuthorsposition(rs.getString(7));
                xmp.setPhotoshopCaptionwriter(rs.getString(8));
                xmp.setPhotoshopCategory(rs.getString(9));
                xmp.setPhotoshopCity(rs.getString(10));
                xmp.setPhotoshopCountry(rs.getString(11));
                xmp.setPhotoshopCredit(rs.getString(12));
                xmp.setPhotoshopHeadline(rs.getString(13));
                xmp.setPhotoshopInstructions(rs.getString(14));
                xmp.setPhotoshopSource(rs.getString(15));
                xmp.setPhotoshopState(rs.getString(16));
                xmp.setPhotoshopTransmissionReference(rs.getString(17));
                String value = rs.getString(18);
                if (value != null) {
                    xmp.addDcSubject(value);
                }
                value = rs.getString(19);
                if (value != null) {
                    xmp.addPhotoshopSupplementalCategory(value);
                }
                long rating = rs.getLong(20);
                xmp.setRating(rs.wasNull() ||
                        rating < ColumnXmpRating.getMinValue()
                              ? null
                              : rating <= ColumnXmpRating.getMaxValue()
                                ? rating
                                : ColumnXmpRating.getMaxValue());
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
            ProgressListener listener) {

        int countRenamed = 0;
        int filecount = filenames.size();
        String tableName = xmpColumn.getTable().getName();
        String columnName = tableName + "." + xmpColumn.getName(); // NOI18N
        boolean isXmpTable = xmpColumn.getTable().equals(TableXmp.INSTANCE);
        ProgressEvent event = new ProgressEvent(this, 0, filecount, 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT DISTINCT files.id, xmp.id" + // NOI18N
                    " FROM xmp" + // NOI18N
                    (isXmpTable
                     ? "" // NOI18N
                     : ", " + tableName) + // NOI18N
                    ", files" + // NOI18N
                    (isXmpTable
                     ? "" // NOI18N
                     : " LEFT JOIN xmp ON " + // NOI18N
                    tableName + ".id_xmp = xmp.id") + // NOI18N
                    " INNER JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE " + columnName + " = ? AND files.filename = ?"); // NOI18N
            stmt.setString(1, oldValue);
            boolean abort = notifyProgressListenerStart(listener, event);
            ImageFile imageFile = new ImageFile();
            for (int i = 0; !abort && i < filecount; i++) {
                String filename = filenames.get(i);
                stmt.setString(2, filename);
                AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    long idFile = rs.getLong(1);
                    Xmp xmp = getXmpOfFile(filename);
                    xmp.removeValue(xmpColumn, oldValue);
                    if (!newValue.isEmpty()) {
                        xmp.setValue(xmpColumn, newValue);
                    }
                    if (XmpMetadata.writeMetadataToSidecarFile(
                            XmpMetadata.suggestSidecarFilenameForImageFile(
                            filename), xmp)) {
                        long idXmp = rs.getLong(2);
                        deleteXmp(connection, idXmp);
                        insertXmp(connection, idFile, xmp);
                        countRenamed++;
                        imageFile.setFilename(filename);
                        notifyDatabaseListener(
                                DatabaseImageEvent.Type.XMP_UPDATED, imageFile);
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

    private void deleteXmp(Connection connection, long idXmp) throws
            SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM xmp WHERE id = ?"); // NOI18N
        stmt.setLong(1, idXmp);
        AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
        int count = stmt.executeUpdate();
        assert count > 0;
        stmt.close();
    }

    /**
     * Liefert alle Kategorien.
     *
     * @return Kategorien
     */
    public Set<String> getCategories() {
        Set<String> categories = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    " SELECT DISTINCT photoshop_category FROM xmp" + // NOI18N
                    " WHERE photoshop_category IS NOT NULL" + // NOI18N
                    " UNION ALL" + // NOI18N
                    " SELECT DISTINCT supplementalcategory" + // NOI18N
                    " FROM xmp_photoshop_supplementalcategories" + // NOI18N
                    " WHERE supplementalcategory IS NOT NULL" + // NOI18N
                    " ORDER BY 1 ASC"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                categories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return categories;
    }

    /**
     * Liefert alle Dateien mit bestimmter Kategorie.
     *
     * @param  category Kategorie
     * @return          Dateinamen
     */
    public Set<String> getFilenamesOfCategory(String category) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    " (SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp.photoshop_category = ?)" + // NOI18N
                    " UNION ALL" + // NOI18N
                    " (SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp_photoshop_supplementalcategories LEFT JOIN xmp" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories.id_xmp = xmp.id" + // NOI18N
                    " LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_photoshop_supplementalcategories.supplementalcategory = ?)"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, category);
            stmt.setString(2, category);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String filename = rs.getString(1);
                if (filename != null) { // only categories in rs or only supplem. cat.
                    filenames.add(filename);
                }
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
     * Liefert, ob eine Kategorie existiert.
     *
     * @param  name Name der Kategorie
     * @return      true, wenn existent
     */
    public boolean existsCategory(String name) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*) FROM" + // NOI18N
                    " iptc_supplemental_categories" + // NOI18N
                    ", xmp" + // NOI18N
                    ", xmp_photoshop_supplementalcategories" + // NOI18N
                    " WHERE" + // NOI18N
                    " xmp.photoshop_category = ?" + // NOI18N
                    " OR xmp_photoshop_supplementalcategories.supplementalcategory = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setString(2, name);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
            exists = count > 0;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exists;
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
                    "SELECT DISTINCT subject" + // NOI18N
                    " FROM xmp_dc_subjects" + // NOI18N
                    " ORDER BY 1 ASC"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
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
                    "SELECT DISTINCT xmp_dc_subjects.subject FROM" + // NOI18N
                    " files INNER JOIN xmp ON files.id = xmp.id_files" + // NOI18N
                    " INNER JOIN xmp_dc_subjects" + // NOI18N
                    " ON xmp.id = xmp_dc_subjects.id_xmp" + // NOI18N
                    " WHERE files.filename = ? " + // NOI18N
                    " ORDER BY xmp_dc_subjects.subject ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, filename);
            AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt.toString());
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
                    " SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp_dc_subjects INNER JOIN xmp" + // NOI18N
                    " ON xmp_dc_subjects.id_xmp = xmp.id" + // NOI18N
                    " INNER JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_dc_subjects.subject = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dcSubject);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    " SELECT files.filename FROM" + // NOI18N
                    " xmp_dc_subjects INNER JOIN xmp" + // NOI18N
                    " ON xmp_dc_subjects.id_xmp = xmp.id" + // NOI18N
                    " INNER JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_dc_subjects.subject IN " + // NOI18N
                    Util.getParamsInParentheses(count) +
                    " GROUP BY files.filename" + // NOI18N
                    " HAVING COUNT(*) = " + count; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    " SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp_dc_subjects INNER JOIN xmp" + // NOI18N
                    " ON xmp_dc_subjects.id_xmp = xmp.id" + // NOI18N
                    " INNER JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_dc_subjects.subject IN " + // NOI18N
                    Util.getParamsInParentheses(count); // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, dcSubjects, 0);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    " SELECT files.filename FROM " + // NOI18N
                    tableName + " INNER JOIN files" + // NOI18N
                    " ON " + tableName + ".id_files = files.id" + // NOI18N
                    " WHERE" + tableName + "." + columnName + " IN " + // NOI18N
                    Util.getParamsInParentheses(count) +
                    " GROUP BY files.filename" + // NOI18N
                    " HAVING COUNT(*) = " + count; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            Util.setStringParams(stmt, words, 0);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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

    private String getInsertIntoExifStatement() {
        return "INSERT INTO exif" + // NOI18N
                " (" + // NOI18N
                "id_files" + // NOI18N -- 1 --
                ", exif_recording_equipment" + // NOI18N -- 2 --
                ", exif_date_time_original" + // NOI18N -- 3 --
                ", exif_focal_length" + // NOI18N -- 4 --
                ", exif_iso_speed_ratings" + // NOI18N -- 5 --
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?)"; // NOI18N
    }

    private void setExifValues(
            PreparedStatement stmt, long idFile, Exif exifData)
            throws SQLException {

        stmt.setLong(1, idFile);
        String recordingEquipment = exifData.getRecordingEquipment();
        if (recordingEquipment == null || recordingEquipment.trim().isEmpty()) {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(2, recordingEquipment);
        }
        Date date = exifData.getDateTimeOriginal();
        if (date == null) {
            stmt.setNull(2, java.sql.Types.DATE);
        } else {
            stmt.setDate(3, date);
        }
        double focalLength = exifData.getFocalLength();
        if (focalLength > 0) {
            stmt.setDouble(4, focalLength);
        } else {
            stmt.setNull(4, java.sql.Types.DOUBLE);
        }
        short iso = exifData.getIsoSpeedRatings();
        if (iso > 0) {
            stmt.setShort(5, iso);
        } else {
            stmt.setNull(5, java.sql.Types.SMALLINT);
        }
    }

    private void updateExif(Connection connection, long idFile, Exif exifData)
            throws SQLException {

        if (exifData != null) {
            long idExif = getIdExifFromIdFile(connection, idFile);
            if (idExif > 0) {
                PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM exif where id = ?"); // NOI18N
                stmt.setLong(1, idExif);
                AppLog.logFiner(getClass(), AppLog.USE_STRING, stmt.toString());
                stmt.executeUpdate();
                stmt.close();
            }
            insertExif(connection, idFile, exifData);
        }
    }

    private void insertExif(Connection connection, long idFile, Exif exifData)
            throws SQLException {

        if (exifData != null && !exifData.isEmpty()) {
            PreparedStatement stmt =
                    connection.prepareStatement(getInsertIntoExifStatement());
            setExifValues(stmt, idFile, exifData);
            AppLog.logFiner(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            stmt.executeUpdate();
            stmt.close();
        }
    }

    private long getIdExifFromIdFile(Connection connection, long idFile) throws
            SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM exif WHERE id_files = ?"); // NOI18N
        stmt.setLong(1, idFile);
        AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                    "SELECT exif_date_time_original" + // NOI18N
                    " FROM exif" + // NOI18N
                    " WHERE exif_date_time_original IS NOT NULL" + // NOI18N
                    " ORDER BY exif_date_time_original ASC"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs.getDate(1));
                timeline.add(cal);
            }
            stmt.close();
            timeline.addUnknownNode();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return timeline;
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
    public List<File> getFilesOf(int year, int month, int day) {
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sqlDate = String.valueOf(year) + "-" + // NOI18N
                    (month > 0
                     ? getMonthDayPrefix(month) + String.valueOf(month)
                     : "%") + // NOI18N
                    "-" + // NOI18N
                    (month > 0 && day > 0
                     ? getMonthDayPrefix(day) + String.valueOf(day)
                     : "%"); // NOI18N
            String sql =
                    "SELECT files.filename" + // NOI18N
                    " FROM exif LEFT JOIN files" + // NOI18N
                    " ON exif.id_files = files.id" + // NOI18N
                    " WHERE exif.exif_date_time_original LIKE ?" + // NOI18N
                    " ORDER BY files.filename ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, sqlDate);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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

    private static String getMonthDayPrefix(int i) {
        return i >= 10
               ? "" // NOI18N
               : "0"; // NOI18N
    }

    /**
     * Returns image files without EXIF date time taken.
     *
     * @return image files
     */
    public List<File> getFilesOfUnknownExifDate() {
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT files.filename" + // NOI18N
                    " FROM exif INNER JOIN files" + // NOI18N
                    " ON exif.id_files = files.id" + // NOI18N
                    " WHERE exif.exif_date_time_original IS NULL" + // NOI18N
                    " ORDER BY files.filename ASC"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
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
                "SELECT files.filename" + // NOI18N
                " FROM files" + // NOI18N
                " WHERE files.id NOT IN " + // NOI18N
                " (SELECT exif.id_files FROM exif)" + // NOI18N
                " ORDER BY files.filename ASC"; // NOI18N
        Statement stmt = connection.createStatement();
        AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
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
                    "SELECT DISTINCT " + // NOI18N
                    column.getName() +
                    " FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + // NOI18N
                    column.getName() +
                    " IS NOT NULL" + // NOI18N
                    " ORDER BY " + // NOI18N
                    column.getName();
            Statement stmt = connection.createStatement();
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
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
                    "SELECT files.filename" + // NOI18N
                    " FROM " + tableName + // NOI18N
                    " INNER JOIN files" + // NOI18N
                    " ON " + tableName + ".id_files = files.id" + // NOI18N
                    " WHERE " + tableName + "." + columnName + // NOI18N
                    " = ?" + // NOI18N
                    " ORDER BY files.filename ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, exactValue);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exif = new Exif();
                exif.setRecordingEquipment(rs.getString(1));
                exif.setDateTimeOriginal(rs.getDate(2));
                exif.setFocalLength(rs.getDouble(3));
                exif.setIsoSpeedRatings(rs.getShort(4));
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
        return "SELECT" + // NOI18N
                " exif_recording_equipment" + // NOI18N -- 1 --
                ", exif.exif_date_time_original" + // NOI18N -- 2 --
                ", exif.exif_focal_length" + // NOI18N -- 3 --
                ", exif.exif_iso_speed_ratings" + // NOI18N -- 4 --
                " FROM files INNER JOIN exif" + // NOI18N
                " ON files.id = exif.id_files" + // NOI18N
                " AND files.filename = ?"; // NOI18N
    }

    public boolean existsExifDay(java.sql.Date date) {
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
                    "SELECT COUNT(*)" + // NOI18N
                    " FROM exif" + // NOI18N
                    " WHERE exif_date_time_original" + // NOI18N
                    " LIKE '" + year + "-" + getMonthDayPrefix(month) + month + // NOI18N
                    "-" + getMonthDayPrefix(day) + day + "%'"; // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
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
                    "SELECT COUNT(*)" + // NOI18N
                    " FROM " + column.getTable().getName() + // NOI18N
                    " WHERE " + column.getName() + // NOI18N
                    " = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
            String sql = "SELECT" + // NOI18N
                    " files.filename" + // NOI18N
                    " FROM" + // NOI18N
                    (tableName.startsWith("exif") // NOI18N
                     ? Join.getSqlFilesExifJoin(Type.LEFT, Arrays.asList(
                    tableName))
                     : Join.getSqlFilesXmpJoin(Type.LEFT, Type.LEFT, Arrays.
                    asList(tableName))) +
                    " WHERE " + tableName + "." + columnName + " IS NULL";  // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
                "exif") || // NOI18N
                column.getTable().getName().startsWith("xmp"); // NOI18N
        assert isExifOrXmpColumn : "Only EXIF or XMP table are valid, not: " + // NOI18N
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
                "SELECT id FROM files WHERE filename = ?"); // NOI18N
        stmt.setString(1, filename);
        AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, stmt.toString());
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
            String sql = "SELECT filename FROM files";  // NOI18N
            Statement stmt = connection.createStatement();
            AppLog.logFinest(DatabaseImageFiles.class, AppLog.USE_STRING, sql);
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
