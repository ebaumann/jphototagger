/*
 * JPhotoTagger tags and finds images fast.
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
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.event.DatabaseFavoritesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseFavoritesListener;
import de.elmar_baumann.jpt.event.listener.impl.ListenerSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseFavorites extends Database {

    public static final DatabaseFavorites                          INSTANCE        = new DatabaseFavorites();
    private final       ListenerSupport<DatabaseFavoritesListener> listenerSupport = new ListenerSupport<DatabaseFavoritesListener>();

    /**
     * Fügt ein Favoritenverzeichnis ein. Existiert es bereits, wird es
     * aktualisiert.
     *
     * @param  favorite  Favoritenverzeichnis
     * @return true bei Erfolg
     */
    public boolean insertOrUpdate(Favorite favorite) {

        boolean inserted = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            if (exists(favorite.getName())) {
                return update(favorite.getName(), favorite);
            }
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement("INSERT INTO favorite_directories" +
                                               " (favorite_name" +   // -- 1 --
                                               ", directory_name" +  // -- 2 --
                                               ", favorite_index)" + // -- 3 --
                                               " VALUES (?, ?, ?)");
            stmt.setString(1, favorite.getName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt   (3, favorite.getIndex());
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            inserted = count > 0;
            if (inserted) {
                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_INSERTED, favorite, favorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
    public boolean delete(String favoriteName) {
        boolean deleted = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            Favorite delFavorite = find(favoriteName);
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                    "DELETE FROM favorite_directories WHERE favorite_name = ?");
            stmt.setString(1, favoriteName);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            if (deleted && delFavorite != null) {
                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_DELETED, delFavorite, delFavorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
    public boolean update(String favoriteName, Favorite favorite) {

        boolean updated = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement("UPDATE favorite_directories SET" +
                                               " favorite_name = ?" +       // -- 1 --
                                               ", directory_name = ?" +     // -- 2 --
                                               ", favorite_index = ?" +     // -- 3 --
                                               " WHERE favorite_name = ?"); // -- 4 --
            stmt.setString(1, favorite.getName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt   (3, favorite.getIndex());
            stmt.setString(4, favoriteName);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            updated = count > 0;
            if (updated) {
                Favorite newFavorite = find(favoriteName);
                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_UPDATED, newFavorite, favorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return updated;
    }

    /**
     * Liefert alle Favoritenverzeichnisse.
     *
     * @return Favoritenverzeichnisse
     */
    public List<Favorite> getAll() {
        List<Favorite> favorites = new ArrayList<Favorite>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            String sql = "SELECT favorite_name" + // -- 1 --
                         ", directory_name" +     // -- 2 --
                         ", favorite_index" +     // -- 3 --
                         " FROM favorite_directories" +
                         " ORDER BY favorite_index ASC";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                favorites.add(new Favorite(rs.getString(1), rs.getString(2), rs.getInt(3)));
            }
        } catch (Exception ex) {
            favorites.clear();
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return favorites;
    }

    private Favorite find(String name) {
        Favorite          favorite   = null;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement("SELECT favorite_name" + // -- 1 --
                                               ", directory_name" +     // -- 2 --
                                               ", favorite_index" +     // -- 3 --
                                               " FROM favorite_directories" +
                                               " WHERE favorite_name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                favorite = new Favorite(rs.getString(1), rs.getString(2), rs.getInt(3));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return favorite;
    }


    /**
     * Liefert, ob ein Favoritenverzeichnis existiert.
     *
     * @param  favoriteName  Name des Favoriten (Alias)
     * @return true wenn existent
     */
    public boolean exists(String favoriteName) {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM favorite_directories" +
                    " WHERE favorite_name = ?");
            stmt.setString(1, favoriteName);
            logFinest(stmt);
            rs = stmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            exists = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    public void addListener(DatabaseFavoritesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseFavoritesListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListener(DatabaseFavoritesEvent.Type type, Favorite favorite, Favorite oldFavorite) {
        DatabaseFavoritesEvent         evt       = new DatabaseFavoritesEvent(type, favorite);
        Set<DatabaseFavoritesListener> listeners = listenerSupport.get();

        evt.setOldFavorite(oldFavorite);
        synchronized (listeners) {
            for (DatabaseFavoritesListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }

    private DatabaseFavorites() {
    }
}
