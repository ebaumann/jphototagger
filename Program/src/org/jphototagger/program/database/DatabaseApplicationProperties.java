/*
 * @(#)DatabaseApplicationProperties.java    Created on 2009-08-28
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

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;

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
 * @author  Elmar Baumann
 */
public final class DatabaseApplicationProperties extends Database {
    public static final DatabaseApplicationProperties INSTANCE =
        new DatabaseApplicationProperties();
    private static final String VALUE_FALSE = "0";    // Never change that!
    private static final String VALUE_TRUE  = "1";    // Never change that!

    private DatabaseApplicationProperties() {}

    /**
     * Returns whether a key exists.
     *
     * @param  key key
     * @return     true if the key exists
     */
    public boolean existsKey(String key) {
        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM application WHERE key = ?";

            stmt = con.prepareStatement(sql);
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
            free(con);
        }

        return false;
    }

    /**
     * Deletes a key and its value.
     *
     * @param key key to delete
     */
    public void deleteKey(String key) {
        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            String sql = "DELETE FROM application WHERE key = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, key);
            logFiner(stmt);
            stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(stmt);
            free(con);
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
        String value = getString(key);

        return (value == null)
               ? false
               : value.equals(VALUE_TRUE);
    }

    /**
     * Inserts a boolean value or updates it if the key exists.
     *
     * @param key   key
     * @param value value to set
     */
    public void setBoolean(String key, boolean value) {
        setString(key, value
                       ? VALUE_TRUE
                       : VALUE_FALSE);
    }

    private String getInsertOrUpdateStmt(String key) {
        if (existsKey(key)) {
            return "UPDATE application SET value = ? WHERE key = ?";
        } else {
            return "INSERT INTO application (value, key) VALUES (?, ?)";
        }
    }

    /**
     * Sets a string.
     *
     * @param key    key
     * @param string string to set
     */
    public void setString(String key, String string) {
        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(getInsertOrUpdateStmt(key));
            stmt.setBytes(1, string.getBytes());
            stmt.setString(2, key);

            logFiner(stmt);
            stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(stmt);
            free(con);
        }
    }

    /**
     * Returns a string.
     *
     * @param  key key
     * @return     string or null if there is no such key in the database,
     *             the inserted string was null or on database errors
     */
    public String getString(String key) {
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        String            string = null;

        try {
            String sql = "SELECT value FROM application WHERE key = ?";

            con  = getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, key);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                byte[] bytes = rs.getBytes(1);

                if (rs.wasNull() || (bytes == null)) {
                    string = null;
                } else {
                    string = new String(bytes);
                }
            }
        } catch (Exception ex) {
            string = null;
            AppLogger.logSevere(DatabaseApplicationProperties.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return string;
    }
}
