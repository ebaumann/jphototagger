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
import de.elmar_baumann.jpt.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Drops unused columns.
 *
 * @author  Elmar Baumann
 * @version 2008-10-31
 */
final class UpdateTablesDropColumns {
    private final UpdateTablesMessages    messages    =
        UpdateTablesMessages.INSTANCE;
    private final List<ColumnInfo>        dropColumns =
        new ArrayList<ColumnInfo>();
    private static final List<ColumnInfo> COLUMNS     =
        new ArrayList<ColumnInfo>();

    static {
        COLUMNS.add(new ColumnInfo("autoscan_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("favorite_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("file_exclude_pattern", "id", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "id", null,
                                   null));
        COLUMNS.add(new ColumnInfo("xmp", "iptc4xmpcore_countrycode", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "iptc4xmpcoreCountrycode", null, null));
    }

    void update(Connection connection) throws SQLException {
        setColumns(connection);

        if (dropColumns.size() > 0) {
            dropColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        dropColumns.clear();

        for (ColumnInfo info : COLUMNS) {
            if (dbMeta.existsColumn(connection, info.getTableName(),
                                    info.getColumnName())) {
                dropColumns.add(info);
            }
        }
    }

    private void dropColumns(Connection connection) throws SQLException {
        messages.message(
            JptBundle.INSTANCE.getString(
                "UpdateTablesDropColumns.Info.update"));

        for (ColumnInfo info : dropColumns) {
            dropColumn(connection, info.getTableName(), info.getColumnName());
        }
    }

    private void dropColumn(Connection connection, String tableName,
                            String columnName)
            throws SQLException {
        setMessage(tableName, columnName);
        Database.execute(connection,
                         "ALTER TABLE " + tableName + " DROP COLUMN "
                         + columnName);
    }

    private void setMessage(String tableName, String columnName) {
        messages.message(
            JptBundle.INSTANCE.getString(
                "UpdateTablesDropColumns.Info", tableName, columnName));
    }
}
