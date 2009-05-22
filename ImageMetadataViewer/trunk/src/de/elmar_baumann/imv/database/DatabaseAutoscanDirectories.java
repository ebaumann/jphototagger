package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.DatabaseAction;
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
 * @version 2008/10/21
 */
public final class DatabaseAutoscanDirectories extends Database {

    public static final DatabaseAutoscanDirectories INSTANCE = new DatabaseAutoscanDirectories();

    private DatabaseAutoscanDirectories() {
    }

    /**
     * Fügt ein automatisch nach Metadaten zu scannendes Verzeichnis hinzu.
     *
     * @param  directoryName Verzeichnisname
     * @return true bei Erfolg
     */
    public boolean insertAutoscanDirectory(String directoryName) {
        boolean inserted = false;
        if (!existsAutoscanDirectory(directoryName)) {
            Connection connection = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(true);
                PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO autoscan_directories (directory) VALUES (?)"); // NOI18N
                stmt.setString(1, directoryName);
                AppLog.logFiner(DatabaseAutoscanDirectories.class, stmt.toString());
                int count = stmt.executeUpdate();
                inserted = count > 0;
                notifyDatabaseListener(
                    DatabaseAction.Type.AUTOSCAN_DIRECTORY_INSERTED, directoryName);
                stmt.close();
            } catch (SQLException ex) {
                AppLog.logWarning(DatabaseAutoscanDirectories.class, ex);
            } finally {
                free(connection);
            }
        }
        return inserted;
    }

    /**
     * Fügt ein automatisch nach Metadaten zu scannende Verzeichnisse hinzu.
     *
     * @param  directoryNames Verzeichnisnamen
     * @return true bei Erfolg
     */
    public boolean insertAutoscanDirectories(List<String> directoryNames) {
        
        boolean inserted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO autoscan_directories (directory) VALUES (?)"); // NOI18N
            for (String directoryName : directoryNames) {
                if (!existsAutoscanDirectory(directoryName)) {
                    stmt.setString(1, directoryName);
                    AppLog.logFiner(DatabaseAutoscanDirectories.class, stmt.toString());
                    stmt.executeUpdate();
                }
            }
            connection.commit();
            notifyDatabaseListener(
                DatabaseAction.Type.AUTOSCAN_DIRECTORIES_INSERTED, directoryNames);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseAutoscanDirectories.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Entfernt ein automatisch nach Metadaten zu scannendes Verzeichnis aus der
     * Datenbank.
     *
     * @param  directoryName Name des Verzeichnisses
     * @return true bei Erfolg
     */
    public boolean deleteAutoscanDirectory(String directoryName) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM autoscan_directories WHERE directory = ?"); // NOI18N
            stmt.setString(1, directoryName);
            AppLog.logFiner(DatabaseAutoscanDirectories.class, stmt.toString());
            int count = stmt.executeUpdate();
            deleted = count > 0;
            if (count > 0) {
                notifyDatabaseListener(
                    DatabaseAction.Type.AUTOSCAN_DIRECTORY_DELETED, directoryName);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseAutoscanDirectories.class, ex);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Liefert, ob ein automatisch nach Metadaten zu scannendes Verzeichnis
     * in der Datenbank existiert.
     *
     * @param  directoryName Verzeichnisname
     * @return true, wenn das Verzeichnis existiert
     */
    public boolean existsAutoscanDirectory(String directoryName) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM autoscan_directories WHERE directory = ?"); // NOI18N
            stmt.setString(1, directoryName);
            AppLog.logFinest(DatabaseAutoscanDirectories.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseAutoscanDirectories.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Liefet alle Verzeichnisse, die automatisch nach Metadaten zu scannen sind.
     *
     * @return Verzeichnisnamen
     */
    public List<String> getAutoscanDirectories() {
        List<String> directories = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT directory FROM autoscan_directories" + // NOI18N
                " ORDER BY directory ASC"); // NOI18N
            while (rs.next()) {
                directories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseAutoscanDirectories.class, ex);
            directories.clear();
        } finally {
            free(connection);
        }

        return directories;
    }
}
