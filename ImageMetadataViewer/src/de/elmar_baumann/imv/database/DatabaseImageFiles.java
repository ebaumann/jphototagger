package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.image.thumbnail.ThumbnailUtil;
import de.elmar_baumann.lib.image.util.ImageUtil;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.ImageIcon;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public class DatabaseImageFiles extends Database {

    private static DatabaseImageFiles instance = new DatabaseImageFiles();

    public static DatabaseImageFiles getInstance() {
        return instance;
    }

    private DatabaseImageFiles() {
    }

    private synchronized void insertThumbnail(Connection connection, long idFile, Image thumbnail, String filename) throws SQLException {
        if (thumbnail != null) {
            ByteArrayInputStream inputStream = ImageUtil.getByteArrayInputStream(thumbnail);
            if (inputStream != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE files SET thumbnail = ? WHERE id = ?"); // NOI18N
                preparedStatement.setBinaryStream(1, inputStream, inputStream.available());
                preparedStatement.setLong(2, idFile);
                logStatement(preparedStatement);
                preparedStatement.executeUpdate();
                notifyDatabaseListener(DatabaseAction.Type.ThumbnailUpdated, filename);
                preparedStatement.close();
            }
        }
    }

    private synchronized void insertExif(Connection connection, long idFile, Exif exifData) throws SQLException {
        if (exifData != null) {
            PreparedStatement stmt = connection.prepareStatement(getInsertIntoExifStatement());
            setExifValues(stmt, idFile, exifData);
            logStatement(stmt);
            stmt.executeUpdate();
            stmt.close();
        }
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

    private void setExifValues(PreparedStatement stmt, long idFile, Exif exifData) throws SQLException {
        stmt.setLong(1, idFile);
        stmt.setString(2, exifData.getRecordingEquipment());
        stmt.setDate(3, exifData.getDateTimeOriginal());
        stmt.setDouble(4, exifData.getFocalLength());
        stmt.setShort(5, exifData.getIsoSpeedRatings());
    }

    private synchronized void insertXmp(Connection connection, long idFile, Xmp xmpData) throws SQLException {
        if (xmpData != null) {
            PreparedStatement stmt = connection.prepareStatement(getInsertIntoXmpStatement());
            setXmpValues(stmt, idFile, xmpData);
            logStatement(stmt);
            stmt.executeUpdate();
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            insertXmpDcSubjects(connection, idXmp, xmpData.getDcSubjects());
            insertXmpPhotoshopSupplementalcategories(connection, idXmp, xmpData.getPhotoshopSupplementalCategories());
            stmt.close();
        }
    }

    private void deleteXmp(Connection connection, long idXmp) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "DELETE FROM xmp WHERE id = ?"); // NOI18N
        stmt.setLong(1, idXmp);
        logStatement(stmt);
        int count = stmt.executeUpdate();
        assert count > 0;
        stmt.close();
    }

    private void insertXmpDcSubjects(Connection connection, long idXmp, List<String> dcSubjects) throws SQLException {
        if (dcSubjects != null) {
            insertValues(connection,
                "INSERT INTO xmp_dc_subjects (id_xmp, subject)", idXmp, dcSubjects); // NOI18N
        }
    }

    private void insertXmpPhotoshopSupplementalcategories(Connection connection, long idXmp, List<String> photoshopSupplementalCategories) throws SQLException {
        if (photoshopSupplementalCategories != null) {
            insertValues(connection,
                "INSERT INTO xmp_photoshop_supplementalcategories" + // NOI18N
                " (id_xmp, supplementalcategory)" // NOI18N
                , idXmp, photoshopSupplementalCategories);
        }
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
            ", lastmodified" + // NOI18N -- 19 --
            ")" + // NOI18N
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // NOI18N
    }

    private void setXmpValues(PreparedStatement stmt, long idFile, Xmp xmpData) throws SQLException {
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
        System.out.println(xmpData.getLastModified());
        stmt.setLong(19, xmpData.getLastModified());
    }

    private synchronized void updateXmp(Connection connection, long idFile, Xmp xmpData) throws SQLException {
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

    private synchronized void updateExif(Connection connection, long idFile, Exif exifData) throws SQLException {
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

    private long getIdExifFromIdFile(Connection connection, long idFile) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT id FROM exif WHERE id_files = ?"); // NOI18N
        stmt.setLong(1, idFile);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    private long getIdXmpFromIdFile(Connection connection, long idFile) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT id FROM xmp WHERE id_files = ?"); // NOI18N
        stmt.setLong(1, idFile);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Renames a filename.
     *
     * @param  oldFilename  old filename
     * @param  newFilename  new filename
     * @return count of renamed filenames
     */
    public synchronized int updateRenameImageFilename(String oldFilename, String newFilename) {
        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE files SET filename = ? WHERE filename = ?"); // NOI18N
            stmt.setString(1, newFilename);
            stmt.setString(2, oldFilename);
            logStatement(stmt);
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Aktualisiert ein Bild in der Datenbank.
     *
     * @param imageFileData Bildmetadaten
     * @return              true bei Erfolg
     */
    public synchronized boolean updateImageFile(ImageFile imageFileData) {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE files SET lastmodified = ? WHERE id = ?"); // NOI18N
            String filename = imageFileData.getFilename();
            long idFile = getIdFile(connection, filename);
            stmt.setLong(1, imageFileData.getLastmodified());
            stmt.setLong(2, idFile);
            logStatement(stmt);
            stmt.executeUpdate();
            stmt.close();
            updateThumbnail(connection, idFile, imageFileData.getThumbnail(), imageFileData.getFilename());
            updateXmp(connection, idFile, imageFileData.getXmp());
            updateExif(connection, idFile, imageFileData.getExif());
            connection.commit();
            success = true;
            notifyDatabaseListener(DatabaseAction.Type.ImageFileUpdated, imageFileData);
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return success;
    }

    private synchronized void insertValues(Connection connection, String statement, long id, List<String> values) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(statement +
            " VALUES (?, ?)"); // NOI18N
        for (String value : values) {
            stmt.setLong(1, id);
            stmt.setString(2, value);
            logStatement(stmt);
            stmt.executeUpdate();
        }
        stmt.close();
    }

    /**
     * Updates all thumbnails, reads the files from the file system and creates
     * thumbnails from the files.
     *
     * @param  listener  progress listener, can stop action via event and receive
     * the current filename
     * @return count of updated thumbnails
     */
    public synchronized int updateAllThumbnails(ProgressListener listener) {
        int updated = 0;
        Connection connection = null;
        try {
            int filecount = DatabaseStatistics.getInstance().getFileCount();
            ProgressEvent event = new ProgressEvent(this, 0, filecount, 0, ""); // NOI18N
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT filename FROM files ORDER BY filename ASC"); // NOI18N
            int count = 0;
            notifyProgressListenerStart(listener, event);
            while (!event.isStop() && rs.next()) {
                String filename = rs.getString(1);
                if (updateThumbnail(filename, getThumbnailFromFile(filename))) {
                    updated++;
                }
                event.setValue(++count);
                event.setInfo(filename);
                notifyProgressListenerPerformed(listener, event);
            }
            stmt.close();
            notifyProgressListenerEnd(listener, event);
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return updated;
    }

    private Image getThumbnailFromFile(String filename) {
        UserSettings settings = UserSettings.getInstance();
        int maxTnWidth = settings.getMaxThumbnailWidth();
        boolean useEmbeddedTn = settings.isUseEmbeddedThumbnails();
        File file = new File(filename);
        if (settings.isCreateThumbnailsWithExternalApp()) {
            return ThumbnailUtil.getThumbnailFromExternalApplication(file, settings.getExternalThumbnailCreationCommand(), maxTnWidth);
        } else {
            return ThumbnailUtil.getThumbnail(file, maxTnWidth, useEmbeddedTn);
        }
    }

    /**
     * Aktualisiert das Thumbnail einer Bilddatei.
     *
     * @param filename  Dateiname
     * @param thumbnail Thumbnail
     * @return          true bei Erfolg
     */
    public boolean updateThumbnail(String filename, Image thumbnail) {
        Connection connection = null;
        try {
            connection = getConnection();
            long idFile = getIdFile(connection, filename);
            updateThumbnail(connection, idFile, thumbnail, filename);
            return true;
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return false;
    }

    private synchronized void updateThumbnail(Connection connection, long idFile, Image thumbnail, String filename) throws SQLException {
        if (thumbnail != null) {
            ByteArrayInputStream inputStream = ImageUtil.getByteArrayInputStream(thumbnail);
            if (inputStream != null) {
                PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE files SET thumbnail = ? WHERE id = ?"); // NOI18N
                stmt.setBinaryStream(1, inputStream, inputStream.available());
                stmt.setLong(2, idFile);
                logStatement(stmt);
                stmt.executeUpdate();
                stmt.close();
                notifyDatabaseListener(DatabaseAction.Type.ThumbnailUpdated, filename);
            }
        }
    }

    /**
     * Liefert die Zeit der letzten Modifikation einer Bilddatei.
     *
     * @param filename Name der Bilddatei
     * @return         Zeit in Millisekunden seit 1970 oder -1, wenn die Zeit nicht
     * ermittelt werden konnte
     */
    public long getLastModifiedImageFile(String filename) {
        long lastModified = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT lastmodified FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return lastModified;
    }

    long getLastModifiedImageFile(long idFiles) {
        long lastModified = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT lastmodified FROM files WHERE id = ?"); // NOI18N
            stmt.setLong(1, idFiles);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return lastModified;
    }

    /**
     * Liefert, ob eine Datei in der Datenbank existiert.
     *
     * @param filename Dateiname
     * @return         true, wenn die Datei existiert
     */
    public boolean existsFilename(String filename) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT filename FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = true;
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Liefert für eine Datei ein Thumbnail.
     *
     * @param filename Dateiname
     * @return         Thumbnail oder null, wenn in der Datebank für die Datei
     * keines existiert oder ein Fehler auftrat
     */
    public Image getThumbnail(String filename) {
        Image thumbnail = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT thumbnail FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                InputStream inputStream = rs.getBinaryStream(1);
                if (inputStream != null) {
                    int bytecount = inputStream.available();
                    byte[] bytes = new byte[bytecount];
                    inputStream.read(bytes, 0, bytecount);
                    ImageIcon icon = new ImageIcon(bytes);
                    thumbnail = icon.getImage();
                }
            }
            stmt.close();
        } catch (IOException ex) {
            handleException(ex, Level.SEVERE);
            thumbnail = null;
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            thumbnail = null;
        } finally {
            free(connection);
        }
        return thumbnail;
    }

    private String getXmpOfFileStatement() {
        return " SELECT" + // NOI18N
            " dc_creator" + // NOI18N --  --
            ", xmp.dc_description" + // NOI18N -- 1 --
            ", xmp.dc_rights" + // NOI18N --2  --
            ", xmp.dc_title" + // NOI18N -- 3 --
            ", xmp.iptc4xmpcore_countrycode" + // NOI18N -- 4 --
            ", xmp.iptc4xmpcore_location" + // NOI18N --5  --
            ", xmp.photoshop_authorsposition" + // NOI18N -- 6 --
            ", xmp.photoshop_captionwriter" + // NOI18N -- 7 --
            ", xmp.photoshop_category" + // NOI18N -- 8 --
            ", xmp.photoshop_city" + // NOI18N -- 9 --
            ", xmp.photoshop_country" + // NOI18N -- 10 --
            ", xmp.photoshop_credit" + // NOI18N -- 11 --
            ", xmp.photoshop_headline" + // NOI18N -- 12 --
            ", xmp.photoshop_instructions" + // NOI18N -- 12 --
            ", xmp.photoshop_source" + // NOI18N -- 14 --
            ", xmp.photoshop_state" + // NOI18N -- 15 --
            ", xmp.photoshop_transmissionReference" + // NOI18N -- 16 --
            ", xmp_dc_subjects.subject" + // NOI18N --  --
            ", xmp_photoshop_supplementalcategories.supplementalcategory" + // NOI18N -- 17 --
            ", lastmodified" + // NOI18N -- 18 --
            " FROM" + // NOI18N
            " xmp LEFT JOIN xmp_dc_subjects" + // NOI18N
            " ON xmp.id = xmp_dc_subjects.id_xmp" + // NOI18N
            " LEFT JOIN xmp_photoshop_supplementalcategories" + // NOI18N
            " ON xmp.id = xmp_photoshop_supplementalcategories.id" + // NOI18N
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
            PreparedStatement stmt = connection.prepareStatement(getXmpOfFileStatement());
            stmt.setString(1, filename);
            logStatement(stmt);
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
                xmp.setLastModified(rs.getLong(18));
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
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return xmp;
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
                "SELECT xmp.lastmodified FROM" + // NOI18N
                " xmp LEFT JOIN files ON xmp.id_files = files.id" + // NOI18N
                " WHERE files.filename = ?"); // NOI18N
            stmt.setString(1, imageFilename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastModified = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return lastModified;
    }

    /**
     * Entfernt eine Bilddatei aus der Datenbank.
     *
     * @param filenames Namen der zu löschenden Dateien
     * @return          Anzahl gelöschter Datensätze
     */
    public synchronized int deleteImageFiles(List<String> filenames) {
        int countDeleted = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM files WHERE filename = ?"); // NOI18N
            for (String filename : filenames) {
                stmt.setString(1, filename);
                logStatement(stmt);
                countDeleted += stmt.executeUpdate();
            }
            stmt.close();
            notifyDatabaseListener(DatabaseAction.Type.ImageFilesDeleted, filenames);
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
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
    public synchronized int deleteNotExistingImageFiles(ProgressListener listener) {
        int countDeleted = 0;
        List<String> deletedFiles = new ArrayList<String>();
        ProgressEvent event = new ProgressEvent(this, 0, 0, 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            event.setMaximum(DatabaseStatistics.getInstance().getFileCount());
            Statement stmt = connection.createStatement();
            String query = "SELECT filename FROM files"; // NOI18N
            ResultSet rs = stmt.executeQuery(query);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, event);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                File file = new File(filename);
                if (!file.exists()) {
                    countDeleted += deleteRowWithFilename(connection, filename);
                    deletedFiles.add(filename);
                }
                event.setValue(event.getValue() + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        if (countDeleted > 0) {
            notifyDatabaseListener(DatabaseAction.Type.MaintainanceNotExistingImageFilesDeleted, deletedFiles);
        }
        event.setInfo(new Integer(countDeleted));
        notifyProgressListenerEnd(listener, event);
        return countDeleted;
    }

    /**
     * Deletes XMP-Data of image files when a XMP sidecar file does not
     * exist but in the database is XMP data for this image file.
     * 
     * @param  listener   progress listener
     * @return count of deleted XMP data (one per image file)
     */
    public synchronized int deleteNotExistingXmpData(ProgressListener listener) {
        int countDeleted = 0;
        ProgressEvent event = new ProgressEvent(this, 0, 0, 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            event.setMaximum(DatabaseStatistics.getInstance().getXmpCount());
            Statement stmt = connection.createStatement();
            String query = "SELECT files.filename FROM files" + // NOI18N
                " LEFT JOIN xmp on files.id = xmp.id_files"; // NOI18N
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
            handleException(ex, Level.SEVERE);
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
            logStatement(stmt);
            count = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        }
        return count;
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
    public synchronized int renameInXmpColumns(List<String> filenames, Column xmpColumn, String oldValue, String newValue, ProgressListener listener) {
        int countRenamed = 0;
        String tableName = xmpColumn.getTable().getName();
        String columnName = tableName + "." + xmpColumn.getName(); // NOI18N
        boolean isXmpTable = xmpColumn.getTable().equals(TableXmp.getInstance());
        ProgressEvent event = new ProgressEvent(this, 0, 0, 0, null);
        XmpMetadata meta = new XmpMetadata();
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT DISTINCT files.id, xmp.id" + // NOI18N
                " FROM xmp" + // NOI18N
                (isXmpTable ? "" : ", " + tableName) + // NOI18N
                ", files" + // NOI18N
                (isXmpTable ? "" : " LEFT JOIN xmp ON " + // NOI18N
                tableName + ".id_xmp = xmp.id") + // NOI18N
                " INNER JOIN files ON xmp.id_files = files.id" + // NOI18N
                " WHERE " + columnName + " = ? AND files.filename = ?"); // NOI18N
            stmt.setString(1, oldValue);
            int filecount = filenames.size();
            event.setMaximum(filecount);
            boolean abort = notifyProgressListenerStart(listener, event);
            for (int i = 0; !abort && i < filecount; i++) {
                String filename = filenames.get(i);
                stmt.setString(2, filename);
                logStatement(stmt);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    long idFile = rs.getLong(1);
                    Xmp xmp = getXmpOfFile(filename);
                    xmp.removeValue(xmpColumn, oldValue);
                    if (!newValue.isEmpty()) {
                        xmp.setValue(xmpColumn, newValue);
                    }
                    if (meta.writeMetaDataToSidecarFile(XmpMetadata.suggestSidecarFilename(filename), xmp)) {
                        long idXmp = rs.getLong(2);
                        deleteXmp(connection, idXmp);
                        insertXmp(connection, idFile, xmp);
                        countRenamed++;
                        notifyDatabaseListener(DatabaseAction.Type.XmpUpdated, filename);
                    }
                }
                connection.commit();
                event.setValue(i + 1);
                notifyProgressListenerPerformed(listener, event);
                abort = event.isStop();
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        event.setInfo(new Integer(countRenamed));
        notifyProgressListenerEnd(listener, event);
        return countRenamed;
    }

    private synchronized int deleteRowWithFilename(Connection connection, String filename) {
        int countDeleted = 0;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            logStatement(stmt);
            countDeleted = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        }
        return countDeleted;
    }

    /**
     * Fügt eine Bilddatei hinzu. Existiert die Datei in der Datenbank,
     * werden ihre Daten aktualisiert.
     * 
     * @param imageFileData Bilddaten
     * @return              true bei Erfolg
     */
    synchronized public boolean insertImageFile(ImageFile imageFileData) {
        boolean success = false;
        if (existsFilename(imageFileData.getFilename())) {
            return updateImageFile(imageFileData);
        }
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO files (filename, lastmodified) VALUES (?, ?)"); // NOI18N
            String filename = imageFileData.getFilename();
            preparedStatement.setString(1, filename);
            preparedStatement.setLong(2, imageFileData.getLastmodified());
            logStatement(preparedStatement);
            preparedStatement.executeUpdate();
            long idFile = getIdFile(connection, filename);
            insertThumbnail(connection, idFile, imageFileData.getThumbnail(), imageFileData.getFilename());
            insertXmp(connection, idFile, imageFileData.getXmp());
            insertExif(connection, idFile, imageFileData.getExif());
            connection.commit();
            success = true;
            notifyDatabaseListener(DatabaseAction.Type.ImageFileInserted, imageFileData);
            preparedStatement.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return success;
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
                " SELECT DISTINCT supplementalcategory FROM xmp_photoshop_supplementalcategories" + // NOI18N
                " WHERE supplementalcategory IS NOT NULL" + // NOI18N
                " ORDER BY 1 ASC"); // NOI18N

            while (rs.next()) {
                categories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return categories;
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
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return dcSubjects;
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
            PreparedStatement stmt = connection.prepareStatement(
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
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
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
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM" + // NOI18N
                " iptc_supplemental_categories" + // NOI18N
                ", xmp" + // NOI18N
                ", xmp_photoshop_supplementalcategories" + // NOI18N
                " WHERE" + // NOI18N
                " xmp.photoshop_category = ?" + // NOI18N
                " OR xmp_photoshop_supplementalcategories.supplementalcategory = ?"); // NOI18N

            stmt.setString(1, name);
            stmt.setString(2, name);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
            exists = count > 0;
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return exists;
    }

    long getIdFile(Connection connection, String filename) {
        long id = -1;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM files WHERE filename = ?"); // NOI18N
            stmt.setString(1, filename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        }
        return id;
    }
}
