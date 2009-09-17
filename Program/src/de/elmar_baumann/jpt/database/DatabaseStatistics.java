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
import de.elmar_baumann.jpt.database.metadata.Column;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseStatistics extends Database {

    public static final DatabaseStatistics INSTANCE = new DatabaseStatistics();

    /**
     * Liefert die Anzahl der Datensätze für verschiedene Spaltenwerte.
     *
     * @param  column  Spalte
     * @return Anzahl oder -1 bei Fehlern
     */
    public int getDistinctCount(Column column) {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String columnName = column.getName();
            String sql = "SELECT COUNT(*) FROM (SELECT DISTINCT " + // NOI18N
                    columnName +
                    " FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + columnName + " IS NOT NULL" + // NOI18N
                    ")"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Returns the count of records in a table for a specific column (where
     * the column value is not NULL).
     * 
     * @param  column  column
     * @return count count of records in the column's table where 
     * <code>column</code> is not null
     */
    public int getTotalRecordCount(Column column) {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + // NOI18N
                    column.getName() +
                    " IS NOT NULL"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Dateien in der Datenbank.
     *
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getFileCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM files"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Dateien mit XMP-Daten in der Datenbank.
     *
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getXmpCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT COUNT(*)" + // NOI18N
                    " FROM xmp LEFT JOIN files ON xmp.id_files = files.id"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl aller Datensätze in allen Tabellen.
     *
     * @return Anzahl oder -1 bei Fehlern
     */
    public long getTotalRecordCount() {
        long count = -1;
        Connection connection = null;
        List<String> tableNames = DatabaseTables.getTableNames();
        try {
            connection = getConnection();
            for (String tableName : tableNames) {
                Statement stmt = connection.createStatement();
                String sql = "SELECT COUNT(*) FROM " + tableName; // NOI18N
                AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    count += rs.getInt(1);
                }
                stmt.close();
            }
        } catch (SQLException ex) {
            count = -1;
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Returns whether one column in a list of columns has at least one value.
     * 
     * @param  columns  columns
     * @param  value    value
     * @return true if the value exists into the column
     */
    public boolean exists(List<Column> columns, String value) {
        boolean exists = false;
        Connection connection = null;
        try {
            connection = getConnection();
            int size = columns.size();
            for (int i = 0; !exists && i < size; i++) {
                Column column = columns.get(i);
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT COUNT(*) FROM " + // NOI18N
                        column.getTable().getName() +
                        " WHERE " + // NOI18N
                        column.getName() +
                        " = ?"); // NOI18N
                stmt.setString(1, value);
                AppLog.logFinest(DatabaseStatistics.class, AppLog.USE_STRING,
                        stmt.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
                stmt.close();
            }
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns whether a column has at least one value.
     * 
     * @param  column  column
     * @param  value   value
     * @return true if the value exists in the column
     */
    public boolean exists(Column column, String value) {
        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + // NOI18N
                    column.getName() +
                    " = ?"); // NOI18N
            stmt.setString(1, value);
            AppLog.logFinest(DatabaseStatistics.class, AppLog.USE_STRING,
                    stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseStatistics.class, ex);
        } finally {
            free(connection);
        }
        return count > 0;
    }

    private DatabaseStatistics() {
    }
}
