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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Database for the application's usage.
 *
 * Consider it as a registry ("INI" file). The name is not e.g.
 * <code>DatabaseRegistry</code> because future releases could use it in a
 * different way too.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-28
 */
public final class DatabaseApplicationProperties extends Database {

    public static final DatabaseApplicationProperties INSTANCE = new DatabaseApplicationProperties();
    private static final String VALUE_TRUE = "1"; // Never change that!
    private static final String VALUE_FALSE = "0"; // Never change that!

    private DatabaseApplicationProperties() {
    }

    /**
     * Returns whether a key exists.
     *
     * @param  key key
     * @return     true if the key exists
     */
    public boolean existsKey(String key) {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM application WHERE key = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, key);
            logFinest(stmt);
            rs = stmt.executeQuery();
            int count = 0;

            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return false;
    }

    /**
     * Deletes a key and it's value.
     *
     * @param key key to delete
     */
    public void deleteKey(String key) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            String sql = "DELETE FROM application WHERE key = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, key);
            logFinest(stmt);
            stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
    }

    /**
     * Returns wheter a value is true.
     *
     * @param  key key
     * @return     true if the value is true or false if the value is false or
     *             the key does not exist. You can check for the existence of
     *             a key with {@link #existsKey(String)}.
     */
    public boolean getBoolean(String key) {
        Connection connection = null;
        boolean isTrue = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            stmt = connection.prepareStatement(getQueryStmt());
            stmt.setString(1, key);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                String value = new String(rs.getBytes(1));
                if (!rs.wasNull()) {
                    isTrue = value.equals(VALUE_TRUE);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return isTrue;
    }

    /**
     * Inserts a boolean value or updates it if the key exists.
     *
     * @param key   key
     * @param value value to set
     */
    public void setBoolean(String key, boolean value) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(getInsertOrUpdateStmt(key));
            stmt.setBytes(1, value
                             ? VALUE_TRUE.getBytes()
                             : VALUE_FALSE.getBytes());
            if (!existsKey(key)) {
                stmt.setString(2, key);
            }
            logFinest(stmt);
            int count = stmt.executeUpdate();
            assert count > 0 : "Not updated: " + key;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
    }

    private String getQueryStmt() {
        return "SELECT value FROM application WHERE key = ?";
    }

    private String getInsertOrUpdateStmt(String key) {
        if (existsKey(key)) {
            return "UPDATE application SET value = ? WHERE key = ?";
        } else {
            return "INSERT INTO application (value, key) VALUES (?, ?)";
        }
    }
}
