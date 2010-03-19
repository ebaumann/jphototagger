/*
 * @(#)DatabaseImageCollections.java    Created on 2008-10-21
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
import de.elmar_baumann.jpt.data.ImageCollection;
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
 * @author  Elmar Baumann
 */
public final class DatabaseImageCollections extends Database {
    public static final DatabaseImageCollections INSTANCE =
        new DatabaseImageCollections();
    private final ListenerSupport<DatabaseImageCollectionsListener> listenerSupport =
        new ListenerSupport<DatabaseImageCollectionsListener>();

    private DatabaseImageCollections() {}

    /**
     * Liefert die Namen aller (bekannten) Sammlungen.
     *
     * @return Namen der Sammlungen
     */
    public List<String> getAll() {
        List<String> names = new ArrayList<String>();
        Connection   con   = null;
        Statement    stmt  = null;
        ResultSet    rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT name FROM collection_names ORDER BY name";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            names.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return names;
    }

    public List<ImageCollection> getAll2() {
        List<String>          names       = getAll();
        List<ImageCollection> collections =
            new ArrayList<ImageCollection>(names.size());

        for (String name : names) {
            collections.add(new ImageCollection(name, getFilenamesOf(name)));
        }

        return collections;
    }

    /**
     * Benennt eine Bildsammlung um.
     *
     * @param oldName Alter Name
     * @param newName Neuer Name
     * @return        Anzahl umbenannter Sammlungen (sollte 1 oder 0 sein)
     */
    public int updateRename(String oldName, String newName) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE collection_names SET name = ? WHERE name = ?");
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFiner(stmt);
            count = stmt.executeUpdate();

            List<String> info = new ArrayList<String>();

