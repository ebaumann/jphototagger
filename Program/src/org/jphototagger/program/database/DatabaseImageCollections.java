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

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.ImageCollection;
import org.jphototagger.program.event.listener.DatabaseImageCollectionsListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
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
    private final ListenerSupport<DatabaseImageCollectionsListener> ls =
        new ListenerSupport<DatabaseImageCollectionsListener>();

    private DatabaseImageCollections() {}

    /**
     * Returns the names of all image collections.
     *
     * @return names
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
            collections.add(new ImageCollection(name, getImageFilesOf(name)));
        }

        return collections;
    }

    /**
     * Renames an image collection
     *
     * @param fromName old name
     * @param toName   new name
     * @return         count of renamed image collections (0 or 1)
     */
    public int updateRename(String fromName, String toName) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE collection_names SET name = ? WHERE name = ?");
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            logFiner(stmt);
            count = stmt.executeUpdate();

            if (count > 0) {
                notifyCollectionRenamed(fromName, toName);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
    }

    public List<File> getImageFilesOf(String collectionName) {
        List<File>        imageFiles = new ArrayList<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT files.filename FROM"
                + " collections INNER JOIN collection_names"
                + " ON collections.id_collectionnname = collection_names.id"
                + " INNER JOIN files ON collections.id_file = files.id"
                + " WHERE collection_names.name = ?"
                + " ORDER BY collections.sequence_number ASC");
            stmt.setString(1, collectionName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
            imageFiles.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return imageFiles;
    }

    public boolean insert(ImageCollection collection) {
        return insert(collection.getName(), collection.getFiles());
    }

    /**
     * Inserts an image collection into the database.
     * <p>
     * If an image collection of that name already exists, it will be deleted
     * before insertion.
     *
     * @param collectionName name of the image collection
     * @param imageFiles     ordered image files
     * @return               true if successfully inserted
     * @see                  #exists(java.lang.String)
     */
    public boolean insert(String collectionName, List<File> imageFiles) {
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
                + " (id_collectionnname, id_file, sequence_number)"
                + " VALUES (?, ?, ?)");
            stmtName.setString(1, collectionName);
            logFiner(stmtName);
            stmtName.executeUpdate();

            long idCollectionName = findId(con, collectionName);
            int  sequence_number  = 0;

            for (File imageFile : imageFiles) {
                long idImageFile =
                    DatabaseImageFiles.INSTANCE.findIdImageFile(con, imageFile);

                if (!DatabaseImageFiles.INSTANCE.exists(imageFile)) {
                    AppLogger.logWarning(
                        getClass(),
                        "DatabaseImageCollections.Error.Insert.FileId",
                        imageFile);
                    rollback(con);

                    return false;
                }

                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idImageFile);
                stmtColl.setInt(3, sequence_number++);
                logFiner(stmtColl);
                stmtColl.executeUpdate();
            }

            con.commit();
            added = true;
            notifyCollectionInserted(collectionName, imageFiles);
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
     * Deletes an image collection.
     *
     * @param collectioNname name of the image collection
     * @return               true if successfully deleted
     */
    public boolean delete(String collectioNname) {
        boolean           deleted = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            List<File> delFiles = getImageFilesOf(collectioNname);

            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "DELETE FROM collection_names WHERE name = ?");
            stmt.setString(1, collectioNname);
            logFiner(stmt);
            stmt.executeUpdate();
            deleted = true;
            notifyCollectionDeleted(collectioNname, delFiles);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseImageCollections.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    /**
     * Deletes image files from an image collection.
     *
     * @param collectionName name of the image collection
     * @param imageFiles     image files to delete
     * @return               count of deleted images
     */
    public int deleteImagesFrom(String collectionName, List<File> imageFiles) {
        int               delCount = 0;
        Connection        con      = null;
        PreparedStatement stmt     = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM collections"
                + " WHERE id_collectionnname = ? AND id_file = ?");

            List<File> deletedFiles = new ArrayList<File>(imageFiles.size());

            for (File imageFile : imageFiles) {
                int  prevDelCount     = delCount;
                long idCollectionName = findId(con, collectionName);
                long idFile           =
                    DatabaseImageFiles.INSTANCE.findIdImageFile(con, imageFile);

                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                logFiner(stmt);
                delCount += stmt.executeUpdate();

                if (prevDelCount < delCount) {
                    deletedFiles.add(imageFile);
                }

                reorderSequenceNumber(con, collectionName);
            }

            con.commit();
            notifyImagesDeleted(collectionName, deletedFiles);
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
     * Adds image files to an image collection.
     *
     * @param collectionName name of the image collection, will be created, if
     *                       no image collection has that name
     * @param imageFiles     image files to add. If that collection already
     *                       contains a file, it will not be added
     * @return               true if successfully inserted
     */
    public boolean insertImagesInto(String collectionName,
                                    List<File> imageFiles) {
        boolean           added = false;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            if (exists(collectionName)) {
                con = getConnection();
                con.setAutoCommit(false);
                stmt = con.prepareStatement(
                    "INSERT INTO collections"
                    + " (id_file, id_collectionnname, sequence_number)"
                    + " VALUES (?, ?, ?)");

                long idCollectionNames = findId(con, collectionName);
                int  sequence_number   = getMaxSequenceNumber(con,
                                             collectionName) + 1;
                List<File> insertedFiles =
                    new ArrayList<File>(imageFiles.size());

                for (File imageFile : imageFiles) {
                    if (!isImageIn(con, collectionName, imageFile)) {
                        long idFiles =
                            DatabaseImageFiles.INSTANCE.findIdImageFile(con,
                                imageFile);

                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        logFiner(stmt);
                        stmt.executeUpdate();
                        insertedFiles.add(imageFile);
                    }
                }

                reorderSequenceNumber(con, collectionName);
                notifyImagesInserted(collectionName, insertedFiles);
            } else {
                return insert(collectionName, imageFiles);
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
                + " ON collections.id_collectionnname = collection_names.id"
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
                "SELECT id_file FROM collections WHERE id_collectionnname = ?"
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
                + " WHERE id_collectionnname = ? AND id_file = ?");

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
     * Returns whether an image collection of a specific name does exist.
     *
     * @param collectionName name of the image collection
     * @return               true if an image collection of that name exists
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
     * Returns the count of image collections.
     *
     * @return count or -1 on database errors
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
     * Returns the sum of all images in all image collections.
     *
     * @return count or -1 on database errors
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
                              File imageFile)
            throws SQLException {
        boolean           isInCollection = false;
        PreparedStatement stmt           = null;
        ResultSet         rs             = null;

        try {
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM"
                + " collections INNER JOIN collection_names"
                + " ON collections.id_collectionnname = collection_names.id"
                + " INNER JOIN files on collections.id_file = files.id"
                + " WHERE collection_names.name = ? AND files.filename = ?");
            stmt.setString(1, collectionName);
            stmt.setString(2, getFilePath(imageFile));
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
        ls.add(listener);
    }

    public void removeListener(DatabaseImageCollectionsListener listener) {
        ls.remove(listener);
    }

    private void notifyImagesInserted(String collectionName,
                                      List<File> insertedImageFiles) {
        Set<DatabaseImageCollectionsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.imagesInserted(collectionName, insertedImageFiles);
            }
        }
    }

    private void notifyImagesDeleted(String collectionName,
                                     List<File> deletedImageFiles) {
        Set<DatabaseImageCollectionsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.imagesDeleted(collectionName, deletedImageFiles);
            }
        }
    }

    private void notifyCollectionInserted(String collectionName,
            List<File> insertedImageFiles) {
        Set<DatabaseImageCollectionsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.collectionInserted(collectionName, insertedImageFiles);
            }
        }
    }

    private void notifyCollectionDeleted(String collectionName,
            List<File> deletedImageFiles) {
        Set<DatabaseImageCollectionsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.collectionDeleted(collectionName, deletedImageFiles);
            }
        }
    }

    private void notifyCollectionRenamed(String oldName, String newName) {
        Set<DatabaseImageCollectionsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseImageCollectionsListener listener : listeners) {
                listener.collectionRenamed(oldName, newName);
            }
        }
    }
}
