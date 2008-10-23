package de.elmar_baumann.imv.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/23
 */
public class DatabaseMetadata extends Database {
    
    private static DatabaseMetadata instance = new DatabaseMetadata();
    
    public static DatabaseMetadata getInstance() {
        return instance;
    }
    
    private DatabaseMetadata() {
    }

    public boolean existsTable(Connection connection, String tablename) throws SQLException {
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

}
