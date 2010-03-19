/*
 * @(#)UpdateTablesIndexes.java    Created on 2009-09-11
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.app.update.tables.v0;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.SplashScreen;
import de.elmar_baumann.jpt.app.update.tables.IndexInfo;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.resource.JptBundle;
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
 * @author  Elmar Baumann
 */
final class UpdateTablesIndexes {
    private static final Map<Pair<String, String>, IndexInfo[]> INDEX_TO_REPLACE =
        new HashMap<Pair<String, String>, IndexInfo[]>();

    static {
        INDEX_TO_REPLACE.put(
            new Pair<String, String>("idx_collections_id", "collections"),
            new IndexInfo[] {
                new IndexInfo(
                    false, "idx_collections_id_collectionnnames",
                    "collections", "id_collectionnnames"),
                new IndexInfo(false, "idx_collections_id_files", "collections",
                              "id_files") });
    }

    void update(Connection con) throws SQLException {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesIndexes.Info"));
        replaceIndices(con);
        SplashScreen.INSTANCE.setMessage("");
    }

    private void replaceIndices(Connection con) throws SQLException {
        for (Pair<String, String> pair : INDEX_TO_REPLACE.keySet()) {
            String indexName = pair.getFirst();
            String tableName = pair.getSecond();

            if (existsIndex(con, indexName, tableName)) {
                replaceIndex(con, indexName, INDEX_TO_REPLACE.get(pair));
            }
        }
    }

    private void replaceIndex(Connection con, String indexName,
                              IndexInfo[] indexInfos)
            throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "DROP INDEX " + indexName + " IF EXISTS";

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);

            for (IndexInfo indexInfo : indexInfos) {
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   indexInfo.sql());
                stmt.executeUpdate(indexInfo.sql());
            }
        } finally {
            Database.close(stmt);
        }
    }

    private boolean existsIndex(Connection con, String indexName,
                                String tableName)
            throws SQLException {
        boolean   exists = false;
        ResultSet rs     = null;

        try {
            DatabaseMetaData meta = con.getMetaData();

            rs = meta.getIndexInfo(con.getCatalog(), null,
                                   tableName.toUpperCase(), false, true);

            while (!exists && rs.next()) {
                String name = rs.getString("INDEX_NAME");

                if (name != null) {
                    exists = name.equalsIgnoreCase(indexName);
                }
            }
        } finally {
            Database.close(rs, null);
        }

        return exists;
    }
}
