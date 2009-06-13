package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Timeline;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import java.awt.Image;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseImageFiles extends Database {

    public static final DatabaseImageFiles INSTANCE = new DatabaseImageFiles();

    private DatabaseImageFiles() {
    }

    /**
     * Returns the id of a filename.
     * 
     * @param  connection  connection
     * @param  filename    filename
     * @return id or -1 if the filename does not exist
     */
    long getIdFile(Connection connection, String filename) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM files WHERE filename = ?"); // NOI18N
        stmt.setString(1, filename);
        AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Renames a file.
     *
     * @param  oldFilename  old filename
     * @param  newFilename  new filename
     * @return count of renamed files
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
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    private int deleteRowWithFilename(Connection connection, String filename) {
        int countDeleted = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
            countDeleted = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        }
        return countDeleted;
    }

    /**
     * Inserts an image file into the databse. If the image already exists
     * it's data will be updated.
     * 
     * @param  imageFile  image
     * @return true if inserted
     */
    public boolean insertImageFile(ImageFile imageFile) {
        boolean success = false;
        if (existsFilename(imageFile.getFilename())) {
            return updateImageFile(imageFile);
        }
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO files (filename, lastmodified, xmp_lastmodified)" + // NOI18N
                    " VALUES (?, ?, ?)"); // NOI18N
            String filename = imageFile.getFilename();
            preparedStatement.setString(1, filename);
            preparedStatement.setLong(2, imageFile.getLastmodified());
            preparedStatement.setLong(3, getLastmodifiedXmp(imageFile));
            AppLog.logFiner(DatabaseImageFiles.class,
                    preparedStatement.toString());
            preparedStatement.executeUpdate();
            long idFile = getIdFile(connection, filename);
            updateThumbnail(idFile, imageFile.getThumbnail());
            insertXmp(connection, idFile, imageFile.getXmp());
            insertExif(connection, idFile, imageFile.getExif());
            connection.commit();
            success = true;
            notifyDatabaseListener(
                    DatabaseAction.Type.IMAGEFILE_INSERTED, imageFile);
            preparedStatement.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return success;
    }

    /**
     * Aktualisiert ein Bild in der Datenbank.
     *
     * @param imageFileData Bildmetadaten
     * @return              true bei Erfolg
     */
    public boolean updateImageFile(ImageFile imageFileData) {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE files " + // NOI18N
                    "SET lastmodified = ?, xmp_lastmodified = ? WHERE id = ?"); // NOI18N
            String filename = imageFileData.getFilename();
            long idFile = getIdFile(connection, filename);
            stmt.setLong(1, imageFileData.getLastmodified());
            stmt.setLong(2, getLastmodifiedXmp(imageFileData));
            stmt.setLong(3, idFile);
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
            stmt.executeUpdate();
            stmt.close();
            updateThumbnail(idFile, imageFileData.getThumbnail());
            updateXmp(connection, idFile, imageFileData.getXmp());
            updateExif(connection, idFile, imageFileData.getExif());
            connection.commit();
            success = true;
            notifyDatabaseListener(
                    DatabaseAction.Type.IMAGEFILE_UPDATED, imageFileData);
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
     * @param  listener  progress listener, can stop action via event and receive
     * the current filename
     * @return count of updated thumbnails
     */
    public int updateAllThumbnails(ProgressListener listener) {
        int updated = 0;
        Connection connection = null;
        try {
            int filecount = DatabaseStatistics.INSTANCE.getFileCount();
            ProgressEvent event = new ProgressEvent(this, 0, filecount, 0, ""); // NOI18N
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT filename FROM files ORDER BY filename ASC"); // NOI18N
            int count = 0;
            notifyProgressListenerStart(listener, event);
            while (!event.isStop() && rs.next()) {
                String filename = rs.getString(1);
                updateThumbnail(getIdFile(connection, filename),
                        getThumbnailFromFile(filename));
                updated++;
                event.setValue(++count);
                event.setInfo(filename);
                notifyProgressListenerPerformed(listener, event);
            }
            stmt.close();
            notifyProgressListenerEnd(listener, event);
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
                    file, settings.getExternalThumbnailCreationCommand(),
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
            long idFile = getIdFile(connection, filename);
            updateThumbnail(idFile, thumbnail);
            return true;
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return false;
    }

    private void updateThumbnail(long idFile, Image thumbnail) {
        if (thumbnail != null) {
            ThumbnailUtil.writeThumbnail(thumbnail, idFile);
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
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns a file's thumbnail.
     *
     * @param  filename  filename
     * @return Thumbnail oder null on errors or if the thumbnail doesn't exist
     */
    public Image getThumbnail(String filename) {
        Image thumbnail = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                thumbnail = ThumbnailUtil.getThumbnail(rs.getLong(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
            thumbnail = null;
        } finally {
            free(connection);
        }
        return thumbnail;
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
                AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
                countDeleted += stmt.executeUpdate();
            }
            stmt.close();
            notifyDatabaseListener(
                    DatabaseAction.Type.IMAGEFILES_DELETED, filenames);
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
     * {@link de.elmar_baumann.imv.event.ProgressListener#progressEnded(de.elmar_baumann.imv.event.ProgressEvent)}
     * liefert ein {@link de.elmar_baumann.imv.event.ProgressEvent}-Objekt,
     * das mit {@link de.elmar_baumann.imv.event.ProgressEvent#getInfo()}
     * ein Int-Objekt liefert mit der Anzahl der gelöschten Datensätze.
     * {@link de.elmar_baumann.imv.event.ProgressEvent#isStop()}
     * wird ausgewertet (Abbruch des Löschens).
     * @return         Anzahl gelöschter Datensätze
     */
    public int deleteNotExistingImageFiles(ProgressListener listener) {
        int countDeleted = 0;
        List<String> deletedFiles = new ArrayList<String>();
        ProgressEvent event = new ProgressEvent(this, 0,
                DatabaseStatistics.INSTANCE.getFileCount(), 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String query = "SELECT filename FROM files"; // NOI18N
            ResultSet rs = stmt.executeQuery(query);
            String filename;
            boolean stop = notifyProgressListenerStart(listener, event);
            while (!stop && rs.next()) {
                filename = rs.getString(1);
                File file = new File(filename);
                if (!file.exists()) {
                    countDeleted += deleteRowWithFilename(connection, filename);
                    deletedFiles.add(filename);
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                stop = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        if (countDeleted > 0) {
            notifyDatabaseListener(
                    DatabaseAction.Type.MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED,
                    deletedFiles);
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
        AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
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
     * @param  imageFilename  <em>image</em> filename (<em>not</em> sidecar
     *                        filename)
     * @return last modification time in milliseconds since 1970 or -1 if
     *         not defined
     */
    public long getLastModifiedXmp(String imageFilename) {
        long lastModified = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT xmp_lastmodified FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, imageFilename);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return lastModified;
    }

    private long getLastmodifiedXmp(ImageFile imageFileData) {
        Xmp xmp = imageFileData.getXmp();
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
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
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
            insertValues(connection,
                    "INSERT INTO xmp_dc_subjects (id_xmp, subject)", // NOI18N
                    idXmp, dcSubjects); // NOI18N
        }
    }

    private void insertXmpPhotoshopSupplementalcategories(
            Connection connection, long idXmp,
            List<String> photoshopSupplementalCategories) throws SQLException {

        if (photoshopSupplementalCategories != null) {
            insertValues(connection,
                    "INSERT INTO xmp_photoshop_supplementalcategories" + // NOI18N
                    " (id_xmp, supplementalcategory)" // NOI18N
                    , idXmp, photoshopSupplementalCategories);
        }
    }

    private void insertValues(Connection connection, String statement, long id,
            List<String> values) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(statement +
                " VALUES (?, ?)"); // NOI18N
        for (String value : values) {
            stmt.setLong(1, id);
            stmt.setString(2, value);
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
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
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // NOI18N
    }

    private void setXmpValues(PreparedStatement stmt, long idFile, Xmp xmpData)
            throws SQLException {
        stmt.setLong(1, idFile);
        stmt.setString(2, xmpData.getDcCreator());
        stmt.setString(3, xmpData.getDcDescription());
        stmt.setString(4, xmpData.getDcRights());
        stmt.setString(5, xmpData.getDcTitle());
        stmt.setString(6, xmpData.getIptc4xmpcoreCountrycode());
        stmt.setString(7, xmpData.getIptc4xmpcoreLocation());
        stmt.setString(8, xmpData.getPhotoshopAuthorsposition());
        stmt.setString(9, xmpData.getPhotoshopCaptionwriter());
        stmt.setString(10, xmpData.getPhotoshopCategory());
        stmt.setString(11, xmpData.getPhotoshopCity());
        stmt.setString(12, xmpData.getPhotoshopCountry());
        stmt.setString(13, xmpData.getPhotoshopCredit());
        stmt.setString(14, xmpData.getPhotoshopHeadline());
        stmt.setString(15, xmpData.getPhotoshopInstructions());
        stmt.setString(16, xmpData.getPhotoshopSource());
        stmt.setString(17, xmpData.getPhotoshopState());
        stmt.setString(18, xmpData.getPhotoshopTransmissionReference());
    }

    private void updateXmp(Connection connection, long idFile, Xmp xmpData)
            throws SQLException {

        if (xmpData != null) {
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            if (idXmp > 0) {
                PreparedStatement stmt = connection.prepareStatement(
                        "DELETE FROM xmp where id = ?"); // NOI18N
                stmt.setLong(1, idXmp);
                stmt.executeUpdate();
                stmt.close();
            }
            insertXmp(connection, idFile, xmpData);
        }
    }

    /**
     * Deletes XMP-Data of image files when a XMP sidecar file does not
     * exist but in the database is XMP data for this image file.
     * 
     * @param  listener   progress listener
     * @return count of deleted XMP data (one per image file)
     */
    public int deleteNotExistingXmpData(ProgressListener listener) {
        int countDeleted = 0;
        ProgressEvent event = new ProgressEvent(this, 0,
                DatabaseStatistics.INSTANCE.getXmpCount(), 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            String query = "SELECT files.filename FROM files," + // NOI18N
                    " xmp WHERE files.id = xmp.id_files"; // NOI18N
            ResultSet rs = stmt.executeQuery(query);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, event);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                if (XmpMetadata.getSidecarFilename(filename) == null) {
                    countDeleted += deleteXmpOfFilename(connection, filename);
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        }
        return count;
    }

    private String getXmpOfFileStatement() {
        return " SELECT" + // NOI18N
                " dc_creator" + // NOI18N -- 1 --
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
                " FROM" + // NOI18N
                " xmp LEFT JOIN xmp_dc_subjects" + // NOI18N
                " ON xmp.id = xmp_dc_subjects.id_xmp" + // NOI18N
                " LEFT JOIN xmp_photoshop_supplementalcategories" + // NOI18N
                " ON xmp.id = xmp_photoshop_supplementalcategories.id_xmp" + // NOI18N
                " INNER JOIN files" + // NOI18N
                " ON xmp.id_files = files.id" + // NOI18N
                " WHERE files.filename = ?"; // NOI18N
    }

    /**
     * Liefert die XMP-Daten einer Datei.
     *
     * @param  filename  Dateiname
     * @return XMP-Daten der Datei
     */
    public Xmp getXmpOfFile(String filename) {
        Xmp xmp = new Xmp();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    getXmpOfFileStatement());
            stmt.setString(1, filename);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
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
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return xmp;
    }

    /**
     * Ersetzt Strings in XMP-Spalten bestimmter Dateien.
     * Gleichzeitig werden die Sidecarfiles aktualisiert.
     *
     * @param  filenames  Dateinamen
     * @param  xmpColumn  Spalte
     * @param  oldValue   Alter Wert
     * @param  newValue   Neuer Wert
     * @param  listener   Beobachter oder null.
     * @return Anzahl umbenannter Strings
     */
    public int renameInXmpColumns(
            List<String> filenames, Column xmpColumn,
            String oldValue, String newValue, ProgressListener listener) {

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
            for (int i = 0; !abort && i < filecount; i++) {
                String filename = filenames.get(i);
                stmt.setString(2, filename);
                AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    long idFile = rs.getLong(1);
                    Xmp xmp = getXmpOfFile(filename);
                    xmp.removeValue(xmpColumn, oldValue);
                    if (!newValue.isEmpty()) {
                        xmp.setValue(xmpColumn, newValue);
                    }
                    if (XmpMetadata.writeMetadataToSidecarFile(
                            XmpMetadata.suggestSidecarFilename(filename), xmp)) {
                        long idXmp = rs.getLong(2);
                        deleteXmp(connection, idXmp);
                        insertXmp(connection, idFile, xmp);
                        countRenamed++;
                        notifyDatabaseListener(
                                DatabaseAction.Type.XMP_UPDATED, filename);
                    }
                }
                connection.commit();
                event.setValue(i + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
        AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
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
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    " SELECT DISTINCT photoshop_category FROM xmp" + // NOI18N
                    " WHERE photoshop_category IS NOT NULL" + // NOI18N
                    " UNION ALL" + // NOI18N
                    " SELECT DISTINCT supplementalcategory" + // NOI18N
                    " FROM xmp_photoshop_supplementalcategories" + // NOI18N
                    " WHERE supplementalcategory IS NOT NULL" + // NOI18N
                    " ORDER BY 1 ASC"); // NOI18N

            while (rs.next()) {
                categories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return categories;
    }

    /**
     * Liefert alle Dateien mit bestimmter Kategorie.
     * 
     * @param  category  Kategorie
     * @return Dateinamen
     */
    public Set<String> getFilenamesOfCategory(String category) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt =
                    connection.prepareStatement(
                    " (SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp.photoshop_category = ?)" + // NOI18N
                    " UNION ALL" + // NOI18N
                    " (SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp_photoshop_supplementalcategories LEFT JOIN xmp" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories.id_xmp = xmp.id" + // NOI18N
                    " LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_photoshop_supplementalcategories.supplementalcategory = ?)"); // NOI18N

            stmt.setString(1, category);
            stmt.setString(2, category);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return filenames;
    }

    /**
     * Liefert, ob eine Kategorie existiert.
     * 
     * @param  name  Name der Kategorie
     * @return true, wenn existent
     */
    public boolean existsCategory(String name) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt =
                    connection.prepareStatement(
                    "SELECT COUNT(*) FROM" + // NOI18N
                    " iptc_supplemental_categories" + // NOI18N
                    ", xmp" + // NOI18N
                    ", xmp_photoshop_supplementalcategories" + // NOI18N
                    " WHERE" + // NOI18N
                    " xmp.photoshop_category = ?" + // NOI18N
                    " OR xmp_photoshop_supplementalcategories.supplementalcategory = ?"); // NOI18N

            stmt.setString(1, name);
            stmt.setString(2, name);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
            exists = count > 0;
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT DISTINCT subject FROM xmp_dc_subjects" + // NOI18N
                    " ORDER BY 1 ASC"); // NOI18N

            while (rs.next()) {
                dcSubjects.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return dcSubjects;
    }

    /**
     * Returns the filenames within a specific dublin core subject (keyword).
     * 
     * @param  dcSubject subject
     * @return filenames
     */
    public Set<String> getFilenamesOfDcSubject(String dcSubject) {
        Set<String> filenames = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    " SELECT DISTINCT files.filename FROM" + // NOI18N
                    " xmp_dc_subjects LEFT JOIN xmp" + // NOI18N
                    " ON xmp_dc_subjects.id_xmp = xmp.id" + // NOI18N
                    " LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                    " WHERE xmp_dc_subjects.subject = ?"); // NOI18N

            stmt.setString(1, dcSubject);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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

    private void setExifValues(PreparedStatement stmt, long idFile,
            Exif exifData) throws SQLException {

        stmt.setLong(1, idFile);
        String recordingEquipment = exifData.getRecordingEquipment();
        if (recordingEquipment == null || recordingEquipment.trim().isEmpty()) {
            stmt.setNull(2, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(2, recordingEquipment);
        }
        stmt.setDate(3, exifData.getDateTimeOriginal());
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
                stmt.executeUpdate();
                stmt.close();
            }
            insertExif(connection, idFile, exifData);
        }
    }

    private void insertExif(Connection connection, long idFile, Exif exifData)
            throws SQLException {

        if (exifData != null && !exifData.isEmpty()) {
            PreparedStatement stmt = connection.prepareStatement(
                    getInsertIntoExifStatement());
            setExifValues(stmt, idFile, exifData);
            AppLog.logFiner(DatabaseImageFiles.class, stmt.toString());
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
        AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
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
     * @return  timeline
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
            AppLog.logFinest(DatabaseImageFiles.class, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs.getDate(1));
                timeline.add(cal);
            }
            stmt.close();
            timeline.addUnknownNode();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
     * @return  image files
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
            AppLog.logFinest(DatabaseImageFiles.class, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
            addFilesWithoutExif(files, connection);
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
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
        AppLog.logFinest(DatabaseImageFiles.class, sql);
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
            AppLog.logFinest(DatabaseImageFiles.class, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                values.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return values;
    }

    public List<File> getFilesFromExif(Column columnExif, String exactValue) {
        List<File> files = new ArrayList<File>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT files.filename" + // NOI18N
                    " FROM exif INNER JOIN files" + // NOI18N
                    " ON exif.id_files = files.id" + // NOI18N
                    " WHERE exif." + // NOI18N
                    columnExif.getName() + // NOI18N
                    " = ?" + // NOI18N
                    " ORDER BY files.filename ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, exactValue);
            AppLog.logFinest(DatabaseImageFiles.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                files.add(new File(rs.getString(1)));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
        } finally {
            free(connection);
        }
        return files;
    }
}
