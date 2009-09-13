/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.generics.Pair;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Updates the tables indexes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-11
 */
final class UpdateTablesIndexes {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private static final Map<Pair<String, String>, IndexInfo[]> INDEX_TO_REPLACE =
            new HashMap<Pair<String, String>, IndexInfo[]>();

    static {
        INDEX_TO_REPLACE.put(
                new Pair<String, String>(
                "idx_collections_id", // NOI18N
                "collections"), // NOI18N
                new IndexInfo[]{
                    new IndexInfo(
                    false,
                    "idx_collections_id_collectionnnames", // NOI18N
                    "collections", // NOI18N
                    "id_collectionnnames"), // NOI18N
                    new IndexInfo(
                    false,
                    "idx_collections_id_files", // NOI18N
                    "collections", // NOI18N
                    "id_files") // NOI18N
                });
    }

    void update(Connection connection) throws SQLException {
        messages.message(Bundle.getString("UpdateTablesIndexes.Info")); // NOI18N
        replaceIndices(connection);
        messages.message(""); // NOI18N
    }

    private void replaceIndices(Connection connection) throws SQLException {
        for (Pair<String, String> pair : INDEX_TO_REPLACE.keySet()) {
            String indexName = pair.getFirst();
            String tableName = pair.getSecond();
            if (existsIndex(connection, indexName, tableName)) {
                replaceIndex(connection, indexName, INDEX_TO_REPLACE.get(pair));
            }
        }
    }

    private void replaceIndex(
            Connection connection, String indexName, IndexInfo[] indexInfos)
            throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "DROP INDEX " + indexName + " IF EXISTS";
        stmt.executeUpdate(sql);
        for (IndexInfo indexInfo : indexInfos) {
            stmt.executeUpdate(indexInfo.sql());
        }
        stmt.close();
    }

    private boolean existsIndex(
            Connection connection, String indexName, String tableName)
            throws SQLException {

        boolean exists = false;
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getIndexInfo(
                connection.getCatalog(), null, tableName.toUpperCase(), false,
                true);
        while (!exists && rs.next()) {
            String name = rs.getString("INDEX_NAME"); // NOI18N
            if (name != null) {
                exists = name.equalsIgnoreCase(indexName);
            }
        }
        return exists;
    }
}
