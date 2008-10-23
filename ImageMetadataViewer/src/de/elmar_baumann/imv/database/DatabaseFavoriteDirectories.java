package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.data.FavoriteDirectory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public class DatabaseFavoriteDirectories extends Database {
    
    private static DatabaseFavoriteDirectories instance = new DatabaseFavoriteDirectories();
    
    public static DatabaseFavoriteDirectories getInstance() {
        return instance;
    }
    
    private DatabaseFavoriteDirectories() {
    }

    /**
     * Fügt ein Favoritenverzeichnis ein. Existiert es bereits, wird es
     * aktualisiert.
     *
     * @param  favoriteDirectory  Favoritenverzeichnis
     * @return true bei Erfolg
     */
    public synchronized boolean insertFavoriteDirectory(FavoriteDirectory favoriteDirectory) {
        boolean inserted = false;
        Connection connection = null;
        try {
            if (existsFavoriteDirectory(favoriteDirectory.getFavoriteName())) {
                return updateFavoriteDirectory(favoriteDirectory.getFavoriteName(), favoriteDirectory);
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
                handleException(ex1, Level.SEVERE);
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
    public synchronized boolean deleteFavoriteDirectory(String favoriteName) {
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
                handleException(ex1, Level.SEVERE);
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
    public synchronized boolean updateFavoriteDirectory(String favoriteName, FavoriteDirectory favorite) {
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
                handleException(ex1, Level.SEVERE);
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
                directories.add(new FavoriteDirectory(rs.getString(1), rs.getString(2), rs.getInt(3)));
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

}
