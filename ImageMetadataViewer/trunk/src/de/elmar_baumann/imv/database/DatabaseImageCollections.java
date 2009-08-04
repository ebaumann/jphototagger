package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseImageCollections extends Database {

    public static final DatabaseImageCollections INSTANCE =
            new DatabaseImageCollections();

    private DatabaseImageCollections() {
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
            String sql = "SELECT name FROM collection_names ORDER BY name"; // NOI18N
            AppLog.logFinest(getClass(), sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
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
    public int updateRenameImageCollection(String oldName, String newName) {

        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE collection_names SET name = ? WHERE name = ?"); // NOI18N
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            AppLog.logFiner(DatabaseImageCollections.class, stmt.toString());
            count = stmt.executeUpdate();
            List<String> info = new ArrayList<String>();
            info.add(oldName);
            info.add(newName);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            free(connection);
        }
        return count;
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
            AppLog.logFinest(DatabaseImageCollections.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
            filenames.clear();
        } finally {
            free(connection);
        }
        return filenames;
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
    public boolean insertImageCollection(
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
                    " (id_collectionnnames" + // NOI18N -- 1 --
                    ", id_files" + // NOI18N -- 2 --
                    ", sequence_number)" + // NOI18N -- 3 --
                    " VALUES (?, ?, ?)"); // NOI18N
            stmtName.setString(1, collectionName);
            AppLog.logFiner(DatabaseImageCollections.class, stmtName.toString());
            stmtName.executeUpdate();
            long idCollectionName = getIdCollectionName(connection,
                    collectionName);
            int sequence_number = 0;
            for (String filename : filenames) {
                long idFile = DatabaseImageFiles.INSTANCE.getIdFile(
                        connection, filename);
                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idFile);
                stmtColl.setInt(3, sequence_number++);
                AppLog.logFiner(
                        DatabaseImageCollections.class, stmtColl.toString());
                stmtColl.executeUpdate();
            }
            connection.commit();
            added = true;
            stmtName.close();
            stmtColl.close();
            notifyDatabaseListener(
                    Type.COLLECTION_INSERTED, collectionName, filenames);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
            rollback(connection);
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
    public boolean deleteImageCollection(String collectionname) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collection_names WHERE name = ?"); // NOI18N
            stmt.setString(1, collectionname);
            AppLog.logFiner(DatabaseImageCollections.class, stmt.toString());
            List<String> affectedFiles = // Prior to executing!
                    getFilenamesOfImageCollection(collectionname);
            stmt.executeUpdate();
            deleted = true;
            stmt.close();
            notifyDatabaseListener(
                    Type.COLLECTION_DELETED, collectionname, affectedFiles);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
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
    public int deleteImagesFromCollection(
            String collectionName, List<String> filenames) {

        int delCount = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collections" + // NOI18N
                    " WHERE id_collectionnnames = ? AND id_files = ?"); // NOI18N
            List<String> affectedFiles = new ArrayList<String>(filenames.size());
            for (String filename : filenames) {
                int prevDelCount = delCount;
                long idCollectionName = getIdCollectionName(
                        connection, collectionName);
                long idFile = DatabaseImageFiles.INSTANCE.getIdFile(
                        connection, filename);
                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                AppLog.logFiner(DatabaseImageCollections.class, stmt.toString());
                delCount += stmt.executeUpdate();
                if (prevDelCount < delCount) {
                    affectedFiles.add(filename);
                }
                reorderCollectionSequenceNumber(connection, collectionName);
            }
            connection.commit();
            stmt.close();
            notifyDatabaseListener(
                    Type.IMAGES_DELETED, collectionName, affectedFiles);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return delCount;
    }

    /**
     * Fügt einer Bildsammlung Bilder hinzu.
     *
     * @param collectionName Name der Bildsammlung. Existiert diese nicht, wird
     * eine neue Bildsammlung angelegt
     * @param filenames      Dateinamen. Existiert eine der Dateien in der
     * Bildsammlung, wird sie nicht hinzugefügt
     * @return               true bei Erfolg
     */
    public boolean insertImagesIntoCollection(
            String collectionName, List<String> filenames) {

        boolean added = false;
        Connection connection = null;
        try {
            if (existsImageCollection(collectionName)) {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO collections" + // NOI18N
                        " (id_files" + // NOI18N -- 1 --
                        ", id_collectionnnames" + // NOI18N -- 2 --
                        ", sequence_number)" + // NOI18N -- 3 --
                        " VALUES (?, ?, ?)"); // NOI18N
                long idCollectionNames = getIdCollectionName(
                        connection, collectionName);
                int sequence_number = getMaxCollectionSequenceNumber(
                        connection, collectionName) + 1;
                List<String> affectedFiles =
                        new ArrayList<String>(filenames.size());
                for (String filename : filenames) {
                    if (!isImageInCollection(connection, collectionName,
                            filename)) {
                        long idFiles = DatabaseImageFiles.INSTANCE.getIdFile(
                                connection, filename);
                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        AppLog.logFiner(
                                DatabaseImageCollections.class, stmt.toString());
                        stmt.executeUpdate();
                        affectedFiles.add(filename);
                    }
                }
                reorderCollectionSequenceNumber(connection, collectionName);
                stmt.close();
                notifyDatabaseListener(
                        Type.IMAGES_INSERTED, collectionName, affectedFiles);
            } else {
                return insertImageCollection(collectionName, filenames);
            }
            connection.commit();
            added = true;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return added;
    }

    private int getMaxCollectionSequenceNumber(
            Connection connection, String collectionName) throws SQLException {

        int max = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT MAX(collections.sequence_number)" + // NOI18N
                " FROM collections INNER JOIN collection_names" + // NOI18N
                " ON collections.id_collectionnnames = collection_names.id" + // NOI18N
                " AND collection_names.name = ?"); // NOI18N
        stmt.setString(1, collectionName);
        AppLog.logFinest(DatabaseImageCollections.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            max = rs.getInt(1);
        }
        stmt.close();
        return max;
    }

    private void reorderCollectionSequenceNumber(
            Connection connection, String collectionName) throws SQLException {

        long idCollectionName = getIdCollectionName(connection, collectionName);
        PreparedStatement stmtIdFiles = connection.prepareStatement(
                "SELECT id_files FROM collections WHERE id_collectionnnames = ?" + // NOI18N
                " ORDER BY collections.sequence_number ASC"); // NOI18N
        stmtIdFiles.setLong(1, idCollectionName);
        AppLog.logFinest(DatabaseImageCollections.class, stmtIdFiles.toString());
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
            AppLog.logFiner(DatabaseImageCollections.class, stmt.toString());
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
            AppLog.logFinest(DatabaseImageCollections.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
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
            String sql = "SELECT COUNT(*) FROM collection_names"; // NOI18N
            AppLog.logFinest(getClass(), sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
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
            String sql = "SELECT COUNT(*) FROM collections"; // NOI18N
            AppLog.logFinest(getClass(), sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    private boolean isImageInCollection(
            Connection connection, String collectionName, String filename)
            throws SQLException {

        boolean isInCollection = false;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM" + // NOI18N
                " collections INNER JOIN collection_names" + // NOI18N
                " ON collections.id_collectionnnames = collection_names.id" + // NOI18N
                " INNER JOIN files on collections.id_files = files.id" + // NOI18N
                " WHERE collection_names.name = ? AND files.filename = ?"); // NOI18N
        stmt.setString(1, collectionName);
        stmt.setString(2, filename);
        AppLog.logFinest(DatabaseImageCollections.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            isInCollection = rs.getInt(1) > 0;
        }
        stmt.close();
        return isInCollection;
    }

    private long getIdCollectionName(
            Connection connection, String collectionname) throws SQLException {

        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM collection_names WHERE name = ?"); // NOI18N
        stmt.setString(1, collectionname);
        AppLog.logFinest(DatabaseImageCollections.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }
}
