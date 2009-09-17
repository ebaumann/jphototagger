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
 * @version 2008-10-23
 */
public final class DatabaseMetadata extends Database {

    public static final DatabaseMetadata INSTANCE = new DatabaseMetadata();

    private DatabaseMetadata() {
    }

    public boolean existsTable(Connection connection, String tablename) throws
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

    public boolean existsColumn(Connection connection, String tableName,
            String columnName) throws SQLException {
        boolean exists = false;
        Statement stmt = connection.createStatement();
        String sql = "select * from " + tableName + " WHERE 1 = 0"; // NOI18N "WHERE 1 = 0": speed, memory!
        AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
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
