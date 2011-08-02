package org.jphototagger.program.app.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.app.update.tables.IndexInfo;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

/**
 * Updates the tables indexes.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesIndexes {

    private static final Map<IndexOfTable, IndexInfo[]> INDEX_TO_REPLACE = new HashMap<IndexOfTable, IndexInfo[]>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesIndexes.class.getName());


    static {
        INDEX_TO_REPLACE.put(new IndexOfTable("idx_collections_id", "collections"),
                new IndexInfo[]{
                    new IndexInfo(false, "idx_collections_id_collectionnname", "collections",
                    "id_collectionnname"),
                    new IndexInfo(false, "idx_collections_id_file", "collections", "id_file")});
    }

    void update(Connection con) throws SQLException {
        startMessage();
        replaceIndices(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void replaceIndices(Connection con) throws SQLException {
        for (IndexOfTable indexOfTable : INDEX_TO_REPLACE.keySet()) {
            String indexName = indexOfTable.getIndexName();
            String tableName = indexOfTable.getTableName();

            if (DatabaseMetadata.existsIndex(con, indexName, tableName)) {
                replaceIndex(con, indexName, INDEX_TO_REPLACE.get(indexOfTable));
            }
        }
    }

    private void replaceIndex(Connection con, String indexName, IndexInfo[] indexInfos) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "DROP INDEX " + indexName + " IF EXISTS";

            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);

            for (IndexInfo indexInfo : indexInfos) {
                LOGGER.log(Level.FINER, indexInfo.sql());
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
