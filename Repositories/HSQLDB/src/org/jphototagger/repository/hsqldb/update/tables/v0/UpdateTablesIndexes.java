package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

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
        LOGGER.log(Level.INFO, "Updating table indices");
        replaceIndices(con);
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
}
