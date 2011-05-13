package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.DatabaseAutoscanDirectoriesListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseAutoscanDirectories extends Database {
    public static final DatabaseAutoscanDirectories INSTANCE = new DatabaseAutoscanDirectories();
    private final ListenerSupport<DatabaseAutoscanDirectoriesListener> ls =
        new ListenerSupport<DatabaseAutoscanDirectoriesListener>();

    private DatabaseAutoscanDirectories() {}

    public boolean insert(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        boolean inserted = false;

        if (!exists(directory)) {
            Connection con = null;
            PreparedStatement stmt = null;

            try {
                con = getConnection();
                con.setAutoCommit(true);
                stmt = con.prepareStatement("INSERT INTO autoscan_directories (directory) VALUES (?)");
                stmt.setString(1, getFilePath(directory));
                logFiner(stmt);

                int count = stmt.executeUpdate();

                inserted = count > 0;

                if (inserted) {
                    notifyInserted(directory);
                }
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
            } finally {
                close(stmt);
                free(con);
            }
        }

        return inserted;
    }

    public boolean delete(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("DELETE FROM autoscan_directories WHERE directory = ?");
            stmt.setString(1, getFilePath(directory));
            logFiner(stmt);

            int count = stmt.executeUpdate();

            deleted = count > 0;

            if (deleted) {
                notifyDeleted(directory);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    public boolean exists(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM autoscan_directories WHERE directory = ?");
            stmt.setString(1, getFilePath(directory));
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public List<File> getAll() {
        List<File> directories = new ArrayList<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT directory FROM autoscan_directories ORDER BY directory ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                directories.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
            directories.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return directories;
    }

    public void addListener(DatabaseAutoscanDirectoriesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseAutoscanDirectoriesListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyInserted(File dir) {
        for (DatabaseAutoscanDirectoriesListener listener : ls.get()) {
            listener.directoryInserted(dir);
        }
    }

    private void notifyDeleted(File dir) {
        for (DatabaseAutoscanDirectoriesListener listener : ls.get()) {
            listener.directoryDeleted(dir);
        }
    }
}
