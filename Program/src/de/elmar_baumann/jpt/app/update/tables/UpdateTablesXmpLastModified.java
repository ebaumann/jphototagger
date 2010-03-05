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
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.database.DatabaseStatistics;
import de.elmar_baumann.jpt.resource.JptBundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-10-29
 */
final class UpdateTablesXmpLastModified {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private       int                  count;

    void update(Connection connection) throws SQLException {
        removeColumnXmpLastModifiedFromTableXmp(connection);
        addColumnXmpLastModifiedToTableFiles(connection);
    }

    private void removeColumnXmpLastModifiedFromTableXmp(Connection connection) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(
            connection, "xmp", "lastmodified")) {
            messages.message(JptBundle.INSTANCE.getString("UpdateTablesXmpLastModified.Info.RemoveColumnXmpLastModified"));
            Database.execute(connection, "ALTER TABLE xmp DROP COLUMN lastmodified");
        }
    }

    private void addColumnXmpLastModifiedToTableFiles(Connection connection) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(connection, "files", "xmp_lastmodified")) {
            messages.message(JptBundle.INSTANCE.getString("UpdateTablesXmpLastModified.Info.AddColumnXmpLastModified.AddColumn"));
            Database.execute(connection, "ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT");
            copyLastModifiedToXmp(connection);
        }
    }

    // too slow and no feedback: "UPDATE files SET xmp_lastmodified = lastmodified"
    private void copyLastModifiedToXmp(Connection connection) throws SQLException {
        setProgress();
        PreparedStatement stmtUpdate = null;
        Statement stmtQuery = null;
        ResultSet rsQuery = null;
        try {
            stmtQuery = connection.createStatement();
            stmtUpdate = connection.prepareStatement("UPDATE files SET xmp_lastmodified = ? WHERE id = ?");
            long lastModified = -1;
            long idFiles = -1;
            int value = 0;
            rsQuery = stmtQuery.executeQuery("SELECT id, lastmodified FROM files");
            while (rsQuery.next()) {
                idFiles = rsQuery.getLong(1);
                lastModified = rsQuery.getLong(2);
                stmtUpdate.setLong(1, lastModified);
                stmtUpdate.setLong(2, idFiles);
                stmtUpdate.execute();
                messages.setValue(++value / count * 100);
            }
        } finally {
            Database.close(rsQuery, stmtQuery);
            Database.close(stmtUpdate);
        }
    }

    private void setProgress() {
        messages.message(JptBundle.INSTANCE.getString("UpdateTablesXmpLastModified.Info.AddColumnXmpLastModified.SetLastModified"));
        count = DatabaseStatistics.INSTANCE.getXmpCount();
    }
}
