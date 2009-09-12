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

import de.elmar_baumann.imv.database.Database;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-23
 */
public final class UpdateTables extends Database {

    public static final UpdateTables INSTANCE = new UpdateTables();

    private UpdateTables() {
    }

    public void update(Connection connection) throws SQLException {
        new UpdateTablesDropColumns().update(connection);
        new UpdateTablesRenameColumns().update(connection);
        new UpdateTablesAddColumns().update(connection);
        new UpdateTablesIndexes().update(connection);
        new UpdateTablesPrimaryKeys().update(connection);
        new UpdateTablesXmpLastModified().update(connection);
        new UpdateTablesPrograms().update(connection);
        new UpdateTablesDeleteInvalidExif().update(connection);
        new UpdateTablesThumbnails().update(connection);
        UpdateTablesMessages.INSTANCE.getProgressDialog().setVisible(false);
    }
}
