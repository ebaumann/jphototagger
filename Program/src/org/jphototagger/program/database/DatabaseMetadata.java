/*
 * @(#)DatabaseMetadata.java    Created on 2008-10-23
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

import org.jphototagger.program.app.AppInfo;
import org.jphototagger.lib.util.Version;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class DatabaseMetadata extends Database {
    public static final DatabaseMetadata INSTANCE               =
        new DatabaseMetadata();
    private static final String          KEY_JPT_APP_DB_VERSION =
        "VersionLastDbUpdate";

    private DatabaseMetadata() {}

    /**
     * Returns, whether the database is the database of a newer JPhotoTagger
     * version.
     *
     * @return true, if the database is for a newer version
     * @since  0.8.3
     */
    public static boolean isDatabaseOfNewerVersion() {
        String dbVersion = DatabaseApplicationProperties.INSTANCE.getString(
                               KEY_JPT_APP_DB_VERSION);

        // Only older JPhotoTagger versions did not write that key
        if (dbVersion == null) {
            return false;
        }

        Version db      = Version.parseVersion(dbVersion, ".");
        Version current = Version.parseVersion(AppInfo.APP_VERSION, ".");

        return current.compareTo(db) < 0;
    }

    /**
     * Returns, whether the database is the database of this JPhotoTagger
     * version.
     *
     * @return true, if the database is from this version
     * @since  0.8.3
     */
    public static boolean isDatabaseOfCurrentVersion() {
        String dbVersion = DatabaseApplicationProperties.INSTANCE.getString(
                               KEY_JPT_APP_DB_VERSION);

        return (dbVersion == null)
               ? false
               : dbVersion.equals(AppInfo.APP_VERSION);
    }

    /**
     * Returns, whether the database is the database of an older JPhotoTagger
     * version.
     *
     * @return true, if the database is from an older version
     * @since  0.8.3
     */
    public static boolean isDatabaseOfOlderVersion() {
        String dbVersion = DatabaseApplicationProperties.INSTANCE.getString(
                               KEY_JPT_APP_DB_VERSION);

        // Only older JPhotoTagger versions did not write that key
        if (dbVersion == null) {
            return true;
        }

        Version db      = Version.parseVersion(dbVersion, ".");
        Version current = Version.parseVersion(AppInfo.APP_VERSION, ".");

        return current.compareTo(db) > 0;
    }

    /**
     * Returns the application version written by
     * {@link #setCurrentAppVersionToDatabase()}.
     *
     * @return version string or null if not written
     */
    public static String getDatabaseAppVersion() {
        return DatabaseApplicationProperties.INSTANCE.getString(
            KEY_JPT_APP_DB_VERSION);
    }

    /**
     * Sets the current application version {@link AppInfo#APP_VERSION} into
     * the database.
     * <p>
     * <em>Use only, if ensured, that the database layout fits to the current
     * version!</em>
     */
    public static void setCurrentAppVersionToDatabase() {
        DatabaseApplicationProperties.INSTANCE.setString(
            KEY_JPT_APP_DB_VERSION, AppInfo.APP_VERSION);
    }

    public boolean existsTable(Connection con, String tablename)
            throws SQLException {
        boolean          exists = false;
        DatabaseMetaData dbm    = con.getMetaData();
        String[]         names  = { "TABLE" };
        ResultSet        rs     = dbm.getTables(null, "%", "%", names);

        while (!exists && rs.next()) {
            exists = rs.getString("TABLE_NAME").equalsIgnoreCase(tablename);
        }

        rs.close();

        return exists;
    }

    public boolean existsColumn(Connection con, String tableName,
                                String columnName)
            throws SQLException {
        if (!existsTable(con, tableName)) {
            return false;
        }

        Statement         stmt   = null;
        ResultSet         rs     = null;
        ResultSetMetaData rsmd   = null;
        boolean           exists = false;

        try {
            stmt = con.createStatement();

            // "WHERE 1 = 0": speed, memory!
            String sql = "select * from " + tableName + " WHERE 1 = 0";

            rs   = stmt.executeQuery(sql);
            rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

            for (int i = 1; !exists && (i <= columnCount); i++) {
                String column = rsmd.getColumnName(i);

                exists = column.equalsIgnoreCase(columnName);
            }
        } finally {
            close(rs, stmt);
        }

        return exists;
    }

    /**
     * Returns information of one or all columns of a specific table.
     *
     * @param  con        connection
     * @param  tableName  table nam
     * @param  columnName column name pattern or null for all columns
     * @return            Information
     * @throws SQLException
     */
    public List<ColumnInfo> getColumnInfo(Connection con, String tableName,
            String columnName)
            throws SQLException {
        ResultSet        rs    = null;
        List<ColumnInfo> infos = new ArrayList<ColumnInfo>();

        try {
            DatabaseMetaData meta = con.getMetaData();

            rs = meta.getColumns(null, null, tableName.toUpperCase(),
                                 (columnName == null)
                                 ? "%"
                                 : columnName.toUpperCase());

            while (rs.next()) {
                ColumnInfo colInfo = new ColumnInfo();

                colInfo.CHAR_OCTET_LENGTH = rs.getInt("CHAR_OCTET_LENGTH");
                colInfo.COLUMN_DEF        = rs.getString("COLUMN_DEF");
                colInfo.COLUMN_NAME       = rs.getString("COLUMN_NAME");
                colInfo.COLUMN_SIZE       = rs.getInt("COLUMN_SIZE");
                colInfo.DATA_TYPE         = rs.getInt("DATA_TYPE");
                colInfo.DECIMAL_DIGITS    = rs.getInt("DECIMAL_DIGITS");
                colInfo.IS_NULLABLE       = rs.getString("IS_NULLABLE");
                colInfo.NULLABLE          = rs.getInt("NULLABLE");
                colInfo.NUM_PREC_RADIX    = rs.getInt("NUM_PREC_RADIX");
                colInfo.ORDINAL_POSITION  = rs.getInt("ORDINAL_POSITION");
                colInfo.REMARKS           = rs.getString("REMARKS");
                colInfo.SCOPE_CATLOG      = rs.getString("SCOPE_CATLOG");
                colInfo.SCOPE_SCHEMA      = rs.getString("SCOPE_SCHEMA");
                colInfo.SCOPE_TABLE       = rs.getString("SCOPE_TABLE");
                colInfo.SOURCE_DATA_TYPE  = rs.getShort("SOURCE_DATA_TYPE");
                colInfo.SQL_DATA_TYPE     = rs.getInt("SQL_DATA_TYPE");
                colInfo.SQL_DATETIME_SUB  = rs.getInt("SQL_DATETIME_SUB");
                colInfo.TABLE_CAT         = rs.getString("TABLE_CAT");
                colInfo.TABLE_NAME        = rs.getString("TABLE_NAME");
                colInfo.TABLE_SCHEM       = rs.getString("TABLE_SCHEM");
                colInfo.TYPE_NAME         = rs.getString("TYPE_NAME");
                infos.add(colInfo);
            }
        } finally {
            close(rs, null);
        }

        return infos;
    }

    /**
     * Column info. The fields are documented in
     * {@code DatabaseMetaData#getColumns()}.
     */
    public static class ColumnInfo {
        public int    CHAR_OCTET_LENGTH;
        public String COLUMN_DEF;
        public String COLUMN_NAME;
        public int    COLUMN_SIZE;
        public int    DATA_TYPE;
        public int    DECIMAL_DIGITS;
        public String IS_NULLABLE;
        public int    NULLABLE;
        public int    NUM_PREC_RADIX;
        public int    ORDINAL_POSITION;
        public String REMARKS;
        public String SCOPE_CATLOG;
        public String SCOPE_SCHEMA;
        public String SCOPE_TABLE;
        public short  SOURCE_DATA_TYPE;
        public int    SQL_DATA_TYPE;
        public int    SQL_DATETIME_SUB;
        public String TABLE_CAT;
        public String TABLE_NAME;
        public String TABLE_SCHEM;
        public String TYPE_NAME;
    }
}
