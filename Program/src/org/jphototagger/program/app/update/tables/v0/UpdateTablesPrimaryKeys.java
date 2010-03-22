/*
 * @(#)UpdateTablesPrimaryKeys.java    Created on 2009-09-11
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
 * @author  Elmar Baumann
 */
final class UpdateTablesPrimaryKeys {
    private static final List<String> TABLES_PRIMARY_KEYS_TO_DROP =
        new ArrayList<String>();

    void update(Connection con) throws SQLException {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesPrimaryKeys.Info"));
        dropPrimaryKeys(con);
        SplashScreen.INSTANCE.setMessage("");
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
}
