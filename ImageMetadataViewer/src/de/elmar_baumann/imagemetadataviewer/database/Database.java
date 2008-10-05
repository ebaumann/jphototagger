package de.elmar_baumann.imagemetadataviewer.database;

import de.elmar_baumann.imagemetadataviewer.data.Exif;
import de.elmar_baumann.imagemetadataviewer.data.ImageFile;
import de.elmar_baumann.imagemetadataviewer.data.Xmp;
import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.data.FavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearchPanel;
import de.elmar_baumann.imagemetadataviewer.data.SavedSearchParamStatement;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.AllTables;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.lib.image.util.ImageUtil;
import de.elmar_baumann.imagemetadataviewer.database.metadata.DatabaseMetadataUtil;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Join;
import de.elmar_baumann.imagemetadataviewer.database.metadata.ParamStatement;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.TableXmp;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseAction;
import de.elmar_baumann.imagemetadataviewer.event.DatabaseListener;
import de.elmar_baumann.imagemetadataviewer.event.ErrorEvent;
import de.elmar_baumann.imagemetadataviewer.event.listener.ErrorListeners;
import de.elmar_baumann.imagemetadataviewer.event.ProgressEvent;
import de.elmar_baumann.imagemetadataviewer.event.ProgressListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

// TODO: Hibernate, de.elmar_baumann.imagemetadataviewer.data nutzen
/**
 * Datenbank, die Bildmetadaten speichert.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    private List<DatabaseListener> databaseListener = new ArrayList<DatabaseListener>();
    private static Database instance = new Database();

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static Database getInstance() {
        return instance;
    }

    /**
     * Fügt einen Beobachter hinzu.
     * 
     * @param listener Beobachter
     */
    public void addDatabaseListener(DatabaseListener listener) {
        databaseListener.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener Beobachter
     */
    public void removeDatabaseListener(DatabaseListener listener) {
        databaseListener.remove(listener);
    }

    private void notifyDatabaseListener(DatabaseAction.Type type) {
        DatabaseAction action = new DatabaseAction(type);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    private void notifyDatabaseListener(DatabaseAction.Type type,
            ImageFile imageFileData) {
        DatabaseAction action = new DatabaseAction(type);
        action.setImageFileData(imageFileData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    private void notifyDatabaseListener(DatabaseAction.Type type,
            SavedSearch savedSerachData) {
        DatabaseAction action = new DatabaseAction(type);
        action.setSavedSerachData(savedSerachData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    private void notifyDatabaseListener(DatabaseAction.Type type,
            String filename) {
        DatabaseAction action = new DatabaseAction(type);
        action.setFilename(filename);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    private void notifyDatabaseListener(DatabaseAction.Type type,
            List<String> filenames) {
        DatabaseAction action = new DatabaseAction(type);
        action.setFilenames(filenames);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    private void notifyDatabaseListener(DatabaseAction.Type type,
            String filename, List<String> filenames) {
        DatabaseAction action = new DatabaseAction(type);
        action.setFilename(filename);
        action.setFilenames(filenames);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    /**
     * Returns a connection from the Connection Pool.
     * @return The connection from the pool.
     */
    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    /**
     * Frees a connection in the Connection Pool so it can be reused at a later time.
     * @param The connection to be freed.
     */
    private void free(Connection connection) {
        if (connection != null) {
            try {
                ConnectionPool.getInstance().free(connection);
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
            }
        }
    }

    /**
     * Method for exception logging.
     * 
     * @param ex The exception to log.
     * @param logLevel The log level.
     */
    private void handleException(Exception ex, Level logLevel) {
        Logger.getLogger(Database.class.getName()).log(logLevel, null, ex);
        notifyErrorListener(ex.toString());
    }

    /**
     * Liefert den Inhalt einer ganzen Tabellenspalte.
     * 
     * @param column Tabellenspalte
     * @return Werte DISTINCT
     */
    public LinkedHashSet<String> getContent(Column column) {
        LinkedHashSet<String> content = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String columnName = column.getName();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT DISTINCT " + // NOI18N
                    columnName +
                    " FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + // NOI18N
                    columnName +
                    " IS NOT NULL"); // NOI18N

            while (resultSet.next()) {
                content.add(resultSet.getString(1));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            content.clear();
        } finally {
            free(connection);
        }
        return content;
    }

    /**
     * Liefert den Inhalt von Tabellenspalten.
     * 
     * @param columns Tabellenspalten
     * @return Werte DISTINCT
     */
    public LinkedHashSet<String> getContent(LinkedHashSet<Column> columns) {
        LinkedHashSet<String> content = new LinkedHashSet<String>();
        for (Column column : columns) {
            content.addAll(getContent(column));
        }
        return content;
    }

    /**
     * Liefert Dateinamen anhand eines Statements.
     * 
     * @param paramStatement Korrekt ausgefülltes Statement
     * @return Dateiname
     */
    public List<String> searchFilenames(ParamStatement paramStatement) {
        List<String> filenames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(paramStatement.getSql());
            if (paramStatement.getValues() != null) {
                for (int i = 0; i < paramStatement.getValues().length; i++) {
                    preparedStatement.setObject(i + 1, paramStatement.getValues()[i]);
                }
            }
            logStatement(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                filenames.add(resultSet.getString(1));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            filenames.clear();
        } finally {
            free(connection);
        }
        return filenames;
    }

    /**
     * Liefert alle Dateinamen, der Metadaten bestimmte Suchbegriffe enthalten.
     * Gesucht wird in allen Spalten mit TabelleA.SpalteB LIKE '%Suchbegriff%'
     * OR TabelleC.SpalteD LIKE '%Suchbegriff%' ...
     * 
     * @param searchColumns Spalten, in denen der Suchbegriff vorkommen soll
     * @param searchString  Suchteilzeichenkette
     * @return              Alle gefundenen Dateinamen
     */
    public List<String> searchFilenamesLikeOr(
            List<Column> searchColumns, String searchString) {
        List<String> filenames = new ArrayList<String>();
        addFilenamesSearchFilenamesLikeOr(
                DatabaseMetadataUtil.getTableColumnsOfTableCategory(searchColumns, "xmp"), // NOI18N
                searchString,
                filenames,
                "xmp"); // NOI18N

        addFilenamesSearchFilenamesLikeOr(
                DatabaseMetadataUtil.getTableColumnsOfTableCategory(searchColumns, "exif"), // NOI18N
                searchString,
                filenames,
                "exif"); // NOI18N

        return filenames;
    }

    // TODO Hier definitiv nicht das übergebene Objekt füllen sondern ein neues zurückgeben
    private void addFilenamesSearchFilenamesLikeOr(
            List<Column> searchColumns, String searchString,
            List<String> filenames, String tablename) {
        if (searchColumns.size() > 0) {
            Connection connection = null;
            try {
                connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        getSqlSearchFilenamesLikeOr(searchColumns, tablename));
                for (int i = 0; i < searchColumns.size(); i++) {
                    preparedStatement.setString(i + 1, "%" + searchString + "%"); // NOI18N

                }
                logStatement(preparedStatement);
                ResultSet resultSet = preparedStatement.executeQuery();
                String string;
                while (resultSet.next()) {
                    string = resultSet.getString(1);
                    if (!filenames.contains(string)) {
                        filenames.add(string);
                    }
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
                filenames.clear();
            } finally {
                free(connection);
            }
        }
    }

    private String getSqlSearchFilenamesLikeOr(List<Column> searchColumns,
            String tablename) {
        StringBuffer sql = new StringBuffer("SELECT DISTINCT files.filename FROM "); // NOI18N

        List<String> tablenames = DatabaseMetadataUtil.getUniqueTableNamesOfColumnArray(
                searchColumns);

        sql.append((tablename.equals("xmp") // NOI18N
                ? Join.getSqlFilesXmpJoin(tablenames)
                : Join.getSqlFilesExifJoin(tablenames)) +
                " WHERE "); // NOI18N

        boolean isFirstColumn = true;
        for (Column tableColumn : searchColumns) {
            sql.append((!isFirstColumn ? " OR " : "") + // NOI18N
                    tableColumn.getTable().getName() + "." + // NOI18N
                    tableColumn.getName() + " LIKE ?"); // NOI18N

            isFirstColumn = false;
        }
        sql.append(" ORDER BY files.filename ASC"); // NOI18N

        return sql.toString();
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
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return success;
    }

    synchronized private void insertThumbnail(Connection connection, long idFile, Image thumbnail, String filename) throws
            SQLException {
        if (thumbnail != null) {
            ByteArrayInputStream inputStream =
                    ImageUtil.getByteArrayInputStream(thumbnail);
            if (inputStream != null) {
                PreparedStatement preparedStatement =
                        connection.prepareStatement(
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

    synchronized private void insertExif(Connection connection, long idFile, Exif exifData) throws SQLException {
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
                "id_files" + // NOI18N
                ", exif_recording_equipment" + // NOI18N
                ", exif_date_time_original" + // NOI18N
                ", exif_focal_length" + // NOI18N
                ", exif_iso_speed_ratings" + // NOI18N
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?)"; // NOI18N

    }

    private void logStatement(PreparedStatement stmt) {
        Logger.getLogger(Database.class.getName()).log(Level.FINER, stmt.toString());
    }

    private void setExifValues(PreparedStatement stmt, long idFile,
            Exif exifData) throws SQLException {
        stmt.setLong(1, idFile);
        stmt.setString(2, exifData.getRecordingEquipment());
        stmt.setDate(3, exifData.getDateTimeOriginal());
        stmt.setDouble(4, exifData.getFocalLength());
        stmt.setShort(5, exifData.getIsoSpeedRatings());
    }

    synchronized private void insertXmp(Connection connection, long idFile, Xmp xmpData) throws SQLException {
        if (xmpData != null) {
            PreparedStatement stmt =
                    connection.prepareStatement(getInsertIntoXmpStatement());
            setXmpValues(stmt, idFile, xmpData);
            logStatement(stmt);
            stmt.executeUpdate();
            long idXmp = getIdXmpFromIdFile(connection, idFile);
            insertXmpDcSubjects(connection, idXmp, xmpData.getDcSubjects());
            insertXmpPhotoshopSupplementalcategories(connection, idXmp,
                    xmpData.getPhotoshopSupplementalCategories());
            stmt.close();
        }
    }

    private void deleteXmp(Connection connection, long idXmp) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM xmp WHERE id = ?");
        stmt.setLong(1, idXmp);
        logStatement(stmt);
        int count = stmt.executeUpdate();
        assert count > 0;
        stmt.close();
    }

    private void insertXmpDcSubjects(Connection connection, long idXmp, List<String> dcSubjects)
            throws SQLException {
        if (dcSubjects != null) {
            insertValues(connection,
                    "INSERT INTO xmp_dc_subjects (id_xmp, subject)", idXmp, // NOI18N
                    dcSubjects);
        }
    }

    private void insertXmpPhotoshopSupplementalcategories(
            Connection connection, long idXmp, List<String> photoshopSupplementalCategories)
            throws SQLException {
        if (photoshopSupplementalCategories != null) {
            insertValues(connection,
                    "INSERT INTO xmp_photoshop_supplementalcategories (id_xmp, supplementalcategory)", // NOI18N
                    idXmp, photoshopSupplementalCategories);
        }
    }

    private String getInsertIntoXmpStatement() {
        return "INSERT INTO xmp " + // NOI18N
                "(" + // NOI18N
                "id_files" + // NOI18N -- 1 --
                ", dc_creator" + // NOI18N -- 2 --
                ", dc_description" + // NOI18N -- 3 --
                ", dc_rights" + // NOI18N -- 4 --
                ", dc_title" + // NOI18N -- 5  --
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

    private void setXmpValues(PreparedStatement stmt, long idFile,
            Xmp xmpData) throws SQLException {
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

    synchronized private void updateXmp(Connection connection, long idFile, Xmp xmpData) throws SQLException {
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

    synchronized private void updateExif(Connection connection, long idFile, Exif exifData) throws SQLException {
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
        stmt.close(); // Closes statement and the result set

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
     * Aktualisiert ein Bild in der Datenbank.
     * 
     * @param imageFileData Bildmetadaten
     * @return              true bei Erfolg
     */
    synchronized public boolean updateImageFile(ImageFile imageFileData) {
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
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return success;
    }

    private long getIdFile(Connection connection, String filename) {
        long id = -1;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT id FROM files WHERE filename = ?"); // NOI18N

            stmt.setString(1, filename);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            //rs.close();
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        }
        return id;
    }

    String getValueParamString(int count) {
        StringBuffer values = new StringBuffer(" VALUES ("); // NOI18N

        for (int index = 0; index < count; index++) {
            if (index == 0) {
                values.append("?"); // NOI18N

            } else {
                values.append(", ?"); // NOI18N

            }
        }
        values.append(")"); // NOI18N

        return values.toString();
    }

    synchronized private void insertValues(Connection connection,
            String statement, long id, List<String> values) throws SQLException {

        PreparedStatement stmt = connection.prepareStatement(statement + " VALUES (?, ?)"); // NOI18N

        for (String value : values) {
            stmt.setLong(1, id);
            stmt.setString(2, value);
            logStatement(stmt);
            stmt.executeUpdate();
        }
        stmt.close();
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
        // notifyDatabaseListener() übernimmt aufgerufene Operation
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return false;
    }

    synchronized private void updateThumbnail(Connection connection, long idFile, Image thumbnail,
            String filename) throws
            SQLException {
        if (thumbnail != null) {
            ByteArrayInputStream inputStream = ImageUtil.getByteArrayInputStream(
                    thumbnail);
            if (inputStream != null) {
                PreparedStatement stmt =
                        connection.prepareStatement(
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
     *                 ermittelt werden konnte
     */
    public long getLastModified(String filename) {
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
            //rs.close();
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
            //rs.close();
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
     *                 keines existiert oder ein Fehler auftrat
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
                //rs.close();
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
                " dc_creator" + // NOI18N -- 1 --
                ", xmp.dc_description" + // NOI18N -- 2 --
                ", xmp.dc_rights" + // NOI18N -- 3 --
                ", xmp.dc_title" + // NOI18N -- 4 --
                ", xmp.iptc4xmpcore_countrycode" + // NOI18N -- 5 --
                ", xmp.iptc4xmpcore_location" + // NOI18N -- 6 --
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
                " ON xmp.id = xmp_photoshop_supplementalcategories.id" + // NOI18N
                " INNER JOIN files" + // NOI18N
                " ON xmp.id_files = files.id" + // NOI18N
                " WHERE files.filename = ?";
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
     * Entfernt eine Bilddatei aus der Datenbank.
     * 
     * @param filenames Namen der zu löschenden Dateien
     * @return          Anzahl gelöschter Datensätze
     */
    synchronized public int deleteImageFiles(List<String> filenames) {
        int countDeleted = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM files WHERE filename = ?"); // NOI18N

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
     *                 {@link de.elmar_baumann.imagemetadataviewer.event.ProgressListener#progressEnded(de.elmar_baumann.imagemetadataviewer.event.ProgressEvent)}
     *                 liefert ein {@link de.elmar_baumann.imagemetadataviewer.event.ProgressEvent}-Objekt,
     *                 das mit {@link de.elmar_baumann.imagemetadataviewer.event.ProgressEvent#getInfo()}
     *                 ein Int-Objekt liefert mit der Anzahl der gelöschten Datensätze.
     *                 {@link de.elmar_baumann.imagemetadataviewer.event.ProgressEvent#isStop()}
     *                 wird ausgewertet (Abbruch des Löschens).
     * @return         Anzahl gelöschter Datensätze
     */
    synchronized public int deleteNotExistingImageFiles(ProgressListener listener) {
        int countDeleted = 0;
        List<String> deletedFiles = new ArrayList<String>();
        ProgressEvent event = new ProgressEvent(this, 0, 0, 0, null);
        Connection connection = null;
        try {
            connection = getConnection();
            event.setMaximum(getFileCount());
            Statement stmt = connection.createStatement();
            String query = "SELECT filename FROM files"; // NOI18N

            ResultSet rs = stmt.executeQuery(query);
            String filename;
            boolean abort = notifyProgressListenerStart(listener, event);
            while (!abort && rs.next()) {
                filename = rs.getString(1);
                File file = new File(filename);
                if (!file.exists()) {
                    deleteRowWithFilename(connection, filename);
                    deletedFiles.add(filename);
                    countDeleted++;
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
    synchronized public int renameInXmpColumns(List<String> filenames,
            Column xmpColumn, String oldValue, String newValue, ProgressListener listener) {
        int countRenamed = 0;
        String tableName = xmpColumn.getTable().getName();
        String columnName = tableName + "." + xmpColumn.getName();
        boolean isXmpTable = xmpColumn.getTable().equals(TableXmp.getInstance());
        ProgressEvent event = new ProgressEvent(this, 0, 0, 0, null);
        XmpMetadata meta = new XmpMetadata();
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT DISTINCT files.id, xmp.id" + // NOI18N
                    " FROM xmp" + (isXmpTable ? "" : ", " + tableName) + ", files" + // NOI18N
                    (isXmpTable ? "" : " LEFT JOIN xmp ON " + tableName + ".id_xmp = xmp.id") + // NOI18N
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
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        event.setInfo(new Integer(countRenamed));
        notifyProgressListenerEnd(listener, event);
        return countRenamed;
    }

    private boolean notifyProgressListenerStart(ProgressListener listener,
            ProgressEvent event) {
        if (listener != null) {
            listener.progressStarted(event);
            return event.isStop();
        }
        return false;
    }

    private boolean notifyProgressListenerPerformed(ProgressListener listener,
            ProgressEvent event) {
        if (listener != null) {
            listener.progressPerformed(event);
            return event.isStop();
        }
        return false;
    }

    private void notifyProgressListenerEnd(ProgressListener listener,
            ProgressEvent event) {
        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    synchronized private void deleteRowWithFilename(Connection connection, String filename) {
        try {
            PreparedStatement stmt =
                    connection.prepareStatement(
                    "DELETE FROM files WHERE filename = ?"); // NOI18N

            stmt.setString(1, filename);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            assert count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        }
    }

    /**
     * Liefert alle Bilder einer Bildsammlung.
     * 
     * @param collectionName Name der Bildsammlung
     * @return               Dateinamen der Bilder
     */
    public List<String> getFilenamesOfImageCollection(String collectionName) {
        List<String> filenames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT files.filename FROM" + // NOI18N
                    " collections INNER JOIN collection_names" + // NOI18N
                    " ON collections.id_collectionnnames = collection_names.id" + // NOI18N
                    " INNER JOIN files ON collections.id_files = files.id" + // NOI18N 
                    " WHERE collection_names.name = ?" + // NOI18N
                    " ORDER BY collections.sequence_number ASC"); // NOI18N

            stmt.setString(1, collectionName);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            filenames.clear();
        } finally {
            free(connection);
        }
        return filenames;
    }

    /**
     * Liefert die Namen aller (bekannten) Sammlungen.
     * 
     * @return Namen der Sammlungen
     */
    public List<String> getImageCollectionNames() {
        List<String> names = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM collection_names"); // NOI18N

            while (rs.next()) {
                names.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            names.clear();
        } finally {
            free(connection);
        }
        return names;
    }

    /**
     * Benennt eine Bildsammlung um.
     * 
     * @param oldName Alter Name
     * @param newName Neuer Name
     * @return        Anzahl umbenannter Sammlungen (sollte 1 oder 0 sein)
     */
    synchronized public int updateRenameImageCollection(String oldName, String newName) {
        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE collection_names SET name = ? WHERE name = ?"); // NOI18N

            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logStatement(stmt);
            count = stmt.executeUpdate();
            List<String> info = new ArrayList<String>();
            info.add(oldName);
            info.add(newName);
            notifyDatabaseListener(DatabaseAction.Type.ImageFileUpdated, info);
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Fügt der Datenbank eine Bildsammlung hinzu. Existiert eine dieses Namens,
     * wird sie vorher gelöscht.
     * 
     * @param collectionName Name der Bildsammlung
     * @param filenames      Dateien in der gewünschten Reihenfolge
     * @return               true bei Erfolg
     * @see                  #existsImageCollection(java.lang.String)
     */
    synchronized public boolean insertImageCollection(
            String collectionName, List<String> filenames) {
        boolean added = false;
        if (existsImageCollection(collectionName)) {
            deleteImageCollection(collectionName);
        }
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmtName = connection.prepareStatement(
                    "INSERT INTO collection_names (name) VALUES (?)"); // NOI18N

            PreparedStatement stmtColl = connection.prepareStatement(
                    "INSERT INTO collections" + // NOI18N
                    " (id_collectionnnames, id_files, sequence_number)" + // NOI18N
                    " VALUES (?, ?, ?)"); // NOI18N

            stmtName.setString(1, collectionName);
            logStatement(stmtName);
            stmtName.executeUpdate();
            long idCollectionName = getIdCollectionName(connection, collectionName);
            int sequence_number = 0;
            for (String filename : filenames) {
                long idFile = getIdFile(connection, filename);
                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idFile);
                stmtColl.setInt(3, sequence_number++);
                logStatement(stmtColl);
                stmtColl.executeUpdate();
            }
            connection.commit();
            added = true;
            notifyDatabaseListener(DatabaseAction.Type.ImageCollectionInserted, collectionName, filenames);
            stmtName.close();
            stmtColl.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return added;
    }

    /**
     * Löscht eine Bildsammlung.
     * 
     * @param collectionname Name der Bildsammlung
     * @return               true bei Erfolg
     */
    synchronized public boolean deleteImageCollection(String collectionname) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collection_names WHERE name = ?"); // NOI18N

            stmt.setString(1, collectionname);
            logStatement(stmt);
            stmt.executeUpdate();
            deleted = true;
            notifyDatabaseListener(DatabaseAction.Type.ImageCollectionDeleted, collectionname);
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Löscht Bilder aus einer Bildsammlung.
     * 
     * @param collectionName Name der Sammlung
     * @param filenames      Dateinamen
     * @return               Anzahl gelöschter Bilder
     */
    synchronized public int deleteImagesFromCollection(
            String collectionName, List<String> filenames) {
        int delCount = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collections" + // NOI18N
                    " WHERE id_collectionnnames = ? AND id_files = ?"); // NOI18N

            for (String filename : filenames) {
                long idCollectionName = getIdCollectionName(connection, collectionName);
                long idFile = getIdFile(connection, filename);
                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                logStatement(stmt);
                delCount += stmt.executeUpdate();
                reorderCollectionSequenceNumber(connection, collectionName);
            }
            connection.commit();
            notifyDatabaseListener(DatabaseAction.Type.ImageCollectionImagesDeleted, collectionName, filenames);
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return delCount;
    }

    /**
     * Fügt einer Bildsammlung Bilder hinzu.
     * 
     * @param collectionName Name der Bildsammlung. Existiert diese nicht, wird
     *                       eine neue Bildsammlung angelegt
     * @param filenames      Dateinamen. Existiert eine der Dateien in der
     *                       Bildsammlung, wird sie nicht hinzugefügt
     * @return               true bei Erfolg
     */
    synchronized public boolean insertImagesIntoCollection(
            String collectionName, List<String> filenames) {
        boolean added = false;
        Connection connection = null;
        try {
            if (existsImageCollection(collectionName)) {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO collections (id_files, id_collectionnnames, sequence_number)" + // NOI18N
                        " VALUES (?, ?, ?)"); // NOI18N

                long idCollectionNames = getIdCollectionName(connection, collectionName);
                int sequence_number = getMaxCollectionSequenceNumber(connection, collectionName) + 1;
                for (String filename : filenames) {
                    if (!isImageInCollection(connection, collectionName, filename)) {
                        long idFiles = getIdFile(connection, filename);
                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        logStatement(stmt);
                        stmt.executeUpdate();
                    }
                }
                reorderCollectionSequenceNumber(connection, collectionName);
                stmt.close();
            } else {
                return insertImageCollection(collectionName, filenames);
            }
            connection.commit();
            added = true;
            notifyDatabaseListener(DatabaseAction.Type.ImageCollectionImagesAdded, collectionName, filenames);
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return added;
    }

    private int getMaxCollectionSequenceNumber(Connection connection, String collectionName) throws SQLException {
        int max = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT MAX(collections.sequence_number)" + // NOI18N
                " FROM collections INNER JOIN collection_names" + // NOI18N
                " ON collections.id_collectionnnames = collection_names.id" + // NOI18N
                " AND collection_names.name = ?"); // NOI18N

        stmt.setString(1, collectionName);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            max = rs.getInt(1);
        }
        stmt.close();
        return max;
    }

    synchronized private void reorderCollectionSequenceNumber(Connection connection, String collectionName) throws SQLException {
        long idCollectionName = getIdCollectionName(connection, collectionName);
        PreparedStatement stmtIdFiles = connection.prepareStatement(
                "SELECT id_files FROM collections WHERE id_collectionnnames = ?" + // NOI18N
                " ORDER BY collections.sequence_number ASC"); // NOI18N

        stmtIdFiles.setLong(1, idCollectionName);
        logStatement(stmtIdFiles);
        ResultSet rs = stmtIdFiles.executeQuery();
        List<Long> idFiles = new ArrayList<Long>();
        while (rs.next()) {
            idFiles.add(rs.getLong(1));
        }
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE collections SET sequence_number = ?" + // NOI18N
                " WHERE id_collectionnnames = ? AND id_files = ?"); // NOI18N

        int sequenceNumer = 0;
        for (Long idFile : idFiles) {
            stmt.setInt(1, sequenceNumer++);
            stmt.setLong(2, idCollectionName);
            stmt.setLong(3, idFile);
            logStatement(stmt);
            stmt.executeUpdate();
        }
        stmtIdFiles.close();
        stmt.close();
    }

    /**
     * Liefert, ob eine Bildsammlung existiert.
     * 
     * @param collectionName Name der Bildsammlung
     * @return               true, wenn die Bildsammlung existiert
     */
    public boolean existsImageCollection(String collectionName) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM collection_names WHERE name = ?"); // NOI18N

            stmt.setString(1, collectionName);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
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
     * Liefert die Anzahl der Bildsammlungen.
     * 
     * @return Anzahl oder -1 bei Datenbankfehlern
     */
    public int getImageCollectionCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM collection_names"); // NOI18N

            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl aller Bilder in Bildsammlungen.
     * 
     * @return Anzahl oder -1 bei Datenbankfehlern
     */
    public int getImageCollectionImagesCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM collections"); // NOI18N

            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    private boolean isImageInCollection(Connection connection, String collectionName, String filename) throws SQLException {
        boolean isInCollection = false;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM" + // NOI18N
                " collections INNER JOIN collection_names" + // NOI18N
                " ON collections.id_collectionnnames = collection_names.id" + // NOI18N
                " INNER JOIN files on collections.id_files = files.id" + // NOI18N
                " WHERE collection_names.name = ? AND files.filename = ?"); // NOI18N

        stmt.setString(1, collectionName);
        stmt.setString(2, filename);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            isInCollection = rs.getInt(1) > 0;
        }
        stmt.close();
        return isInCollection;
    }

    private long getIdCollectionName(Connection connection, String collectionname) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM collection_names WHERE name = ?"); // NOI18N

        stmt.setString(1, collectionname);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Fügt eine gespeicherte Suche ein. Existiert die Suche, wird
     * {@link #updateSavedSearch(de.elmar_baumann.imagemetadataviewer.data.SavedSearch)}
     * aufgerufen.
     * 
     * @param  data Suche
     * @return true bei Erfolg
     */
    synchronized public boolean insertSavedSearch(SavedSearch data) {
        boolean inserted = false;
        SavedSearchParamStatement stmtData = data.getParamStatements();
        List<SavedSearchPanel> panelData = data.getPanels();
        if (stmtData != null && !stmtData.getName().isEmpty()) {
            if (existsSavedSearch(data)) {
                return updateSavedSearch(data);
            }
            Connection connection = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO saved_searches (name, sql_string, is_query)" + // NOI18N
                        " VALUES (?, ?, ?)"); // NOI18N

                stmt.setString(1, stmtData.getName());
                stmt.setBytes(2, stmtData.getSql().getBytes());
                stmt.setBoolean(3, stmtData.isQuery());
                logStatement(stmt);
                stmt.executeUpdate();
                long id = getIdSavedSearch(connection, stmtData.getName());
                insertSavedSearchValues(connection, id, stmtData.getValues());
                insertSavedSearchPanelData(connection, id, panelData);
                connection.commit();
                inserted = true;
                notifyDatabaseListener(DatabaseAction.Type.SavedSearchInserted, data);
                stmt.close();
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
                try {
                    connection.rollback();
                } catch (SQLException ex1) {
                    handleException(ex, Level.SEVERE);
                }
            } finally {
                free(connection);
            }
        }

        return inserted;
    }

    synchronized private void insertSavedSearchValues(Connection connection, long idSavedSearch, List<String> values) throws SQLException {
        if (idSavedSearch > 0 && values.size() > 0) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO saved_searches_values (" + // NOI18N 
                    "id_saved_searches" + // NOI18N -- 1 --
                    ", value" + // NOI18N -- 2 --
                    ", value_index" + // NOI18N -- 3 --
                    ")" + // NOI18N
                    " VALUES (?, ?, ?)"); // NOI18N

            stmt.setLong(1, idSavedSearch);
            int size = values.size();
            for (int index = 0; index < size; index++) {
                String value = values.get(index);
                stmt.setString(2, value);
                stmt.setInt(3, index);
                logStatement(stmt);
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    synchronized private void insertSavedSearchPanelData(
            Connection connection, long idSavedSearch, List<SavedSearchPanel> panelData) throws SQLException {
        if (idSavedSearch > 0 && panelData != null) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO" + // NOI18N
                    " saved_searches_panels (" + // NOI18N
                    "id_saved_searches" + // NOI18N -- 1 --
                    ", panel_index" + // NOI18N -- 2 --
                    ", bracket_left_1" + // NOI18N -- 3 --
                    ", operator_id" + // NOI18N -- 4 --
                    ", bracket_left_2" + // NOI18N -- 5 --
                    ", column_id" + // NOI18N -- 6 --
                    ", comparator_id" + // NOI18N -- 7 --
                    ", value" + // NOI18N -- 8 --
                    ", bracket_right)" + // NOI18N -- 9 --
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N

            stmt.setLong(1, idSavedSearch);
            for (SavedSearchPanel data : panelData) {
                stmt.setInt(2, data.getPanelIndex());
                stmt.setBoolean(3, data.isBracketLeft1Selected());
                stmt.setInt(4, data.getOperatorId());
                stmt.setBoolean(5, data.isBracketLeft2Selected());
                stmt.setInt(6, data.getColumnId());
                stmt.setInt(7, data.getComparatorId());
                stmt.setString(8, data.getValue());
                stmt.setBoolean(9, data.isBracketRightSelected());
                logStatement(stmt);
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    private long getIdSavedSearch(Connection connection, String name) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM saved_searches WHERE name = ?"); // NOI18N

        stmt.setString(1, name);
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Fügt ein neues Metadaten-Edit-Template ein. Existiert das Template
     * bereits, werden seine Daten aktualisiert.
     * 
     * @param  template  Template
     * @return true bei Erfolg
     */
    synchronized public boolean insertMetaDataEditTemplate(MetaDataEditTemplate template) {
        if (existsMetaDataEditTemplate(template.getName())) {
            return updateMetaDataEditTemplate(template);
        }
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO metadata_edit_templates" + // NOI18N
                    " (name" + // NOI18N
                    ", dcSubjects" + // NOI18N
                    ", dcTitle" + // NOI18N
                    ", photoshopHeadline" + // NOI18N
                    ", dcDescription" + // NOI18N
                    ", photoshopCaptionwriter" + // NOI18N
                    ", iptc4xmpcoreLocation" + // NOI18N
                    ", iptc4xmpcoreCountrycode" + // NOI18N
                    ", photoshopCategory" + // NOI18N
                    ", photoshopSupplementalCategories" + // NOI18N
                    ", dcRights" + // NOI18N
                    ", dcCreator" + // NOI18N
                    ", photoshopAuthorsposition" + // NOI18N
                    ", photoshopCity" + // NOI18N
                    ", photoshopState" + // NOI18N
                    ", photoshopCountry" + // NOI18N
                    ", photoshopTransmissionReference" + // NOI18N
                    ", photoshopInstructions" + // NOI18N
                    ", photoshopCredit" + // NOI18N
                    ", photoshopSource" + // NOI18N
                    ")" + // NOI18N
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N

            setMetaDataEditTemplate(stmt, template);
            logStatement(stmt);
            stmt.executeUpdate();
            connection.commit();
            inserted = true;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return inserted;
    }

    private void setMetaDataEditTemplate(PreparedStatement stmt,
            MetaDataEditTemplate template) throws SQLException {
        stmt.setString(1, template.getName());
        stmt.setBytes(2, template.getDcSubjects() == null ? null : template.getDcSubjects().getBytes());
        stmt.setBytes(3, template.getDcTitle() == null ? null : template.getDcTitle().getBytes());
        stmt.setBytes(4, template.getPhotoshopHeadline() == null ? null : template.getPhotoshopHeadline().getBytes());
        stmt.setBytes(5, template.getDcDescription() == null ? null : template.getDcDescription().getBytes());
        stmt.setBytes(6, template.getPhotoshopCaptionwriter() == null ? null : template.getPhotoshopCaptionwriter().getBytes());
        stmt.setBytes(7, template.getIptc4xmpcoreLocation() == null ? null : template.getIptc4xmpcoreLocation().getBytes());
        stmt.setBytes(8, template.getIptc4xmpcoreCountrycode() == null ? null : template.getIptc4xmpcoreCountrycode().getBytes());
        stmt.setBytes(9, template.getPhotoshopCategory() == null ? null : template.getPhotoshopCategory().getBytes());
        stmt.setBytes(10, template.getPhotoshopSupplementalCategories() == null ? null : template.getPhotoshopSupplementalCategories().getBytes());
        stmt.setBytes(11, template.getDcRights() == null ? null : template.getDcRights().getBytes());
        stmt.setBytes(12, template.getDcCreator() == null ? null : template.getDcCreator().getBytes());
        stmt.setBytes(13, template.getPhotoshopAuthorsposition() == null ? null : template.getPhotoshopAuthorsposition().getBytes());
        stmt.setBytes(14, template.getPhotoshopCity() == null ? null : template.getPhotoshopCity().getBytes());
        stmt.setBytes(15, template.getPhotoshopState() == null ? null : template.getPhotoshopState().getBytes());
        stmt.setBytes(16, template.getPhotoshopCountry() == null ? null : template.getPhotoshopCountry().getBytes());
        stmt.setBytes(17, template.getPhotoshopTransmissionReference() == null ? null : template.getPhotoshopTransmissionReference().getBytes());
        stmt.setBytes(18, template.getPhotoshopInstructions() == null ? null : template.getPhotoshopInstructions().getBytes());
        stmt.setBytes(19, template.getPhotoshopCredit() == null ? null : template.getPhotoshopCredit().getBytes());
        stmt.setBytes(20, template.getPhotoshopSource() == null ? null : template.getPhotoshopSource().getBytes());
    }

    /**
     * Liefert alle Metadaten-Edit-Templates.
     * 
     * @return Templates
     */
    public List<MetaDataEditTemplate> getMetaDataEditTemplates() {
        List<MetaDataEditTemplate> templates = new ArrayList<MetaDataEditTemplate>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT" + // NOI18N 
                    " name" + // NOI18N 
                    ", dcSubjects" + // NOI18N 
                    ", dcTitle" + // NOI18N 
                    ", photoshopHeadline" + // NOI18N 
                    ", dcDescription" + // NOI18N 
                    ", photoshopCaptionwriter" + // NOI18N 
                    ", iptc4xmpcoreLocation" + // NOI18N 
                    ", iptc4xmpcoreCountrycode" + // NOI18N 
                    ", photoshopCategory" + // NOI18N 
                    ", photoshopSupplementalCategories" + // NOI18N 
                    ", dcRights" + // NOI18N 
                    ", dcCreator" + // NOI18N 
                    ", photoshopAuthorsposition" + // NOI18N 
                    ", photoshopCity" + // NOI18N 
                    ", photoshopState" + // NOI18N 
                    ", photoshopCountry" + // NOI18N 
                    ", photoshopTransmissionReference" + // NOI18N
                    ", photoshopInstructions" + // NOI18N 
                    ", photoshopCredit" + // NOI18N 
                    ", photoshopSource" + // NOI18N
                    " FROM metadata_edit_templates" + // NOI18N
                    " WHERE name IS NOT NULL"); // NOI18N

            while (rs.next()) {
                MetaDataEditTemplate template = new MetaDataEditTemplate();
                template.setName(rs.getString(1));
                template.setDcSubjects(new String(rs.getBytes(2)));
                template.setDcTitle(new String(rs.getBytes(3)));
                template.setPhotoshopHeadline(new String(rs.getBytes(4)));
                template.setDcDescription(new String(rs.getBytes(5)));
                template.setPhotoshopCaptionwriter(new String(rs.getBytes(6)));
                template.setIptc4xmpcoreLocation(new String(rs.getBytes(7)));
                template.setIptc4xmpcoreCountrycode(new String(rs.getBytes(8)));
                template.setPhotoshopCategory(new String(rs.getBytes(9)));
                template.setPhotoshopSupplementalCategories(new String(rs.getBytes(10)));
                template.setDcRights(new String(rs.getBytes(11)));
                template.setDcCreator(new String(rs.getBytes(12)));
                template.setPhotoshopAuthorsposition(new String(rs.getBytes(13)));
                template.setPhotoshopCity(new String(rs.getBytes(14)));
                template.setPhotoshopState(new String(rs.getBytes(15)));
                template.setPhotoshopCountry(new String(rs.getBytes(16)));
                template.setPhotoshopTransmissionReference(new String(rs.getBytes(17)));
                template.setPhotoshopInstructions(new String(rs.getBytes(18)));
                template.setPhotoshopCredit(new String(rs.getBytes(19)));
                template.setPhotoshopSource(new String(rs.getBytes(20)));
                templates.add(template);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return templates;
    }

    /**
     * Aktualisiert ein Metadaten-Edit-Template.
     * 
     * @param  template  Template
     * @return true bei Erfolg
     */
    synchronized public boolean updateMetaDataEditTemplate(MetaDataEditTemplate template) {
        boolean updated = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE metadata_edit_templates" + // NOI18N
                    " SET name = ?" + // NOI18N Name ist redundant wegen setMetaDataEditTemplate()
                    ", dcSubjects = ?" + // NOI18N
                    ", dcTitle = ?" + // NOI18N
                    ", photoshopHeadline = ?" + // NOI18N
                    ", dcDescription = ?" + // NOI18N
                    ", photoshopCaptionwriter = ?" + // NOI18N
                    ", iptc4xmpcoreLocation = ?" + // NOI18N
                    ", iptc4xmpcoreCountrycode = ?" + // NOI18N
                    ", photoshopCategory = ?" + // NOI18N
                    ", photoshopSupplementalCategories = ?" + // NOI18N
                    ", dcRights = ?" + // NOI18N
                    ", dcCreator = ?" + // NOI18N
                    ", photoshopAuthorsposition = ?" + // NOI18N
                    ", photoshopCity = ?" + // NOI18N
                    ", photoshopState = ?" + // NOI18N
                    ", photoshopCountry = ?" + // NOI18N
                    ", photoshopTransmissionReference = ?" + // NOI18N
                    ", photoshopInstructions = ?" + // NOI18N
                    ", photoshopCredit = ?" + // NOI18N
                    ", photoshopSource = ?" + // NOI18N
                    " WHERE name = ?");  // NOI18N

            setMetaDataEditTemplate(stmt, template);
            stmt.setString(21, template.getName());
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return updated;
    }

    /**
     * Benennt ein Metadaten-Edit-Template um.
     * 
     * @param  oldName  Alter Name
     * @param  newName  Neuer Name
     * @return true bei Erfolg
     */
    synchronized public boolean updateRenameMetaDataEditTemplate(String oldName, String newName) {
        boolean renamed = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE metadata_edit_templates" + // NOI18N
                    " SET name = ?" + // NOI18N Name ist redundant wegen setMetaDataEditTemplate()
                    " WHERE name = ?");  // NOI18N

            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            renamed = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return renamed;
    }

    /**
     * Löscht ein Metadaten-Edit-Template.
     * 
     * @param  name  Name des Templates
     * @return true bei Erfolg
     */
    synchronized public boolean deleteMetaDataEditTemplate(String name) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM metadata_edit_templates WHERE name = ?");  // NOI18N

            stmt.setString(1, name);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return deleted;
    }

    public boolean existsMetaDataEditTemplate(String name) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*)" + // NOI18N
                    " FROM metadata_edit_templates" + // NOI18N
                    " WHERE name = ?"); // NOI18N

            stmt.setString(1, name);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
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
     * Liefert die Anzahl gespeicherter Suchen.
     * 
     * @return Anzahl oder -1 bei Fehlern.
     */
    public int getSavedSearchesCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM saved_searches"); // NOI18N

            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     * 
     * @param  name Name der gespeicherten Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #existsSavedSearch(de.elmar_baumann.imagemetadataviewer.data.SavedSearch)
     */
    public boolean existsSavedSearch(String name) {
        Connection connection = null;
        try {
            connection = getConnection();
            long id = getIdSavedSearch(connection, name);
            return id > 0;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            notifyErrorListener(ex.toString());
        } finally {
            free(connection);
        }
        return false;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     * 
     * @param  search search Gespeicherte Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #existsSavedSearch(java.lang.String)
     */
    public boolean existsSavedSearch(SavedSearch search) {
        return existsSavedSearch(search.getName());
    }

    /**
     * Löscht eine gespeicherte Suche.
     * 
     * @param  name Name der Suche
     * @return true bei Erfolg
     */
    synchronized public boolean deleteSavedSearch(String name) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM saved_searches WHERE name = ?"); // NOI18N

            stmt.setString(1, name);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            if (deleted) {
                notifyDatabaseListener(DatabaseAction.Type.SavedSearchDeleted, name);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Benennt eine gespeicherte Suche um.
     * 
     * @param  oldName Alter Name
     * @param  newName Neuer Name
     * @return true bei Erfolg
     */
    synchronized public boolean updateRenameSavedSearch(String oldName, String newName) {
        boolean renamed = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE saved_searches SET name = ? WHERE name = ?"); // NOI18N

            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            renamed = count > 0;
            if (renamed) {
                List<String> info = new ArrayList<String>();
                info.add(oldName);
                info.add(newName);
                notifyDatabaseListener(DatabaseAction.Type.SavedSearchUpdated, info);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return renamed;
    }

    /**
     * Aktualisiert eine gespeicherte Suche.
     * 
     * @param  data Suche
     * @return true bei Erfolg
     */
    synchronized public boolean updateSavedSearch(SavedSearch data) {
        if (data.hasParamStatement() && data.getParamStatements() != null) {
            boolean updated = // Gefahr: Löschen, aber kein Einfügen
                    deleteSavedSearch(data.getParamStatements().getName()) &&
                    insertSavedSearch(data);
            if (updated) {
                notifyDatabaseListener(DatabaseAction.Type.SavedSearchUpdated, data);
            }
            return updated;
        }
        return false;
    }

    /**
     * Liefert eine gespeicherte Suche mit bestimmtem Namen.
     * 
     * @param  name Name
     * @return Suche oder null, wenn die Suche nicht erzeugt wurde
     */
    public SavedSearch getSavedSearch(String name) {
        SavedSearch data = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT" + // NOI18N
                    " name, sql_string, is_query" + // NOI18N
                    " FROM saved_searches WHERE name = ?"); // NOI18N

            stmt.setString(1, name);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                data = new SavedSearch();
                SavedSearchParamStatement paramStatementData = new SavedSearchParamStatement();
                paramStatementData.setName(rs.getString(1));
                paramStatementData.setSql(new String(rs.getBytes(2)));
                paramStatementData.setQuery(rs.getBoolean(3));
                data.setParamStatements(paramStatementData);
                setSavedSearchValues(connection, data);
                setSavedSearchPanels(connection, data);
            }
            stmt.close();
        } catch (SQLException ex) {
            data = null;
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return data;
    }

    /**
     * Liefert alle gespeicherten Suchen.
     * 
     * @return Gespeicherte Suchen
     */
    public List<SavedSearch> getSavedSearches() {
        List<SavedSearch> allData = new ArrayList<SavedSearch>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT" + // NOI18N
                    " name, sql_string, is_query" + // NOI18N
                    " FROM saved_searches ORDER BY name"); // NOI18N

            while (rs.next()) {
                SavedSearch data = new SavedSearch();
                SavedSearchParamStatement paramStatementData = new SavedSearchParamStatement();
                paramStatementData.setName(rs.getString(1));
                paramStatementData.setSql(new String(rs.getBytes(2)));
                paramStatementData.setQuery(rs.getBoolean(3));
                data.setParamStatements(paramStatementData);
                setSavedSearchValues(connection, data);
                setSavedSearchPanels(connection, data);
                allData.add(data);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            allData.clear();
        } finally {
            free(connection);
        }
        return allData;
    }

    private void setSavedSearchValues(Connection connection, SavedSearch data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT" + // NOI18N
                " saved_searches_values.value" + // NOI18N -- 1 --
                " FROM" + // NOI18N
                " saved_searches_values INNER JOIN saved_searches" + // NOI18N
                " ON saved_searches_values.id_saved_searches = saved_searches.id" + // NOI18N
                " AND saved_searches.name = ?" + // NOI18N
                " ORDER BY saved_searches_values.value_index ASC"); // NOI18N

        stmt.setString(1, data.getParamStatements().getName());
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        List<String> values = new ArrayList<String>();
        while (rs.next()) {
            values.add(rs.getString(1));
        }
        stmt.close();
        if (values.size() > 0) {
            data.getParamStatements().setValues(values);
        }
    }

    private void setSavedSearchPanels(Connection connection, SavedSearch data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT" + // NOI18N
                " saved_searches_panels.panel_index" + // NOI18N -- 1 --
                ", saved_searches_panels.bracket_left_1" + // NOI18N -- 2 --
                ", saved_searches_panels.operator_id" + // NOI18N -- 3 --
                ", saved_searches_panels.bracket_left_2" + // NOI18N -- 4 --
                ", saved_searches_panels.column_id" + // NOI18N -- 5 --
                ", saved_searches_panels.comparator_id" + // NOI18N -- 6 --
                ", saved_searches_panels.value" + // NOI18N -- 7 --
                ", saved_searches_panels.bracket_right" + // NOI18N -- 8 --
                " FROM" + // NOI18N
                " saved_searches_panels INNER JOIN saved_searches" + // NOI18N
                " ON saved_searches_panels.id_saved_searches = saved_searches.id" + // NOI18N
                " AND saved_searches.name = ?" + // NOI18N
                " ORDER BY saved_searches_panels.panel_index ASC"); // NOI18N

        stmt.setString(1, data.getParamStatements().getName());
        logStatement(stmt);
        ResultSet rs = stmt.executeQuery();
        List<SavedSearchPanel> allPanelData = new ArrayList<SavedSearchPanel>();
        while (rs.next()) {
            SavedSearchPanel panelData = new SavedSearchPanel();
            panelData.setPanelIndex(rs.getInt(1));
            panelData.setBracketLeft1Selected(rs.getBoolean(2));
            panelData.setOperatorId(rs.getInt(3));
            panelData.setBracketLeft2Selected(rs.getBoolean(4));
            panelData.setColumnId(rs.getInt(5));
            panelData.setComparatorId(rs.getInt(6));
            panelData.setValue(rs.getString(7));
            panelData.setBracketRightSelected(rs.getBoolean(8));
            allPanelData.add(panelData);
        }
        stmt.close();
        if (allPanelData.size() > 0) {
            data.setPanels(allPanelData);
        }
    }

    /**
     * Fügt ein automatisch nach Metadaten zu scannendes Verzeichnis hinzu.
     * 
     * @param  directoryName Verzeichnisname
     * @return true bei Erfolg
     */
    synchronized public boolean insertAutoscanDirectory(String directoryName) {
        boolean inserted = false;
        if (!existsAutoscanDirectory(directoryName)) {
            Connection connection = null;
            try {
                connection = getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO autoscan_directories (directory) VALUES (?)"); // NOI18N

                stmt.setString(1, directoryName);
                logStatement(stmt);
                int count = stmt.executeUpdate();
                inserted = count > 0;
                notifyDatabaseListener(DatabaseAction.Type.AutoscanDirectoryInserted, directoryName);
                stmt.close();
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
            } finally {
                free(connection);
            }
        }
        return inserted;
    }

    /**
     * Fügt ein automatisch nach Metadaten zu scannende Verzeichnisse hinzu.
     * 
     * @param  directoryNames Verzeichnisnamen
     * @return true bei Erfolg
     */
    synchronized public boolean insertAutoscanDirectories(List<String> directoryNames) {
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO autoscan_directories (directory) VALUES (?)"); // NOI18N

            for (String directoryName : directoryNames) {
                if (!existsAutoscanDirectory(directoryName)) {
                    stmt.setString(1, directoryName);
                    logStatement(stmt);
                    stmt.executeUpdate();
                }
            }
            connection.commit();
            notifyDatabaseListener(DatabaseAction.Type.AutoscanDirectoriesInserted, directoryNames);
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }

        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Entfernt ein automatisch nach Metadaten zu scannendes Verzeichnis aus der
     * Datenbank.
     * 
     * @param  directoryName Name des Verzeichnisses
     * @return true bei Erfolg
     */
    synchronized public boolean deleteAutoscanDirectory(String directoryName) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM autoscan_directories WHERE directory = ?"); // NOI18N

            stmt.setString(1, directoryName);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            deleted = count > 0;
            if (count > 0) {
                notifyDatabaseListener(DatabaseAction.Type.AutoscanDirectoryDeleted, directoryName);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Liefert, ob ein automatisch nach Metadaten zu scannendes Verzeichnis
     * in der Datenbank existiert.
     * 
     * @param  directoryName Verzeichnisname
     * @return true, wenn das Verzeichnis existiert
     */
    public boolean existsAutoscanDirectory(String directoryName) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM autoscan_directories WHERE directory = ?"); // NOI18N

            stmt.setString(1, directoryName);
            logStatement(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
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
     * Liefet alle Verzeichnisse, die automatisch nach Metadaten zu scannen sind.
     * 
     * @return Verzeichnisnamen
     */
    public List<String> getAutoscanDirectories() {
        List<String> directories = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT directory FROM autoscan_directories ORDER BY directory ASC"); // NOI18N

            while (rs.next()) {
                directories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            directories.clear();
        } finally {
            free(connection);
        }

        return directories;
    }

    /**
     * Liefert alle Kategorien.
     * 
     * @return Kategorien
     */
    public LinkedHashSet<String> getCategories() {
        LinkedHashSet<String> categories = new LinkedHashSet<String>();
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
     * Liefert alle Dateien mit bestimmter Kategorie.
     * 
     * @param  category  Kategorie
     * @return Dateinamen
     */
    public LinkedHashSet<String> getFilenamesOfCategory(String category) {
        LinkedHashSet<String> filenames = new LinkedHashSet<String>();
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

    /**
     * Fügt ein Favoritenverzeichnis ein. Existiert es bereits, wird es
     * aktualisiert.
     * 
     * @param  favoriteDirectory  Favoritenverzeichnis
     * @return true bei Erfolg
     */
    synchronized public boolean insertFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        boolean inserted = false;
        Connection connection = null;
        try {
            if (existsFavoriteDirectory(favoriteDirectory.getFavoriteName())) {
                return updateFavoriteDirectory(favoriteDirectory.getFavoriteName(),
                        favoriteDirectory);
            }
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO favorite_directories" + // NOI18N
                    " (favorite_name, directory_name, favorite_index)" + // NOI18N
                    " VALUES (?, ?, ?)"); // NOI18N

            stmt.setString(1, favoriteDirectory.getFavoriteName());
            stmt.setString(2, favoriteDirectory.getDirectoryName());
            stmt.setInt(3, favoriteDirectory.getIndex());
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Löscht ein Favoritenverzeichnis.
     * 
     * @param  favoriteName Favoritenname
     * @return true bei Erfolg
     */
    synchronized public boolean deleteFavoriteDirectory(String favoriteName) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM favorite_directories WHERE favorite_name = ?"); // NOI18N

            stmt.setString(1, favoriteName);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Aktualisiert ein Favoritenverzeichnis.
     * 
     * @param favoriteName      Name
     * @param favorite          Favoritenverzeichnis
     * @return true bei Erfolg
     */
    synchronized public boolean updateFavoriteDirectory(String favoriteName,
            FavoriteDirectory favorite) {
        boolean updated = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE favorite_directories SET" + // NOI18N
                    " favorite_name = ?" + // NOI18N
                    ", directory_name = ?" + // NOI18N
                    ", favorite_index = ?" + // NOI18N
                    " WHERE favorite_name = ?"); // NOI18N

            stmt.setString(1, favorite.getFavoriteName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt(3, favorite.getIndex());
            stmt.setString(4, favoriteName);
            logStatement(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return updated;
    }

    /**
     * Liefert alle Favoritenverzeichnisse.
     * 
     * @return Favoritenverzeichnisse
     */
    public List<FavoriteDirectory> getFavoriteDirectories() {
        List<FavoriteDirectory> directories = new ArrayList<FavoriteDirectory>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT favorite_name, directory_name, favorite_index" + // NOI18N
                    " FROM favorite_directories" + // NOI18N
                    " ORDER BY favorite_index ASC"); // NOI18N

            while (rs.next()) {
                directories.add(new FavoriteDirectory(
                        rs.getString(1), rs.getString(2), rs.getInt(3)));
            }
            stmt.close();
        } catch (SQLException ex) {
            directories.clear();
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return directories;
    }

    /**
     * Liefert, ob ein Favoritenverzeichnis existiert.
     * 
     * @param  favoriteName  Name des Favoriten (Alias)
     * @return true wenn existent
     */
    public boolean existsFavoriteDirectory(String favoriteName) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM favorite_directories WHERE favorite_name = ?"); // NOI18N

            stmt.setString(1, favoriteName);
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

    /**
     * Liefert die Anzahl der Datensätze für verschiedene Spaltenwerte.
     * 
     * @param  column  Spalte
     * @return Anzahl oder -1 bei Fehlern
     */
    public int getDistinctCount(Column column) {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String query = "SELECT COUNT(*) FROM (SELECT DISTINCT " + // NOI18N
                    column.getName() +
                    " FROM " + // NOI18N
                    column.getTable().getName() +
                    ")"; // NOI18N

            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Dateien in der Datenbank.
     * 
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getFileCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM files"); // NOI18N

            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl aller Datensätze in allen Tabellen.
     * 
     * @return Anzahl oder -1 bei Fehlern
     */
    public long getTotalRecordCount() {
        long count = -1;
        Connection connection = null;
        List<Table> tables = AllTables.get();
        try {
            connection = getConnection();
            for (Table table : tables) {
                Statement stmt = connection.createStatement();
                String query = "SELECT COUNT(*) FROM " + table.getName(); // NOI18N

                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    count += rs.getInt(1);
                }
                stmt.close();
            }
        } catch (SQLException ex) {
            count = -1;
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Thumbnails in der Datenbank.
     * 
     * @return Thumbnailanzahl oder -1 bei Fehlern
     */
    public int getThumbnailCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM files WHERE thumbnail IS NOT NULL"); // NOI18N

            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Komprimiert die Datenbank.
     * 
     * @return true, wenn die Datenbank erfolgreich komprimiert wurde
     */
    synchronized public boolean compressDatabase() {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CHECKPOINT DEFRAG"); // NOI18N

            success = true;
            notifyDatabaseListener(DatabaseAction.Type.MaintainanceDatabaseCompressed);
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return success;
    }

    private void notifyErrorListener(String message) {
        ErrorListeners.getInstance().notifyErrorListener(new ErrorEvent(message, this));
    }

    private boolean existsTable(Connection connection, String tablename) throws SQLException {
        boolean exists = false;
        DatabaseMetaData dbm = connection.getMetaData();
        String[] names = {"TABLE"}; // NOI18N

        ResultSet rs = dbm.getTables(null, "%", "%", names); // NOI18N

        while (!exists && rs.next()) {
            exists = rs.getString("TABLE_NAME").equalsIgnoreCase(tablename); // NOI18N

        }
        rs.close();
        return exists;
    }

    /**
     * Creates the necessary tables if not exists. Exits the VM if not successfully.
     */
    synchronized public void createTables() {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            createFilesTable(connection, stmt);
            createXmpTables(connection, stmt);
            createExifTables(connection, stmt);
            createCollectionsTables(connection, stmt);
            createSavedSerachesTables(connection, stmt);
            createAutoScanDirectoriesTable(connection, stmt);
            createMetaDataEditTemplateTable(connection, stmt);
            createFavoriteDirectoriesTable(connection, stmt);
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex, Level.SEVERE);
            }
            JOptionPane.showMessageDialog(null,
                    Bundle.getString("Database.CreateTables.ErrorMessage"), // NOI18N
                    Bundle.getString("Database.CreateTables.ErrorMessage.Title"), // NOI18N
                    JOptionPane.ERROR_MESSAGE,
                    AppSettings.getSmallAppIcon());
            System.exit(0);
        } finally {
            free(connection);
        }
    }

    synchronized private void createFilesTable(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "files")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE files " + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", filename  VARCHAR_IGNORECASE(512) NOT NULL" + // NOI18N
                    ", lastmodified  BIGINT" + // NOI18N
                    ", thumbnail BINARY" + ");"); // NOI18N

            stmt.execute("CREATE UNIQUE INDEX idx_files ON files (filename)"); // NOI18N

        }
    }

    synchronized private void createXmpTables(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "xmp")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE xmp" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_files BIGINT NOT NULL" + // NOI18N
                    ", dc_creator VARCHAR(128)" + // NOI18N
                    ", dc_description VARCHAR_IGNORECASE(2000)" + // NOI18N
                    ", dc_rights VARCHAR_IGNORECASE(128)" + // NOI18N
                    ", dc_title VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", iptc4xmpcore_countrycode VARCHAR_IGNORECASE(3)" + // NOI18N
                    ", iptc4xmpcore_location VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", photoshop_authorsposition VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_captionwriter VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_category VARCHAR_IGNORECASE(128)" + // NOI18N
                    ", photoshop_city VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_country VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", photoshop_credit VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_headline VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", photoshop_instructions VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", photoshop_source VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_state VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", photoshop_transmissionReference VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_xmp_id_files ON xmp (id_files)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_description ON xmp (dc_description)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_rights ON xmp (dc_rights)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_title ON xmp (dc_title)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_iptc4xmpcore_countrycode" + // NOI18N
                    " ON xmp (iptc4xmpcore_countrycode)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_iptc4xmpcore_location" + // NOI18N
                    " ON xmp (iptc4xmpcore_location)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_authorsposition" + // NOI18N
                    " ON xmp (photoshop_authorsposition)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_captionwriter" + // NOI18N
                    " ON xmp (photoshop_captionwriter)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_category" + // NOI18N
                    " ON xmp (photoshop_category)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_city" + // NOI18N
                    " ON xmp (photoshop_city)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_country" + // NOI18N
                    " ON xmp (photoshop_country)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_credit" + // NOI18N
                    " ON xmp (photoshop_credit)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_headline" + // NOI18N
                    " ON xmp (photoshop_headline)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_instructions" + // NOI18N
                    " ON xmp (photoshop_instructions)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_source" + // NOI18N
                    " ON xmp (photoshop_source)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_state" + // NOI18N
                    " ON xmp (photoshop_state)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_transmissionReference" + // NOI18N
                    " ON xmp (photoshop_transmissionReference)"); // NOI18N

        }
        if (!existsTable(connection, "xmp_dc_subjects")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE xmp_dc_subjects" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_xmp BIGINT NOT NULL" + // NOI18N
                    ", subject VARCHAR_IGNORECASE(64)" + // NOI18N
                    ", FOREIGN KEY (id_xmp) REFERENCES xmp (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_subjects_id_xmp" + // NOI18N
                    " ON xmp_dc_subjects (id_xmp)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_dc_subjects_subject" + // NOI18N
                    " ON xmp_dc_subjects (subject)"); // NOI18N

        }
        if (!existsTable(connection, "xmp_photoshop_supplementalcategories")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE xmp_photoshop_supplementalcategories" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_xmp BIGINT NOT NULL" + // NOI18N
                    ", supplementalcategory VARCHAR_IGNORECASE(32)" + // NOI18N
                    ", FOREIGN KEY (id_xmp) REFERENCES xmp (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_supplementalcategories_id_xmp" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories (id_xmp)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_xmp_photoshop_supplementalcategories_supplementalcategory" + // NOI18N
                    " ON xmp_photoshop_supplementalcategories (supplementalcategory)"); // NOI18N

        }
    }

    synchronized private void createExifTables(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "exif")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE exif" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", id_files BIGINT NOT NULL" + // NOI18N
                    ", exif_recording_equipment VARCHAR_IGNORECASE(125)" + // NOI18N
                    ", exif_date_time_original DATE" + // NOI18N
                    ", exif_focal_length REAL" + // NOI18N
                    ", exif_iso_speed_ratings SMALLINT" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_exif_id_files ON exif (id_files)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_exif_recording_equipment ON exif (exif_recording_equipment)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_exif_date_time_original ON exif (exif_date_time_original)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_exif_focal_length ON exif (exif_focal_length)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_exif_iso_speed_ratings ON exif (exif_iso_speed_ratings)"); // NOI18N

        }
    }

    synchronized private void createCollectionsTables(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "collection_names")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE collection_names" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_collection_names_id ON collection_names (id)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_collection_names_name ON collection_names (name)"); // NOI18N

        }
        if (!existsTable(connection, "collections")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE collections" + // NOI18N
                    " (" + // NOI18N
                    "id_collectionnnames BIGINT" + // NOI18N
                    ", id_files BIGINT" + // NOI18N
                    ", sequence_number INTEGER" + // NOI18N
                    ", PRIMARY KEY (id_collectionnnames, id_files)" + // NOI18N
                    ", FOREIGN KEY (id_collectionnnames) REFERENCES collection_names (id) ON DELETE CASCADE" + // NOI18N
                    ", FOREIGN KEY (id_files) REFERENCES files (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_collections_id ON collections (id_collectionnnames, id_files)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_collections_sequence_number ON collections (sequence_number)"); // NOI18N

        }
    }

    synchronized private void createSavedSerachesTables(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "saved_searches")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE saved_searches" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", name VARCHAR_IGNORECASE(125)" + // NOI18N
                    ", sql_string BINARY" + // NOI18N
                    ", is_query BOOLEAN" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_saved_searches_id ON saved_searches (id)"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_saved_searches_name ON saved_searches (name)"); // NOI18N

        }
        if (!existsTable(connection, "saved_searches_values")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE saved_searches_values" + // NOI18N
                    " (" + // NOI18N
                    "id_saved_searches BIGINT" + // NOI18N
                    ", value VARCHAR(256)" + // NOI18N
                    ", value_index INTEGER" + // NOI18N
                    ", FOREIGN KEY (id_saved_searches) REFERENCES saved_searches (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_saved_searches_id_saved_searches ON saved_searches_values (id_saved_searches)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_saved_searches_value_index ON saved_searches_values (value_index)"); // NOI18N

        }
        if (!existsTable(connection, "saved_searches_panels")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE saved_searches_panels" + // NOI18N
                    " (" + // NOI18N
                    "id_saved_searches BIGINT" + // NOI18N
                    ", panel_index INTEGER" + // NOI18N
                    ", bracket_left_1 BOOLEAN" + // NOI18N
                    ", operator_id INTEGER" + // NOI18N
                    ", bracket_left_2 BOOLEAN" + // NOI18N
                    ", column_id INTEGER" + // NOI18N
                    ", comparator_id INTEGER" + // NOI18N
                    ", value VARCHAR(256)" + // NOI18N
                    ", bracket_right BOOLEAN" + // NOI18N
                    ", FOREIGN KEY (id_saved_searches) REFERENCES saved_searches (id) ON DELETE CASCADE" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_saved_searches_panels_id_saved_searches ON saved_searches_panels (id_saved_searches)"); // NOI18N

            stmt.execute(
                    "CREATE INDEX idx_saved_searches_panels_panel_index ON saved_searches_panels (panel_index)"); // NOI18N

        }
    }

    synchronized private void createAutoScanDirectoriesTable(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "autoscan_directories")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE autoscan_directories" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", directory VARCHAR_IGNORECASE(1024)" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_autoscan_directories_directory ON autoscan_directories (directory)"); // NOI18N

        }
    }

    synchronized private void createMetaDataEditTemplateTable(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "metadata_edit_templates")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE metadata_edit_templates" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", dcSubjects BINARY" + // NOI18N
                    ", dcTitle BINARY" + // NOI18N
                    ", photoshopHeadline BINARY" + // NOI18N
                    ", dcDescription BINARY" + // NOI18N
                    ", photoshopCaptionwriter BINARY" + // NOI18N
                    ", iptc4xmpcoreLocation BINARY" + // NOI18N
                    ", iptc4xmpcoreCountrycode BINARY" + // NOI18N
                    ", photoshopCategory BINARY" + // NOI18N
                    ", photoshopSupplementalCategories BINARY" + // NOI18N
                    ", dcRights BINARY" + // NOI18N
                    ", dcCreator BINARY" + // NOI18N
                    ", photoshopAuthorsposition BINARY" + // NOI18N
                    ", photoshopCity BINARY" + // NOI18N
                    ", photoshopState BINARY" + // NOI18N
                    ", photoshopCountry BINARY" + // NOI18N
                    ", photoshopTransmissionReference BINARY" + // NOI18N
                    ", photoshopInstructions BINARY" + // NOI18N
                    ", photoshopCredit BINARY" + // NOI18N
                    ", photoshopSource BINARY" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_metadata_edit_templates_name ON metadata_edit_templates (name)"); // NOI18N

        }
    }

    synchronized private void createFavoriteDirectoriesTable(Connection connection, Statement stmt) throws SQLException {
        if (!existsTable(connection, "favorite_directories")) { // NOI18N

            stmt.execute("CREATE CACHED TABLE favorite_directories" + // NOI18N
                    " (" + // NOI18N
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY" + // NOI18N
                    ", favorite_name VARCHAR_IGNORECASE(256)" + // NOI18N
                    ", directory_name VARCHAR(512)" + // NOI18N
                    ", favorite_index INTEGER" + // NOI18N
                    ");"); // NOI18N

            stmt.execute(
                    "CREATE UNIQUE INDEX idx_favorite_directories_favorite_name ON favorite_directories (favorite_name)"); // NOI18N

        }
    }

    private Database() {
    }
}
