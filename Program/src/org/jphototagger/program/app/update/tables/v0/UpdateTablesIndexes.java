package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.app.update.tables.IndexInfo;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * Updates the tables indexes.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesIndexes {
    private static final Map<Pair<String, String>, IndexInfo[]> INDEX_TO_REPLACE = new HashMap<Pair<String, String>,
                                                                                       IndexInfo[]>();

    static {
        INDEX_TO_REPLACE.put(new Pair<String, String>("idx_collections_id", "collections"),
                             new IndexInfo[] {
                                 new IndexInfo(false, "idx_collections_id_collectionnname", "collections",
                                     "id_collectionnname"),
                                 new IndexInfo(false, "idx_collections_id_file", "collections", "id_file") });
    }

    void update(Connection con) throws SQLException {
        startMessage();
        replaceIndices(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void replaceIndices(Connection con) throws SQLException {
        for (Pair<String, String> pair : INDEX_TO_REPLACE.keySet()) {
            String indexName = pair.getFirst();
            String tableName = pair.getSecond();

            if (DatabaseMetadata.existsIndex(con, indexName, tableName)) {
                replaceIndex(con, indexName, INDEX_TO_REPLACE.get(pair));
            }
        }
    }

    private void replaceIndex(Connection con, String indexName, IndexInfo[] indexInfos) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "DROP INDEX " + indexName + " IF EXISTS";

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);

            for (IndexInfo indexInfo : indexInfos) {
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, indexInfo.sql());
                stmt.executeUpdate(indexInfo.sql());
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("UpdateTablesIndexes.Info"));
    }
}
