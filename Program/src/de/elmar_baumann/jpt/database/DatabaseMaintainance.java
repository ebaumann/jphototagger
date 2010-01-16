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
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseMaintainance extends Database {

    public static final DatabaseMaintainance INSTANCE = new DatabaseMaintainance();

    /**
     * Shuts down the database.
     */
    public void shutdown() {
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            AppLogger.logInfo(DatabaseMaintainance.class,
                    "DatabaseMaintainance.Info.Shutdown");
            stmt.executeUpdate("SHUTDOWN");
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
            MessageDisplayer.error(null, "DatabaseMaintainance.Error.Shutdown");
        }
    }

    /**
     * Komprimiert die Datenbank.
     *
     * @return true, wenn die Datenbank erfolgreich komprimiert wurde
     */
    public boolean compressDatabase() {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CHECKPOINT DEFRAG");
            success = true;
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(DatabaseMaintainance.class, ex);
        } finally {
            free(connection);
        }
        return success;
    }

    private DatabaseMaintainance() {
    }
}
