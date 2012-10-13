package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 * @author Elmar Baumann
 */
public final class UpdateTablesDropTables {

    private static final List<String> TABLES = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesDropTables.class.getName());

    static {
        TABLES.add("saved_searches_values");
    }

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Dropping table");
        dropTables(con);
    }

    private void dropTables(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            for (String table : TABLES) {
                if (DatabaseMetadata.INSTANCE.existsTable(con, table)) {
                    String sql = "DROP TABLE " + table;

                    LOGGER.log(Level.FINER, sql);
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            Database.close(stmt);
        }
    }
}
