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

import de.elmar_baumann.jpt.app.AppLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public boolean insert(String directoryName) {
        boolean inserted = false;
        if (!exists(directoryName)) {
            Connection connection = null;
            PreparedStatement stmt = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(true);
                stmt = connection.prepareStatement(
                        "INSERT INTO autoscan_directories (directory) VALUES (?)");
                stmt.setString(1, directoryName);
                logFiner(stmt);
                int count = stmt.executeUpdate();
                inserted = count > 0;
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
            } finally {
                close(stmt);
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
    public boolean insert(List<String> directoryNames) {
        boolean inserted = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                    "INSERT INTO autoscan_directories (directory) VALUES (?)");
            for (String directoryName : directoryNames) {
                if (!exists(directoryName)) {
                    stmt.setString(1, directoryName);
                    logFiner(stmt);
                    stmt.executeUpdate();
                }
            }
            connection.commit();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
    public boolean delete(String directoryName) {
        boolean deleted = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(
                        "DELETE FROM autoscan_directories WHERE directory = ?");
            stmt.setString(1, directoryName);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            deleted = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
        } finally {
            close(stmt);
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
    public boolean exists(String directoryName) {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM autoscan_directories WHERE directory = ?");
            stmt.setString(1, directoryName);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    /**
     * Liefet alle Verzeichnisse, die automatisch nach Metadaten zu scannen sind.
     *
     * @return Verzeichnisnamen
     */
    public List<String> getAll() {
        List<String> directories = new ArrayList<String>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            String sql = "SELECT directory FROM autoscan_directories" +
                         " ORDER BY directory ASC";
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                directories.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
            directories.clear();
        } finally {
            close(rs, stmt);
            free(connection);
        }

        return directories;
    }
}
