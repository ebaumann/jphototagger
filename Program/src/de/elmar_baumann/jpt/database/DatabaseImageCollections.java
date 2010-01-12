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
import de.elmar_baumann.jpt.event.DatabaseImageCollectionsEvent;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionsEvent.Type;
import de.elmar_baumann.jpt.event.listener.DatabaseImageCollectionsListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseImageCollections extends Database {

    public static final DatabaseImageCollections                          INSTANCE        = new DatabaseImageCollections();
    private final       ListenerSupport<DatabaseImageCollectionsListener> listenerSupport = new ListenerSupport<DatabaseImageCollectionsListener>();

    private DatabaseImageCollections() {
    }

    /**
     * Liefert die Namen aller (bekannten) Sammlungen.
     *
     * @return Namen der Sammlungen
     */
    public List<String> getAll() {
        List<String> names = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT name FROM collection_names ORDER BY name";
            logFinest(sql);
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
    public int updateRename(String oldName, String newName) {

        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement("UPDATE collection_names SET name = ? WHERE name = ?");
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFiner(stmt);
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
    public List<String> getFilenamesOf(String collectionName) {
        List<String> filenames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT files.filename FROM" +
                    " collections INNER JOIN collection_names" +
                    " ON collections.id_collectionnnames = collection_names.id" +
                    " INNER JOIN files ON collections.id_files = files.id" +
                    " WHERE collection_names.name = ?" +
                    " ORDER BY collections.sequence_number ASC");
            stmt.setString(1, collectionName);
            logFinest(stmt);
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
     * @see                  #exists(java.lang.String)
     */
    public boolean insert(
            String collectionName, List<String> filenames) {
        boolean added = false;
        if (exists(collectionName)) {
            delete(collectionName);
        }
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmtName = connection.prepareStatement(
                    "INSERT INTO collection_names (name) VALUES (?)");
            PreparedStatement stmtColl = connection.prepareStatement(
                    "INSERT INTO collections" +
                    " (id_collectionnnames" + // -- 1 --
                    ", id_files" + // -- 2 --
                    ", sequence_number)" + // -- 3 --
                    " VALUES (?, ?, ?)");
            stmtName.setString(1, collectionName);
            logFiner(stmtName);
            stmtName.executeUpdate();
            long idCollectionName = findId(connection,
                    collectionName);
            int sequence_number = 0;
            for (String filename : filenames) {
                long idFile = DatabaseImageFiles.INSTANCE.findIdFile(
                        connection, filename);
                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idFile);
                stmtColl.setInt(3, sequence_number++);
                logFiner(stmtColl);
                stmtColl.executeUpdate();
            }
            connection.commit();
            added = true;
            stmtName.close();
            stmtColl.close();
            notifyListeners(Type.COLLECTION_INSERTED, collectionName, filenames);
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
    public boolean delete(String collectionname) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionname);
            logFiner(stmt);
            List<String> affectedFiles = // Prior to executing!
                    getFilenamesOf(collectionname);
            stmt.executeUpdate();
            deleted = true;
            stmt.close();
            notifyListeners(Type.COLLECTION_DELETED, collectionname, affectedFiles);
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
    public int deleteImagesFrom(
            String collectionName, List<String> filenames) {

        int delCount = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM collections" +
                    " WHERE id_collectionnnames = ? AND id_files = ?");
            List<String> affectedFiles = new ArrayList<String>(filenames.size());
            for (String filename : filenames) {
                int prevDelCount = delCount;
                long idCollectionName = findId(
                        connection, collectionName);
                long idFile = DatabaseImageFiles.INSTANCE.findIdFile(
                        connection, filename);
                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                logFiner(stmt);
                delCount += stmt.executeUpdate();
                if (prevDelCount < delCount) {
                    affectedFiles.add(filename);
                }
                reorderSequenceNumber(connection, collectionName);
            }
            connection.commit();
            stmt.close();
            notifyListeners(Type.IMAGES_DELETED, collectionName, affectedFiles);
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
    public boolean insertImagesInto(
            String collectionName, List<String> filenames) {

        boolean added = false;
        Connection connection = null;
        try {
            if (exists(collectionName)) {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO collections" +
                        " (id_files" + // -- 1 --
                        ", id_collectionnnames" + // -- 2 --
                        ", sequence_number)" + // -- 3 --
                        " VALUES (?, ?, ?)");
                long idCollectionNames = findId(
                        connection, collectionName);
                int sequence_number = getMaxSequenceNumber(
                        connection, collectionName) + 1;
                List<String> affectedFiles =
                        new ArrayList<String>(filenames.size());
                for (String filename : filenames) {
                    if (!isImageIn(connection, collectionName,
                            filename)) {
                        long idFiles = DatabaseImageFiles.INSTANCE.findIdFile(
                                connection, filename);
                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        logFiner(stmt);
                        stmt.executeUpdate();
                        affectedFiles.add(filename);
                    }
                }
                reorderSequenceNumber(connection, collectionName);
                stmt.close();
                notifyListeners(Type.IMAGES_INSERTED, collectionName, affectedFiles);
            } else {
                return insert(collectionName, filenames);
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

    private int getMaxSequenceNumber(
            Connection connection, String collectionName) throws SQLException {

        int max = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT MAX(collections.sequence_number)" +
                " FROM collections INNER JOIN collection_names" +
                " ON collections.id_collectionnnames = collection_names.id" +
                " AND collection_names.name = ?");
        stmt.setString(1, collectionName);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            max = rs.getInt(1);
        }
        stmt.close();
        return max;
    }

    private void reorderSequenceNumber(
            Connection connection, String collectionName) throws SQLException {

        long idCollectionName = findId(connection, collectionName);
        PreparedStatement stmtIdFiles = connection.prepareStatement(
                "SELECT id_files FROM collections WHERE id_collectionnnames = ?" +
                " ORDER BY collections.sequence_number ASC");
        stmtIdFiles.setLong(1, idCollectionName);
        logFinest(stmtIdFiles);
        ResultSet rs = stmtIdFiles.executeQuery();
        List<Long> idFiles = new ArrayList<Long>();
        while (rs.next()) {
            idFiles.add(rs.getLong(1));
        }
        PreparedStatement stmt = connection.prepareStatement(
                "UPDATE collections SET sequence_number = ?" +
                " WHERE id_collectionnnames = ? AND id_files = ?");
        int sequenceNumer = 0;
        for (Long idFile : idFiles) {
            stmt.setInt(1, sequenceNumer++);
            stmt.setLong(2, idCollectionName);
            stmt.setLong(3, idFile);
            logFiner(stmt);
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
    public boolean exists(String collectionName) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionName);
            logFinest(stmt);
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
    public int getCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM collection_names";
            logFinest(sql);
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
    public int getTotalImageCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM collections";
            logFinest(sql);
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

    private boolean isImageIn(
            Connection connection, String collectionName, String filename)
            throws SQLException {

        boolean isInCollection = false;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM" +
                " collections INNER JOIN collection_names" +
                " ON collections.id_collectionnnames = collection_names.id" +
                " INNER JOIN files on collections.id_files = files.id" +
                " WHERE collection_names.name = ? AND files.filename = ?");
        stmt.setString(1, collectionName);
        stmt.setString(2, filename);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            isInCollection = rs.getInt(1) > 0;
        }
        stmt.close();
        return isInCollection;
    }

    private long findId(
            Connection connection, String collectionname) throws SQLException {

        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM collection_names WHERE name = ?");
        stmt.setString(1, collectionname);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    public void addListener(DatabaseImageCollectionsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseImageCollectionsListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(
            DatabaseImageCollectionsEvent.Type type,
            String                            collectionName,
            Collection<String>                filenames) {

        DatabaseImageCollectionsEvent         evt       = new DatabaseImageCollectionsEvent(type, collectionName, filenames);
        Set<DatabaseImageCollectionsListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
