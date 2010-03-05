/*
 * JPhotoTagger tags and finds images fast
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
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.generics.Pair;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
final class UpdateTablesRenameColumns {

    private final        UpdateTablesMessages               messages      = UpdateTablesMessages.INSTANCE;
    private final        List<Pair<ColumnInfo, ColumnInfo>> renameColumns = new ArrayList<Pair<ColumnInfo, ColumnInfo>>();
    private static final List<Pair<ColumnInfo, ColumnInfo>> COLUMNS       = new ArrayList<Pair<ColumnInfo, ColumnInfo>>();

    static {
        COLUMNS.add(new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo("programs", "parameters"                , null, null),
                new ColumnInfo(null      , "parameters_before_filename", null, null)
                ));
    }

    void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (renameColumns.size() > 0) {
            renameColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;
        renameColumns.clear();
        for (Pair<ColumnInfo, ColumnInfo> info : COLUMNS) {
            if (dbMeta.existsColumn(connection, info.getFirst().getTableName(), info.getFirst().getColumnName())) {
                renameColumns.add(info);
            }
        }
    }

    private void renameColumns(Connection connection) throws SQLException {
        messages.message(JptBundle.INSTANCE.getString("UpdateTablesRenameColumns.Info.update"));
        for (Pair<ColumnInfo, ColumnInfo> info : renameColumns) {
            renameColumn(connection, info);
        }
    }

    private void renameColumn(Connection connection, Pair<ColumnInfo, ColumnInfo> info) throws SQLException {
        setMessage(info.getFirst().getTableName(), info.getFirst().getColumnName());
        Database.execute(connection, "ALTER TABLE " +
                info.getFirst().getTableName() +
                " ALTER COLUMN " +
                info.getFirst().getColumnName() +
                " RENAME TO " +
                info.getSecond().getColumnName());
    }

    private void setMessage(String tableName, String columnName) {
        messages.message(JptBundle.INSTANCE.getString("UpdateTablesRenameColumns.Info.RenameColumn", tableName, columnName));
    }
}
