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
package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.database.DatabaseStatistics;
import de.elmar_baumann.jpt.resource.Bundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-29
 */
final class UpdateTablesXmpLastModified {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;

    void update(Connection connection) throws SQLException {
        removeColumnXmpLastModifiedFromTableXmp(connection);
        addColumnXmpLastModifiedToTableFiles(connection);
    }

    private void removeColumnXmpLastModifiedFromTableXmp(Connection connection) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(
            connection, "xmp", "lastmodified")) {
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTablesXmpLastModified.Info.RemoveColumnXmpLastModified"));
            stmt.execute("ALTER TABLE xmp DROP COLUMN lastmodified");
            stmt.close();
        }
    }

    private void addColumnXmpLastModifiedToTableFiles(Connection connection) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(
            connection, "files", "xmp_lastmodified")) {
            Statement stmt = connection.createStatement();
            messages.message(Bundle.getString("UpdateTablesXmpLastModified.Info.AddColumnXmpLastModified.AddColumn"));
            stmt.execute("ALTER TABLE files ADD COLUMN xmp_lastmodified BIGINT");
            copyLastModifiedToXmp(connection);
            stmt.close();
        }
    }

    // too slow and no feedback: "UPDATE files SET xmp_lastmodified = lastmodified"
    private void copyLastModifiedToXmp(Connection connection) throws SQLException {
        setProgressDialog();
        Statement stmtQueryXmp = connection.createStatement();
        PreparedStatement stmtUpdate = connection.prepareStatement(
            "UPDATE files SET xmp_lastmodified = ? WHERE id = ?");
        long lastModified = -1;
        long idFiles = -1;
        int count = 0;
        ResultSet rsQuery = stmtQueryXmp.executeQuery(
            "SELECT id, lastmodified FROM files");
        while (rsQuery.next()) {
            idFiles = rsQuery.getLong(1);
            lastModified = rsQuery.getLong(2);
            stmtUpdate.setLong(1, lastModified);
            stmtUpdate.setLong(2, idFiles);
            stmtUpdate.execute();
            messages.setValue(++count);
        }
        stmtQueryXmp.close();
        stmtUpdate.close();
    }

    private void setProgressDialog() {
        messages.message(Bundle.getString("UpdateTablesXmpLastModified.Info.AddColumnXmpLastModified.SetLastModified"));
        messages.setIndeterminate(false);
        messages.setMinimum(0);
        messages.setMaximum(DatabaseStatistics.INSTANCE.getXmpCount());
        messages.setValue(0);
    }
}
