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
import de.elmar_baumann.jpt.UserSettings;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.logging.Level;

/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann
 * @version 2008-10-23
 */
public final class UpdateTables extends Database {
    public static final UpdateTables INSTANCE = new UpdateTables();

    private UpdateTables() {}

    public void update(Connection con) throws SQLException {
        Level defaultLogLevel = UserSettings.INSTANCE.getLogLevel();

        UserSettings.INSTANCE.setLogLevel(Level.FINEST);

        try {
            new UpdateTablesDropColumns().update(con);
            new UpdateTablesRenameColumns().update(con);
            new UpdateTablesInsertColumns().update(con);
            new UpdateTablesIndexes().update(con);
            new UpdateTablesPrimaryKeys().update(con);
            new UpdateTablesXmpLastModified().update(con);
            new UpdateTablesPrograms().update(con);
            new UpdateTablesDeleteInvalidExif().update(con);
            new UpdateTablesThumbnails().update(con);
            new UpdateTablesDropCategories().update(con);
            new UpdateTablesXmpDcSubjects().update(con);
            new UpdateTablesMake1n().update(con);
        } finally {
            UserSettings.INSTANCE.setLogLevel(defaultLogLevel);
        }
    }
}
