/*
 * @(#)UpdateTablesV0.java    Created on 2008-10-23
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

import org.jphototagger.program.app.update.tables.UpdateTablesFactory.Updater;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Updates tables from previous application versions
 *
 * @author  Elmar Baumann
 */
public final class UpdateTablesV0 implements Updater {

    @Override
    public void update(Connection con) throws SQLException {
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
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }
}
