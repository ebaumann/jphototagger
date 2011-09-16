package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.ApplicationProperties;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.lib.util.Version;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseMetadata extends Database {

    public static final DatabaseMetadata INSTANCE = new DatabaseMetadata();
    private static final String KEY_JPT_APP_DB_VERSION = "VersionLastDbUpdate";

    private DatabaseMetadata() {
    }

    /**
     * Returns, whether the database is the database of a newer JPhotoTagger
     * version.
     *
     * @return true, if the database is for a newer version
     */
    public static boolean isDatabaseOfNewerVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        String dbVersion = appPropertiesRepo.getString(KEY_JPT_APP_DB_VERSION);

        // Only older JPhotoTagger versions did not write that key
        if (dbVersion == null) {
            return false;
        }

        String versionString = Lookup.getDefault().lookup(ApplicationProperties.class).getApplicationVersionString();
        Version db = Version.parseVersion(dbVersion, ".");
        Version current = Version.parseVersion(versionString, ".");

        return current.compareTo(db) < 0;
    }

    /**
     * Returns, whether the database is the database of this JPhotoTagger
     * version.
     *
     * @return true, if the database is from this version
     */
    public static boolean isDatabaseOfCurrentVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        String dbVersion = appPropertiesRepo.getString(KEY_JPT_APP_DB_VERSION);
        String versionString = Lookup.getDefault().lookup(ApplicationProperties.class).getApplicationVersionString();

        return (dbVersion == null)
                ? false
                : dbVersion.equals(versionString);
    }

    /**
     * Returns, whether the database is the database of an older JPhotoTagger
     * version.
     *
     * @return true, if the database is from an older version
     */
    public static boolean isDatabaseOfOlderVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        String dbVersion = appPropertiesRepo.getString(KEY_JPT_APP_DB_VERSION);

        // Only older JPhotoTagger versions did not write that key
        if (dbVersion == null) {
            return true;
        }

        String versionString = Lookup.getDefault().lookup(ApplicationProperties.class).getApplicationVersionString();
        Version db = Version.parseVersion(dbVersion, ".");
        Version current = Version.parseVersion(versionString, ".");

        return current.compareTo(db) > 0;
    }

    /**
     * Returns the application version written by
     * {@code #setCurrentAppVersionToDatabase()}.
     *
     * @return version string or null if not written
     */
    public static String getDatabaseAppVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);

        return appPropertiesRepo.getString(KEY_JPT_APP_DB_VERSION);
    }

    /**
     * Sets the current application version into the database.
     * <p>
     * <em>Use only, if ensured, that the database layout fits to the current
     * version!</em>
     */
    public static void setCurrentAppVersionToDatabase() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        String versionString = Lookup.getDefault().lookup(ApplicationProperties.class).getApplicationVersionString();

        appPropertiesRepo.setString(KEY_JPT_APP_DB_VERSION, versionString);
    }

    public boolean existsTable(Connection con, String tablename) throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        boolean exists = false;
        DatabaseMetaData dbm = con.getMetaData();
        String[] names = {"TABLE"};
        ResultSet rs = dbm.getTables(null, "%", "%", names);

        while (!exists && rs.next()) {
            exists = rs.getString("TABLE_NAME").equalsIgnoreCase(tablename);
        }

        rs.close();

        return exists;
    }

    public boolean existsColumn(Connection con, String tableName, String columnName) throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tableName == null) {
            throw new NullPointerException("tableName == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        if (!existsTable(con, tableName)) {
            return false;
        }

        Statement stmt = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        boolean exists = false;

        try {
            stmt = con.createStatement();

            // "WHERE 1 = 0": speed, memory!
            String sql = "select * from " + tableName + " WHERE 1 = 0";

            rs = stmt.executeQuery(sql);
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

    public static boolean existsIndex(Connection con, String indexName, String tableName) throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (indexName == null) {
            throw new NullPointerException("indexName == null");
        }

        if (tableName == null) {
            throw new NullPointerException("tableName == null");
        }

        boolean exists = false;
        ResultSet rs = null;

        try {
            DatabaseMetaData meta = con.getMetaData();

            rs = meta.getIndexInfo(con.getCatalog(), null, tableName.toUpperCase(), false, true);

            while (!exists && rs.next()) {
                String name = rs.getString("INDEX_NAME");

                if (name != null) {
                    exists = name.equalsIgnoreCase(indexName);
                }
            }
        } finally {
            Database.close(rs, null);
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
    public List<ColumnInfo> getColumnInfo(Connection con, String tableName, String columnName) throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tableName == null) {
            throw new NullPointerException("tableName == null");
        }

        ResultSet rs = null;
        List<ColumnInfo> infos = new ArrayList<ColumnInfo>();

        try {
            DatabaseMetaData meta = con.getMetaData();

            rs = meta.getColumns(null, null, tableName.toUpperCase(), (columnName == null)
                    ? "%"
                    : columnName.toUpperCase());

            while (rs.next()) {
                ColumnInfo colInfo = new ColumnInfo();

                colInfo.CHAR_OCTET_LENGTH = rs.getInt("CHAR_OCTET_LENGTH");
                colInfo.COLUMN_DEF = rs.getString("COLUMN_DEF");
                colInfo.COLUMN_NAME = rs.getString("COLUMN_NAME");
                colInfo.COLUMN_SIZE = rs.getInt("COLUMN_SIZE");
                colInfo.DATA_TYPE = rs.getInt("DATA_TYPE");
                colInfo.DECIMAL_DIGITS = rs.getInt("DECIMAL_DIGITS");
                colInfo.IS_NULLABLE = rs.getString("IS_NULLABLE");
                colInfo.NULLABLE = rs.getInt("NULLABLE");
                colInfo.NUM_PREC_RADIX = rs.getInt("NUM_PREC_RADIX");
                colInfo.ORDINAL_POSITION = rs.getInt("ORDINAL_POSITION");
                colInfo.REMARKS = rs.getString("REMARKS");
                colInfo.SCOPE_CATLOG = rs.getString("SCOPE_CATLOG");
                colInfo.SCOPE_SCHEMA = rs.getString("SCOPE_SCHEMA");
                colInfo.SCOPE_TABLE = rs.getString("SCOPE_TABLE");
                colInfo.SOURCE_DATA_TYPE = rs.getShort("SOURCE_DATA_TYPE");
                colInfo.SQL_DATA_TYPE = rs.getInt("SQL_DATA_TYPE");
                colInfo.SQL_DATETIME_SUB = rs.getInt("SQL_DATETIME_SUB");
                colInfo.TABLE_CAT = rs.getString("TABLE_CAT");
                colInfo.TABLE_NAME = rs.getString("TABLE_NAME");
                colInfo.TABLE_SCHEM = rs.getString("TABLE_SCHEM");
                colInfo.TYPE_NAME = rs.getString("TYPE_NAME");
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

        public int CHAR_OCTET_LENGTH;
        public String COLUMN_DEF;
        public String COLUMN_NAME;
        public int COLUMN_SIZE;
        public int DATA_TYPE;
        public int DECIMAL_DIGITS;
        public String IS_NULLABLE;
        public int NULLABLE;
        public int NUM_PREC_RADIX;
        public int ORDINAL_POSITION;
        public String REMARKS;
        public String SCOPE_CATLOG;
        public String SCOPE_SCHEMA;
        public String SCOPE_TABLE;
        public short SOURCE_DATA_TYPE;
        public int SQL_DATA_TYPE;
        public int SQL_DATETIME_SUB;
        public String TABLE_CAT;
        public String TABLE_NAME;
        public String TABLE_SCHEM;
        public String TYPE_NAME;
    }
}
