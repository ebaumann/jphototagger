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
package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.FavoriteDirectory;
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
public final class DatabaseFavoriteDirectories extends Database {

    public static final DatabaseFavoriteDirectories INSTANCE =
            new DatabaseFavoriteDirectories();

    private DatabaseFavoriteDirectories() {
    }

    /**
     * Fügt ein Favoritenverzeichnis ein. Existiert es bereits, wird es
     * aktualisiert.
     *
     * @param  favoriteDirectory  Favoritenverzeichnis
     * @return true bei Erfolg
     */
    public boolean insertOrUpdateFavoriteDirectory(
            FavoriteDirectory favoriteDirectory) {

        boolean inserted = false;
        Connection connection = null;
        try {
            if (existsFavoriteDirectory(favoriteDirectory.getFavoriteName())) {
                return updateFavoriteDirectory(
                        favoriteDirectory.getFavoriteName(), favoriteDirectory);
            }
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO favorite_directories" + // NOI18N
                    " (favorite_name" + // NOI18N -- 1 --
                    ", directory_name" + // NOI18N -- 2 --
                    ", favorite_index)" + // NOI18N -- 3 --
                    " VALUES (?, ?, ?)"); // NOI18N
            stmt.setString(1, favoriteDirectory.getFavoriteName());
            stmt.setString(2, favoriteDirectory.getDirectoryName());
            stmt.setInt(3, favoriteDirectory.getIndex());
            AppLog.logFiner(DatabaseFavoriteDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFavoriteDirectories.class, ex);
            rollback(connection);
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
    public boolean deleteFavoriteDirectory(String favoriteName) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM favorite_directories WHERE favorite_name = ?"); // NOI18N
            stmt.setString(1, favoriteName);
            AppLog.logFiner(DatabaseFavoriteDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFavoriteDirectories.class, ex);
            rollback(connection);
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
    public boolean updateFavoriteDirectory(String favoriteName,
            FavoriteDirectory favorite) {

        boolean updated = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE favorite_directories SET" + // NOI18N
                    " favorite_name = ?" + // NOI18N -- 1 --
                    ", directory_name = ?" + // NOI18N --2  --
                    ", favorite_index = ?" + // NOI18N -- 3 --
                    " WHERE favorite_name = ?"); // NOI18N -- 4 --
            stmt.setString(1, favorite.getFavoriteName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt(3, favorite.getIndex());
            stmt.setString(4, favoriteName);
            AppLog.logFiner(DatabaseFavoriteDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFavoriteDirectories.class, ex);
            rollback(connection);
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
            String sql =
                    "SELECT favorite_name" + // NOI18N -- 1 --
                    ", directory_name" + // NOI18N -- 2 --
                    ", favorite_index" + // NOI18N -- 3 --
                    " FROM favorite_directories" + // NOI18N
                    " ORDER BY favorite_index ASC"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                directories.add(new FavoriteDirectory(
                        rs.getString(1), rs.getString(2), rs.getInt(3)));
            }
            stmt.close();
        } catch (SQLException ex) {
            directories.clear();
            AppLog.logSevere(DatabaseFavoriteDirectories.class, ex);
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
                    "SELECT COUNT(*) FROM favorite_directories" + // NOI18N
                    " WHERE favorite_name = ?"); // NOI18N
            stmt.setString(1, favoriteName);
            AppLog.logFinest(DatabaseFavoriteDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
            exists = count > 0;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseFavoriteDirectories.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }
}
