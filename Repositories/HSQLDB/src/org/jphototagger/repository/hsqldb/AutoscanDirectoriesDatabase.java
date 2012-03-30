package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.jphototagger.domain.repository.event.autoscandirectories.AutoscanDirectoryDeletedEvent;
import org.jphototagger.domain.repository.event.autoscandirectories.AutoscanDirectoryInsertedEvent;

/**
 * @author Elmar Baumann
 */
final class AutoscanDirectoriesDatabase extends Database {

    static final AutoscanDirectoriesDatabase INSTANCE = new AutoscanDirectoriesDatabase();
    private static final Logger LOGGER = Logger.getLogger(AutoscanDirectoriesDatabase.class.getName());

    private AutoscanDirectoriesDatabase() {
    }

    boolean insertDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }
        boolean inserted = false;
        if (!existsDirectory(directory)) {
            Connection con = null;
            PreparedStatement stmt = null;
            try {
                con = getConnection();
                con.setAutoCommit(true);
                stmt = con.prepareStatement("INSERT INTO autoscan_directories (directory) VALUES (?)");
                stmt.setString(1, directory.getAbsolutePath());
                LOGGER.log(Level.FINER, stmt.toString());
                int count = stmt.executeUpdate();
                inserted = count > 0;
                if (inserted) {
                    notifyInserted(directory);
                }
            } catch (Exception ex) {
                Logger.getLogger(AutoscanDirectoriesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                close(stmt);
                free(con);
            }
        }
        return inserted;
    }

    boolean deleteDirectory(File directory) {
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
            stmt.setString(1, directory.getAbsolutePath());
            LOGGER.log(Level.FINER, stmt.toString());
            int count = stmt.executeUpdate();
            deleted = count > 0;
            if (deleted) {
                notifyDeleted(directory);
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoscanDirectoriesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }
        return deleted;
    }

    boolean existsDirectory(File directory) {
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
            stmt.setString(1, directory.getAbsolutePath());
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoscanDirectoriesDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    List<File> getAllDirectories() {
        List<File> directories = new ArrayList<File>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            String sql = "SELECT directory FROM autoscan_directories ORDER BY directory ASC";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                directories.add(new File(rs.getString(1)));
            }
        } catch (Exception ex) {
            Logger.getLogger(AutoscanDirectoriesDatabase.class.getName()).log(Level.SEVERE, null, ex);
            directories.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }
        return directories;
    }

    private void notifyInserted(File dir) {
        EventBus.publish(new AutoscanDirectoryInsertedEvent(this, dir));
    }

    private void notifyDeleted(File dir) {
        EventBus.publish(new AutoscanDirectoryDeletedEvent(this, dir));
    }
}
