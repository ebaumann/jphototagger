/*
 * @(#)UpdateTablesXmpLastModified.java    Created on 2008-10-29
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

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesXmpLastModified {
    void update(Connection con) throws SQLException {
        startMessage();
        removeColumnXmpLastModifiedFromTableXmp(con);
        addColumnXmpLastModifiedToTableFiles(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private void removeColumnXmpLastModifiedFromTableXmp(Connection con)
            throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, "xmp",
                "lastmodified")) {
            Database.execute(con, "ALTER TABLE xmp DROP COLUMN lastmodified");
        }
    }

    private void addColumnXmpLastModifiedToTableFiles(Connection con)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "files",
                "xmp_lastmodified")) {
            Database.execute(
                con, "ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT");
            copyLastModifiedToXmp(con);
        }
    }

    // too slow and no feedback:
    // "UPDATE files SET xmp_lastmodified = lastmodified"
    private void copyLastModifiedToXmp(Connection con) throws SQLException {
        PreparedStatement stmtUpdate = null;
        Statement         stmtQuery  = null;
        ResultSet         rsQuery    = null;

        try {
            stmtQuery = con.createStatement();
            stmtUpdate = con.prepareStatement(
                "UPDATE files SET xmp_lastmodified = ? WHERE id = ?");

            long   lastModified = -1;
            long   idFiles      = -1;
            String sql          = "SELECT id, lastmodified FROM files";

            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rsQuery = stmtQuery.executeQuery(sql);

            while (rsQuery.next()) {
                idFiles      = rsQuery.getLong(1);
                lastModified = rsQuery.getLong(2);
                stmtUpdate.setLong(1, lastModified);
                stmtUpdate.setLong(2, idFiles);
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   stmtUpdate);
                stmtUpdate.executeUpdate();
            }
        } finally {
            Database.close(rsQuery, stmtQuery);
            Database.close(stmtUpdate);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesXmpLastModified.Info"));
    }
}
