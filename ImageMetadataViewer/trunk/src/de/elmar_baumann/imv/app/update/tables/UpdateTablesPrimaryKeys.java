package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.resource.Bundle;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Updates the table's primary keys.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-11
 */
final class UpdateTablesPrimaryKeys {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private static final List<String> TABLES_PRIMARY_KEYS_TO_DROP =
            new ArrayList<String>();

    void update(Connection connection) throws SQLException {
        messages.message(Bundle.getString("UpdateTablesPrimaryKeys.Info")); // NOI18N
        dropPrimaryKeys(connection);
        messages.message(""); // NOI18N
    }

    private void dropPrimaryKeys(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        for (String table : TABLES_PRIMARY_KEYS_TO_DROP) {
            ResultSet rs = meta.getPrimaryKeys(
                    connection.getCatalog(), null, table.toUpperCase());
            Statement stmt = connection.createStatement();
            boolean hasPk = false;
            while (!hasPk && rs.next()) {
                String pkName = rs.getString("PK_NAME");
                if (pkName != null) {
                    hasPk = true;
                    stmt.executeUpdate(
                            "alter table " + table + " drop primary key");
                }
            }
        }
    }
}
