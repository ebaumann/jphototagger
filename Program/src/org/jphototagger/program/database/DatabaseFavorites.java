package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Favorite;
import org.jphototagger.program.event.listener.DatabaseFavoritesListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

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
 * @author Elmar Baumann
 */
public final class DatabaseFavorites extends Database {
    public static final DatabaseFavorites INSTANCE = new DatabaseFavorites();
    private final ListenerSupport<DatabaseFavoritesListener> ls = new ListenerSupport<DatabaseFavoritesListener>();

    private DatabaseFavorites() {}

    public boolean insertOrUpdate(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        boolean inserted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            if (exists(favorite.getName())) {
                return update(favorite);
            }

            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO favorite_directories"
                                        + " (favorite_name, directory_name, favorite_index)" + " VALUES (?, ?, ?)");
            stmt.setString(1, favorite.getName());
            stmt.setString(2, getFilePath(favorite.getDirectory()));
            stmt.setInt(3, favorite.getIndex());
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            inserted = count > 0;

            if (inserted) {
                notifyInserted(favorite);
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

    public boolean delete(String favoriteName) {
        if (favoriteName == null) {
            throw new NullPointerException("favoriteName == null");
        }

        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            Favorite delFavorite = find(favoriteName);

            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM favorite_directories WHERE favorite_name = ?");
            stmt.setString(1, favoriteName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            deleted = count > 0;

            if (deleted && (delFavorite != null)) {
                notifyDeleted(delFavorite);
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

    public boolean updateRename(String fromFavoriteName, String toFavoriteName) {
        if (fromFavoriteName == null) {
            throw new NullPointerException("fromFavoriteName == null");
        }

        if (toFavoriteName == null) {
            throw new NullPointerException("toFavoriteName == null");
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            Favorite oldFavorite = find(fromFavoriteName);
            Connection con = getConnection();

            con.setAutoCommit(true);

            String sql = "UPDATE favorite_directories SET favorite_name = ?" + " WHERE favorite_name = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, toFavoriteName);
            stmt.setString(2, fromFavoriteName);
            logFiner(stmt);
            count = stmt.executeUpdate();

            if (count > 0) {
                notifyUpdated(oldFavorite, find(toFavoriteName));
            }
        } catch (SQLException ex) {
            AppLogger.logSevere(getClass(), ex);
        } finally {
            close(rs, stmt);
        }

        return count > 0;
    }

    /**
     * Calling if - <em>and only if</em> - some data in the favorite has been
     * updated <em>with exception of the favorite name</em>
     * ({@link Favorite#getName()}).
     * <p>
     * To rename a favorite, call {@link #updateRename(String, String)}.
     *
     * @param  favorite favorite
     * @return          true if updated
     */
    public boolean update(Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        boolean updated = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);

            Favorite oldFavorite = find(favorite.getId());

            stmt = con.prepareStatement("UPDATE favorite_directories SET"
                                        + " favorite_name = ?, directory_name = ?, favorite_index = ?"
                                        + " WHERE id = ?");
            stmt.setString(1, favorite.getName());
            stmt.setString(2, getFilePath(favorite.getDirectory()));
            stmt.setInt(3, favorite.getIndex());
            stmt.setLong(4, favorite.getId());
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            updated = count > 0;

            if (updated) {
                notifyUpdated(oldFavorite, favorite);
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

    public List<Favorite> getAll() {
        List<Favorite> favorites = new ArrayList<Favorite>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT id, favorite_name, directory_name, favorite_index"
                         + " FROM favorite_directories ORDER BY favorite_index ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Favorite favorite = new Favorite();

                favorite.setId(rs.getLong(1));
                favorite.setName(rs.getString(2));
                favorite.setDirectory(getFile(rs.getString(3)));
                favorite.setIndex(rs.getInt(4));
                favorites.add(favorite);
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

    private Favorite find(Long id) {
        Favorite favorite = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT id, favorite_name, directory_name, favorite_index"
                                        + " FROM favorite_directories WHERE id = ?");
            stmt.setLong(1, id);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                favorite = new Favorite();
                favorite.setId(rs.getLong(1));
                favorite.setName(rs.getString(2));
                favorite.setDirectory(getFile(rs.getString(3)));
                favorite.setIndex(rs.getInt(4));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return favorite;
    }

    private Favorite find(String favoriteName) {
        Favorite favorite = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT id, favorite_name, directory_name, favorite_index"
                                        + " FROM favorite_directories WHERE favorite_name = ?");
            stmt.setString(1, favoriteName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                favorite = new Favorite();
                favorite.setId(rs.getLong(1));
                favorite.setName(rs.getString(2));
                favorite.setDirectory(getFile(rs.getString(3)));
                favorite.setIndex(rs.getInt(4));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFavorites.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return favorite;
    }

    public boolean exists(String favoriteName) {
        if (favoriteName == null) {
            throw new NullPointerException("favoriteName == null");
        }

        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM favorite_directories" + " WHERE favorite_name = ?");
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
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseFavoritesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyInserted(Favorite favorite) {
        for (DatabaseFavoritesListener listener : ls.get()) {
            listener.favoriteInserted(favorite);
        }
    }

    private void notifyDeleted(Favorite favorite) {
        for (DatabaseFavoritesListener listener : ls.get()) {
            listener.favoriteDeleted(favorite);
        }
    }

    private void notifyUpdated(Favorite oldFavorite, Favorite updatedFavorite) {
        for (DatabaseFavoritesListener listener : ls.get()) {
            listener.favoriteUpdated(oldFavorite, updatedFavorite);
        }
    }
}
