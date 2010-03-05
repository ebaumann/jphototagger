/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.resource.JptBundle;

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
 * @author  Elmar Baumann
 * @version 2009-09-11
 */
final class UpdateTablesPrimaryKeys {
    private final UpdateTablesMessages messages                    =
        UpdateTablesMessages.INSTANCE;
    private static final List<String>  TABLES_PRIMARY_KEYS_TO_DROP =
        new ArrayList<String>();

    void update(Connection connection) throws SQLException {
        messages.message(
            JptBundle.INSTANCE.getString("UpdateTablesPrimaryKeys.Info"));
        dropPrimaryKeys(connection);
        messages.message("");
    }

    private void dropPrimaryKeys(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        Statement        stmt = null;
        ResultSet        rs   = null;

        for (String table : TABLES_PRIMARY_KEYS_TO_DROP) {
            try {
                rs = meta.getPrimaryKeys(connection.getCatalog(), null,
                                         table.toUpperCase());
                stmt = connection.createStatement();

                boolean hasPk = false;

                while (!hasPk && rs.next()) {
                    String pkName = rs.getString("PK_NAME");

                    if (pkName != null) {
                        hasPk = true;
                        stmt.executeUpdate("alter table " + table
                                           + " drop primary key");
                    }
                }
            } finally {
                Database.close(rs, stmt);
            }
        }
    }
}
