/*
 * @(#)UpdateTablesDropTables.java    Created on 2010-04-01
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import org.jphototagger.program.app.AppLogger;

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
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesDropTables.Info"));
    }
}
