package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.repository.hsqldb.Database;

/**
 * Updates the table's primary keys.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesPrimaryKeys {

    private static final List<String> TABLES_PRIMARY_KEYS_TO_DROP = new ArrayList<String>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesPrimaryKeys.class.getName());

    void update(Connection con) throws SQLException {
        dropPrimaryKeys(con);
    }

    private void dropPrimaryKeys(Connection con) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        Statement stmt = null;
        ResultSet rs = null;

        LOGGER.log(Level.INFO, "Dropping primary keys");
        for (String table : TABLES_PRIMARY_KEYS_TO_DROP) {
            try {
                rs = meta.getPrimaryKeys(con.getCatalog(), null, table.toUpperCase());
                stmt = con.createStatement();

                boolean hasPk = false;

                while (!hasPk && rs.next()) {
                    String pkName = rs.getString("PK_NAME");

                    if (pkName != null) {
                        hasPk = true;

                        String sql = "alter table " + table + " drop primary key";
                        LOGGER.log(Level.FINER, sql);
                        stmt.executeUpdate(sql);
                    }
                }
            } finally {
                Database.close(rs, stmt);
            }
        }
    }
}