            info.add(oldName);
            info.add(newName);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(stmt);
            free(con);
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
        List<String>      filenames = new ArrayList<String>();
        Connection        con       = null;
        PreparedStatement stmt      = null;
        ResultSet         rs        = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT files.filename FROM"
                + " collections INNER JOIN collection_names"
                + " ON collections.id_collectionnnames = collection_names.id"
                + " INNER JOIN files ON collections.id_files = files.id"
                + " WHERE collection_names.name = ?"
                + " ORDER BY collections.sequence_number ASC");
            stmt.setString(1, collectionName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            filenames.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return filenames;
    }

    public boolean insert(ImageCollection collection) {
        return insert(collection.getName(), collection.getFilenames());
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
    public boolean insert(String collectionName, List<String> filenames) {
        boolean added = false;

        if (exists(collectionName)) {
            delete(collectionName);
        }

        Connection        con      = null;
        PreparedStatement stmtName = null;
        PreparedStatement stmtColl = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmtName = con.prepareStatement(
                "INSERT INTO collection_names (name) VALUES (?)");
            stmtColl = con.prepareStatement(
                "INSERT INTO collections"
                + " (id_collectionnnames, id_files, sequence_number)"
                + " VALUES (?, ?, ?)");
            stmtName.setString(1, collectionName);
            logFiner(stmtName);
            stmtName.executeUpdate();

            long idCollectionName = findId(con, collectionName);
            int  sequence_number  = 0;

            for (String filename : filenames) {
                long idFile = DatabaseImageFiles.INSTANCE.findIdFile(con,
                                  filename);

                if (!DatabaseImageFiles.INSTANCE.exists(filename)) {
                    AppLogger.logWarning(
                        getClass(),
                        "DatabaseImageCollections.Error.Insert.FileId",
                        filename);
                    rollback(con);

                    return false;
                }

                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idFile);
                stmtColl.setInt(3, sequence_number++);
                logFiner(stmtColl);
                stmtColl.executeUpdate();
            }

            con.commit();
            added = true;
            notifyListeners(Type.COLLECTION_INSERTED, collectionName,
                            filenames);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            rollback(con);
        } finally {
            close(stmtColl);
            close(stmtName);
            free(con);
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
        boolean           deleted = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "DELETE FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionname);
            logFiner(stmt);

            List<String> affectedFiles =    // Prior to executing!
                getFilenamesOf(collectionname);

            stmt.executeUpdate();
            deleted = true;
            notifyListeners(Type.COLLECTION_DELETED, collectionname,
                            affectedFiles);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(stmt);
            free(con);
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
    public int deleteImagesFrom(String collectionName, List<String> filenames) {
        int               delCount = 0;
        Connection        con      = null;
        PreparedStatement stmt     = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM collections"
                + " WHERE id_collectionnnames = ? AND id_files = ?");

            List<String> affectedFiles =
                new ArrayList<String>(filenames.size());

            for (String filename : filenames) {
                int  prevDelCount     = delCount;
                long idCollectionName = findId(con, collectionName);
                long idFile           =
                    DatabaseImageFiles.INSTANCE.findIdFile(con, filename);

                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                logFiner(stmt);
                delCount += stmt.executeUpdate();

                if (prevDelCount < delCount) {
                    affectedFiles.add(filename);
                }

                reorderSequenceNumber(con, collectionName);
            }

            con.commit();
            notifyListeners(Type.IMAGES_DELETED, collectionName, affectedFiles);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
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
    public boolean insertImagesInto(String collectionName,
                                    List<String> filenames) {
        boolean           added = false;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            if (exists(collectionName)) {
                con = getConnection();
                con.setAutoCommit(false);
                stmt = con.prepareStatement(
                    "INSERT INTO collections"
                    + " (id_files, id_collectionnnames, sequence_number)"
                    + " VALUES (?, ?, ?)");

                long idCollectionNames = findId(con, collectionName);
                int  sequence_number   = getMaxSequenceNumber(con,
                                             collectionName) + 1;
                List<String> affectedFiles =
                    new ArrayList<String>(filenames.size());

                for (String filename : filenames) {
                    if (!isImageIn(con, collectionName, filename)) {
                        long idFiles =
                            DatabaseImageFiles.INSTANCE.findIdFile(con,
                                filename);

                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        logFiner(stmt);
                        stmt.executeUpdate();
                        affectedFiles.add(filename);
                    }
                }

                reorderSequenceNumber(con, collectionName);
                notifyListeners(Type.IMAGES_INSERTED, collectionName,
                                affectedFiles);
            } else {
                return insert(collectionName, filenames);
            }

            con.commit();
            added = true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return added;
    }

    private int getMaxSequenceNumber(Connection con, String collectionName)
            throws SQLException {
        int               max  = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT MAX(collections.sequence_number)"
                + " FROM collections INNER JOIN collection_names"
                + " ON collections.id_collectionnnames = collection_names.id"
                + " AND collection_names.name = ?");
            stmt.setString(1, collectionName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                max = rs.getInt(1);
            }
        } finally {
            close(rs, stmt);
        }

        return max;
    }

    private void reorderSequenceNumber(Connection con, String collectionName)
            throws SQLException {
        long              idCollectionName = findId(con, collectionName);
        PreparedStatement stmtIdFiles      = null;
        PreparedStatement stmt             = null;
        ResultSet         rs               = null;

        try {
            stmtIdFiles = con.prepareStatement(
                "SELECT id_files FROM collections WHERE id_collectionnnames = ?"
                + " ORDER BY collections.sequence_number ASC");
            stmtIdFiles.setLong(1, idCollectionName);
            logFinest(stmtIdFiles);
            rs = stmtIdFiles.executeQuery();

            List<Long> idFiles = new ArrayList<Long>();

            while (rs.next()) {
                idFiles.add(rs.getLong(1));
            }

            stmt = con.prepareStatement(
                "UPDATE collections SET sequence_number = ?"
                + " WHERE id_collectionnnames = ? AND id_files = ?");

            int sequenceNumer = 0;

            for (Long idFile : idFiles) {
                stmt.setInt(1, sequenceNumer++);
                stmt.setLong(2, idCollectionName);
                stmt.setLong(3, idFile);
                logFiner(stmt);
                stmt.executeUpdate();
            }
        } finally {
            close(rs, stmtIdFiles);
            close(stmt);
        }
    }

    /**
     * Liefert, ob eine Bildsammlung existiert.
     *
     * @param collectionName Name der Bildsammlung
     * @return               true, wenn die Bildsammlung existiert
     */
    public boolean exists(String collectionName) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Liefert die Anzahl der Bildsammlungen.
     *
     * @return Anzahl oder -1 bei Datenbankfehlern
     */
    public int getCount() {
        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM collection_names";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Liefert die Anzahl aller Bilder in Bildsammlungen.
     *
     * @return Anzahl oder -1 bei Datenbankfehlern
     */
    public int getTotalImageCount() {
        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM collections";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    private boolean isImageIn(Connection con, String collectionName,
                              String filename)
            throws SQLException {
        boolean           isInCollection = false;
        PreparedStatement stmt           = null;
        ResultSet         rs             = null;

        try {
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM"
                + " collections INNER JOIN collection_names"
                + " ON collections.id_collectionnnames = collection_names.id"
                + " INNER JOIN files on collections.id_files = files.id"
                + " WHERE collection_names.name = ? AND files.filename = ?");
            stmt.setString(1, collectionName);
            stmt.setString(2, filename);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                isInCollection = rs.getInt(1) > 0;
            }
        } finally {
            close(rs, stmt);
        }

        return isInCollection;
    }

    private long findId(Connection con, String collectionname)
            throws SQLException {
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT id FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionname);
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

    public void addListener(DatabaseImageCollectionsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseImageCollectionsListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListeners(DatabaseImageCollectionsEvent.Type type,
                                 String collectionName,
                                 Collection<String> filenames) {
        DatabaseImageCollectionsEvent evt =
            new DatabaseImageCollectionsEvent(type, collectionName, filenames);
        Set<DatabaseImageCollectionsListener> listeners = listenerSupport.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
