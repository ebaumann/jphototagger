/*
 * @(#)DatabaseFavorites.java    Created on 2008-10-21
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
 * @author  Elmar Baumann
 */
public final class DatabaseFavorites extends Database {
    public static final DatabaseFavorites                    INSTANCE        =
        new DatabaseFavorites();
    private final ListenerSupport<DatabaseFavoritesListener> listenerSupport =
        new ListenerSupport<DatabaseFavoritesListener>();

    private DatabaseFavorites() {}

    /**
     * Fügt ein Favoritenverzeichnis ein. Existiert es bereits, wird es
     * aktualisiert.
     *
     * @param  favorite  Favoritenverzeichnis
     * @return true bei Erfolg
     */
    public boolean insertOrUpdate(Favorite favorite) {
        boolean           inserted = false;
        Connection        con      = null;
        PreparedStatement stmt     = null;

        try {
            if (exists(favorite.getName())) {
                return update(favorite.getName(), favorite);
            }

            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "INSERT INTO favorite_directories"
                + " (favorite_name, directory_name, favorite_index)"
                + " VALUES (?, ?, ?)");
            stmt.setString(1, favorite.getName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt(3, favorite.getIndex());
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            inserted = count > 0;

            if (inserted) {
                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_INSERTED,
                               favorite, favorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
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
        boolean           deleted = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            Favorite delFavorite = find(favoriteName);

            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM favorite_directories WHERE favorite_name = ?");
            stmt.setString(1, favoriteName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            deleted = count > 0;

            if (deleted && (delFavorite != null)) {
                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_DELETED,
                               delFavorite, delFavorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
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
        boolean           updated = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "UPDATE favorite_directories SET"
                + " favorite_name = ?, directory_name = ?, favorite_index = ?"
                + " WHERE favorite_name = ?");
            stmt.setString(1, favorite.getName());
            stmt.setString(2, favorite.getDirectoryName());
            stmt.setInt(3, favorite.getIndex());
            stmt.setString(4, favoriteName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            updated = count > 0;

            if (updated) {
                Favorite newFavorite = find(favoriteName);

                notifyListener(DatabaseFavoritesEvent.Type.FAVORITE_UPDATED,
                               newFavorite, favorite);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
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
        Connection     con       = null;
        Statement      stmt      = null;
        ResultSet      rs        = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql =
                "SELECT favorite_name, directory_name, favorite_index"
                + " FROM favorite_directories ORDER BY favorite_index ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                favorites.add(new Favorite(rs.getString(1), rs.getString(2),
                                           rs.getInt(3)));
            }
        } catch (Exception ex) {
            favorites.clear();
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return favorites;
    }

    private Favorite find(String name) {
        Favorite          favorite = null;
        Connection        con      = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT" + " favorite_name, directory_name, favorite_index"
                + " FROM favorite_directories WHERE favorite_name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                favorite = new Favorite(rs.getString(1), rs.getString(2),
                                        rs.getInt(3));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
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
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM favorite_directories"
                + " WHERE favorite_name = ?");
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
            free(con);
        }

        return exists;
    }

    public void addListener(DatabaseFavoritesListener listener) {
        listenerSupport.add(listener);
    }

    public void removeListener(DatabaseFavoritesListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyListener(DatabaseFavoritesEvent.Type type,
                                Favorite favorite, Favorite oldFavorite) {
        DatabaseFavoritesEvent         evt       =
            new DatabaseFavoritesEvent(type, favorite);
        Set<DatabaseFavoritesListener> listeners = listenerSupport.get();

        evt.setOldFavorite(oldFavorite);

        synchronized (listeners) {
            for (DatabaseFavoritesListener listener : listeners) {
                listener.actionPerformed(evt);
            }
        }
    }
}
