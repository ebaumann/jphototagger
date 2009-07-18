package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/23
 */
public final class DatabaseMetadata extends Database {

    public static final DatabaseMetadata INSTANCE = new DatabaseMetadata();

    private DatabaseMetadata() {
    }

    boolean existsTable(Connection connection, String tablename) throws
            SQLException {
        boolean exists = false;
        DatabaseMetaData dbm = connection.getMetaData();
        String[] names = {"TABLE"}; // NOI18N
        ResultSet rs = dbm.getTables(null, "%", "%", names); // NOI18N
        while (!exists && rs.next()) {
            exists = rs.getString("TABLE_NAME").equalsIgnoreCase(tablename); // NOI18N
        }
        rs.close();
        return exists;
    }

    boolean existsColumn(Connection connection, String tableName,
            String columnName) throws SQLException {
        boolean exists = false;
        Statement stmt = connection.createStatement();
        String sql = "select * from " + tableName + " WHERE 1 = 0"; // NOI18N "WHERE 1 = 0": speed, memory!
        AppLog.logFinest(getClass(), sql);
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();

        for (int i = 1; !exists && i <= columns; i++) {
            String column = rsmd.getColumnName(i);
            exists = column.equalsIgnoreCase(columnName);
        }
        stmt.close();
        return exists;
    }
}
