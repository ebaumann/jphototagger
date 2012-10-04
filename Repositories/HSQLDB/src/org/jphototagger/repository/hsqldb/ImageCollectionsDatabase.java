package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionDeletedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionImagesDeletedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionImagesInsertedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionInsertedEvent;
import org.jphototagger.domain.repository.event.imagecollections.ImageCollectionRenamedEvent;

/**
 * @author Elmar Baumann
 */
final class ImageCollectionsDatabase extends Database {

    static final ImageCollectionsDatabase INSTANCE = new ImageCollectionsDatabase();
    private static final Logger LOGGER = Logger.getLogger(ImageCollectionsDatabase.class.getName());
    private final ImageFilesDatabase repo = ImageFilesDatabase.INSTANCE;

    private ImageCollectionsDatabase() {
    }

    List<String> getAllImageCollectionNames() {
        List<String> names = new ArrayList<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = "SELECT name FROM collection_names ORDER BY name";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString(1));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            names.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }
        return names;
    }

    List<ImageCollection> getAllImageCollections() {
        List<String> names = getAllImageCollectionNames();
        List<ImageCollection> collections = new ArrayList<ImageCollection>(names.size());
        for (String name : names) {
            collections.add(new ImageCollection(name, getImageFilesOfImageCollection(name)));
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
    int updateRenameImageCollection(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }
        if (toName == null) {
            throw new NullPointerException("toName == null");
        }
        if (ImageCollection.isSpecialCollection(fromName) || ImageCollection.isSpecialCollection(toName)) {
            return 0;
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("UPDATE collection_names SET name = ? WHERE name = ?");
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            if (count > 0) {
                notifyCollectionRenamed(fromName, toName);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return count;
    }

    List<File> getImageFilesOfImageCollection(String collectionName) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }
        List<File> imageFiles = new ArrayList<File>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT files.filename FROM"
                    + " collections INNER JOIN collection_names"
                    + " ON collections.id_collectionnname = collection_names.id"
                    + " INNER JOIN files ON collections.id_file = files.id"
                    + " WHERE collection_names.name = ?"
                    + " ORDER BY collections.sequence_number ASC");
            stmt.setString(1, collectionName);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                imageFiles.add(new File(rs.getString(1)));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            imageFiles.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }
        return imageFiles;
    }

    boolean insertImageCollection(ImageCollection collection) {
        if (collection == null) {
            throw new NullPointerException("collection == null");
        }
        return insertImageCollection(collection.getName(), collection.getFiles());
    }

    /**
     * Inserts an image collection into the database.
     * <p>
     * If an image collection of that name already existsValueInColumn, it will be deleted
     * before insertion.
     *
     * @param collectionName name of the image collection
     * @param imageFiles     ordered image files
     * @return               true if successfully inserted
     */
    boolean insertImageCollection(String collectionName, List<File> imageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        boolean added = false;
        if (existsImageCollection(collectionName)) {
            deleteImageCollection(collectionName);
        }
        Connection con = null;
        PreparedStatement stmtName = null;
        PreparedStatement stmtColl = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmtName = con.prepareStatement("INSERT INTO collection_names (name) VALUES (?)");
            stmtColl = con.prepareStatement("INSERT INTO collections"
                    + " (id_collectionnname, id_file, sequence_number)"
                    + " VALUES (?, ?, ?)");
            stmtName.setString(1, collectionName);
            LOGGER.log(Level.FINER, stmtName.toString());
            stmtName.executeUpdate();
            long idCollectionName = findId(con, collectionName);
            int sequence_number = 0;
            for (File imageFile : imageFiles) {
                long idImageFile = repo.findIdImageFile(con, imageFile);
                if (!repo.existsImageFile(imageFile)) {
                    LOGGER.log(Level.WARNING, "File ''{0}'' is not in the database! No photo album will be created!", imageFile);
                    rollback(con);

                    return false;
                }
                stmtColl.setLong(1, idCollectionName);
                stmtColl.setLong(2, idImageFile);
                stmtColl.setInt(3, sequence_number++);
                LOGGER.log(Level.FINER, stmtColl.toString());
                stmtColl.executeUpdate();
            }
            con.commit();
            added = true;
            notifyCollectionInserted(collectionName, imageFiles);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    boolean deleteImageCollection(String collectioNname) {
        if (collectioNname == null) {
            throw new NullPointerException("collectioNname == null");
        }
        if (ImageCollection.isSpecialCollection(collectioNname)) {
            return false;
        }
        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            List<File> delFiles = getImageFilesOfImageCollection(collectioNname);
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("DELETE FROM collection_names WHERE name = ?");
            stmt.setString(1, collectioNname);
            LOGGER.log(Level.FINER, stmt.toString());
            stmt.executeUpdate();
            deleted = true;
            notifyCollectionDeleted(collectioNname, delFiles);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
     * @param imageFiles     image files to deleteImageFiles
     * @return               count of deleted images
     */
    int deleteImagesFromImageCollection(String collectionName, List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        int delCount = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM collections WHERE id_collectionnname = ? AND id_file = ?");
            List<File> deletedFiles = new ArrayList<File>(imageFiles.size());
            for (File imageFile : imageFiles) {
                int prevDelCount = delCount;
                long idCollectionName = findId(con, collectionName);
                long idFile = repo.findIdImageFile(con, imageFile);
                stmt.setLong(1, idCollectionName);
                stmt.setLong(2, idFile);
                LOGGER.log(Level.FINER, stmt.toString());
                delCount += stmt.executeUpdate();
                if (prevDelCount < delCount) {
                    deletedFiles.add(imageFile);
                }
                reorderSequenceNumber(con, collectionName);
            }
            con.commit();
            notifyImagesDeleted(collectionName, deletedFiles);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    boolean insertImagesIntoImageCollection(String collectionName, List<File> imageFiles) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }
        boolean added = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            if (existsImageCollection(collectionName)) {
                con = getConnection();
                con.setAutoCommit(false);
                stmt = con.prepareStatement("INSERT INTO collections"
                        + " (id_file, id_collectionnname, sequence_number)"
                        + " VALUES (?, ?, ?)");
                long idCollectionNames = findId(con, collectionName);
                int sequence_number = getMaxSequenceNumber(con, collectionName) + 1;
                List<File> insertedFiles = new ArrayList<File>(imageFiles.size());
                for (File imageFile : imageFiles) {
                    if (!isImageIn(con, collectionName, imageFile)) {
                        long idFiles = repo.findIdImageFile(con, imageFile);
                        stmt.setLong(1, idFiles);
                        stmt.setLong(2, idCollectionNames);
                        stmt.setInt(3, sequence_number++);
                        LOGGER.log(Level.FINER, stmt.toString());
                        stmt.executeUpdate();
                        insertedFiles.add(imageFile);
                    }
                }
                reorderSequenceNumber(con, collectionName);
                notifyImagesInserted(collectionName, insertedFiles);
            } else {
                return insertImageCollection(collectionName, imageFiles);
            }
            con.commit();
            added = true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return added;
    }

    private int getMaxSequenceNumber(Connection con, String collectionName) throws SQLException {
        int max = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT MAX(collections.sequence_number)"
                    + " FROM collections INNER JOIN collection_names"
                    + " ON collections.id_collectionnname = collection_names.id"
                    + " AND collection_names.name = ?");
            stmt.setString(1, collectionName);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                max = rs.getInt(1);
            }
        } finally {
            close(rs, stmt);
        }
        return max;
    }

    private void reorderSequenceNumber(Connection con, String collectionName) throws SQLException {
        long idCollectionName = findId(con, collectionName);
        PreparedStatement stmtIdFiles = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmtIdFiles = con.prepareStatement("SELECT id_file FROM collections WHERE id_collectionnname = ?"
                    + " ORDER BY collections.sequence_number ASC");
            stmtIdFiles.setLong(1, idCollectionName);
            rs = stmtIdFiles.executeQuery();
            List<Long> idFiles = new ArrayList<Long>();
            while (rs.next()) {
                idFiles.add(rs.getLong(1));
            }
            stmt = con.prepareStatement("UPDATE collections SET sequence_number = ?"
                    + " WHERE id_collectionnname = ? AND id_file = ?");
            LOGGER.log(Level.FINEST, stmt.toString());
            int sequenceNumer = 0;
            for (Long idFile : idFiles) {
                stmt.setInt(1, sequenceNumer++);
                stmt.setLong(2, idCollectionName);
                stmt.setLong(3, idFile);
                LOGGER.log(Level.FINER, stmt.toString());
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
     * @return               true if an image collection of that name existsValueInColumn
     */
    boolean existsImageCollection(String collectionName) {
        if (collectionName == null) {
            throw new NullPointerException("collectionName == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionName);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    int getImageCollectionCount() {
        int count = -1;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = "SELECT COUNT(*) FROM collection_names";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    int getImageCountOfAllImageCollections() {
        int count = -1;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = "SELECT COUNT(*) FROM collections";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count;
    }

    private boolean isImageIn(Connection con, String collectionName, File imageFile) throws SQLException {
        boolean isInCollection = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM"
                    + " collections INNER JOIN collection_names"
                    + " ON collections.id_collectionnname = collection_names.id"
                    + " INNER JOIN files on collections.id_file = files.id"
                    + " WHERE collection_names.name = ? AND files.filename = ?");
            stmt.setString(1, collectionName);
            stmt.setString(2, imageFile.getAbsolutePath());
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                isInCollection = rs.getInt(1) > 0;
            }
        } finally {
            close(rs, stmt);
        }
        return isInCollection;
    }

    private long findId(Connection con, String collectionname) throws SQLException {
        long id = -1;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement("SELECT id FROM collection_names WHERE name = ?");
            stmt.setString(1, collectionname);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }
        return id;
    }

    private void notifyImagesInserted(String collectionName, List<File> insertedImageFiles) {
        EventBus.publish(new ImageCollectionImagesInsertedEvent(this, collectionName, insertedImageFiles));
    }

    private void notifyImagesDeleted(String collectionName, List<File> deletedImageFiles) {
        EventBus.publish(new ImageCollectionImagesDeletedEvent(this, collectionName, deletedImageFiles));
    }

    private void notifyCollectionInserted(String collectionName, List<File> insertedImageFiles) {
        EventBus.publish(new ImageCollectionInsertedEvent(this, collectionName, insertedImageFiles));
    }

    private void notifyCollectionDeleted(String collectionName, List<File> deletedImageFiles) {
        EventBus.publish(new ImageCollectionDeletedEvent(this, collectionName, deletedImageFiles));
    }

    private void notifyCollectionRenamed(String fromName, String toName) {
        EventBus.publish(new ImageCollectionRenamedEvent(this, fromName, toName));
    }
}
