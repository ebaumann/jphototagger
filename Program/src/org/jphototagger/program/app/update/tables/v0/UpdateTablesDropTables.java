package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class UpdateTablesDropTables {
    private static final List<String> TABLES = new ArrayList<String>();

    static {
        TABLES.add("saved_searches_values");
    }

    void update(Connection con) throws SQLException {
        startMessage();
        dropTables(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void dropTables(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            for (String table : TABLES) {
                if (DatabaseMetadata.INSTANCE.existsTable(con, table)) {
                    String sql = "DROP TABLE " + table;

                    AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                    stmt.executeUpdate(sql);
                }
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("UpdateTablesDropTables.Info"));
    }
}
