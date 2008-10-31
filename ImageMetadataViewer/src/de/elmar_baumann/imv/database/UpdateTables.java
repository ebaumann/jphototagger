package de.elmar_baumann.imv.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/23
 */
class UpdateTables extends Database {

    private static UpdateTables instance = new UpdateTables();

    static UpdateTables getInstance() {
        return instance;
    }

    private UpdateTables() {
    }

    synchronized void update(Connection connection) throws SQLException {
        new UpdateTablesXmpLastModified().update(connection);
        new UpdateTablesUnusedColumns().update(connection);
        UpdateTablesMessages.getInstance().getProgressDialog().setVisible(false);
    }
}
