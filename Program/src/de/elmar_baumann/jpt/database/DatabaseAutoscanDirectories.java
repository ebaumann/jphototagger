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
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLog;
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
public final class DatabaseAutoscanDirectories extends Database {

    public static final DatabaseAutoscanDirectories INSTANCE =
            new DatabaseAutoscanDirectories();

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
                PreparedStatement stmt =
                        connection.prepareStatement(
                        "INSERT INTO autoscan_directories (directory) VALUES (?)"); // NOI18N
                stmt.setString(1, directoryName);
                AppLog.logFiner(DatabaseAutoscanDirectories.class,
                        AppLog.USE_STRING, stmt.toString());
                int count = stmt.executeUpdate();
                inserted = count > 0;
                stmt.close();
            } catch (SQLException ex) {
                AppLog.logSevere(DatabaseAutoscanDirectories.class, ex);
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
                    AppLog.logFiner(DatabaseAutoscanDirectories.class,
                            AppLog.USE_STRING, stmt.toString());
                    stmt.executeUpdate();
                }
            }
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseAutoscanDirectories.class, ex);
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
            AppLog.logFiner(DatabaseAutoscanDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            int count = stmt.executeUpdate();
            deleted = count > 0;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseAutoscanDirectories.class, ex);
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
            PreparedStatement stmt =
                    connection.prepareStatement(
                    "SELECT COUNT(*) FROM autoscan_directories WHERE directory = ?"); // NOI18N
            stmt.setString(1, directoryName);
            AppLog.logFinest(DatabaseAutoscanDirectories.class,
                    AppLog.USE_STRING, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseAutoscanDirectories.class, ex);
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
            String sql =
                    "SELECT directory FROM autoscan_directories" + // NOI18N
                    " ORDER BY directory ASC"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                directories.add(rs.getString(1));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseAutoscanDirectories.class, ex);
            directories.clear();
        } finally {
            free(connection);
        }

        return directories;
    }
}
