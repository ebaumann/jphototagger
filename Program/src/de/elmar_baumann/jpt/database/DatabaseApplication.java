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
public final class DatabaseApplication extends Database {

    public static final DatabaseApplication INSTANCE = new DatabaseApplication();
    private static final String VALUE_TRUE = "1"; // NOI18N Never change that!
    private static final String VALUE_FALSE = "0"; // NOI18N Never change that!

    private DatabaseApplication() {
    }

    /**
     * Returns whether a key exists.
     *
     * @param  key key
     * @return     true if the key exists
     */
    public boolean existsKey(String key) {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM application WHERE key = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, key);
            AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt);
            ResultSet rs = stmt.executeQuery();
            int count = 0;

            if (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            return count > 0;
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseApplication.class, ex);
        } finally {
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
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            String sql = "DELETE FROM application WHERE key = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, key);
            AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseApplication.class, ex);
        } finally {
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
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(getQueryStmt());
            stmt.setString(1, key);
            AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String value = new String(rs.getBytes(1));
                if (!rs.wasNull()) {
                    isTrue = value.equals(VALUE_TRUE);
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseApplication.class, ex);
        } finally {
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
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt =
                    connection.prepareStatement(getInsertOrUpdateStmt(key));
            stmt.setBytes(1, value
                             ? VALUE_TRUE.getBytes()
                             : VALUE_FALSE.getBytes());
            if (!existsKey(key)) {
                stmt.setString(2, key);
            }
            AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt);
            int count = stmt.executeUpdate();
            stmt.close();
            assert count > 0 : "Not updated: " + key; // NOI18N
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseApplication.class, ex);
        } finally {
            free(connection);
        }
    }

    private String getQueryStmt() {
        return "SELECT value FROM application WHERE key = ?"; // NOI18N
    }

    private String getInsertOrUpdateStmt(String key) {
        if (existsKey(key)) {
            return "UPDATE application SET value = ? WHERE key = ?"; // NOI18N
        } else {
            return "INSERT INTO application (value, key) VALUES (?, ?)"; // NOI18N
        }
    }
}
