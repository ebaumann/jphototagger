package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.resource.JptBundle;

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
 * @author Elmar Baumann
 */
final class UpdateTablesPrimaryKeys {
    private static final List<String> TABLES_PRIMARY_KEYS_TO_DROP =
        new ArrayList<String>();

    void update(Connection con) throws SQLException {
        startMessage();
        dropPrimaryKeys(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void dropPrimaryKeys(Connection con) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        Statement        stmt = null;
        ResultSet        rs   = null;

        for (String table : TABLES_PRIMARY_KEYS_TO_DROP) {
            try {
                rs = meta.getPrimaryKeys(con.getCatalog(), null,
                                         table.toUpperCase());
                stmt = con.createStatement();

                boolean hasPk = false;

                while (!hasPk && rs.next()) {
                    String pkName = rs.getString("PK_NAME");

                    if (pkName != null) {
                        hasPk = true;

                        String sql = "alter table " + table
                                     + " drop primary key";

                        AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                           sql);
                        stmt.executeUpdate(sql);
                    }
                }
            } finally {
                Database.close(rs, stmt);
            }
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesPrimaryKeys.Info"));
    }
}
