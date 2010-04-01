/*
 * @(#)UpdateTablesRenameColumns.java    Created on 2008-11-06
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

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.app.update.tables.ColumnInfo;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
final class UpdateTablesRenameColumns {
    private static final List<Pair<ColumnInfo, ColumnInfo>> COLUMNS =
        new ArrayList<Pair<ColumnInfo, ColumnInfo>>();

    static {
        COLUMNS.add(new Pair<ColumnInfo,
                             ColumnInfo>(new ColumnInfo("programs",
                                 "parameters", null,
                                 null), new ColumnInfo(null,
                                     "parameters_before_filename", null,
                                     null)));
        COLUMNS.add(new Pair<ColumnInfo,
                             ColumnInfo>(new ColumnInfo("xmp", "id_files",
                                 null, null), new ColumnInfo(null, "id_file",
                                     null, null)));
        COLUMNS.add(new Pair<ColumnInfo,
                             ColumnInfo>(new ColumnInfo("exif", "id_files",
                                 null, null), new ColumnInfo(null, "id_file",
                                     null, null)));
        COLUMNS.add(new Pair<ColumnInfo,
                             ColumnInfo>(new ColumnInfo("collections",
                                 "id_collectionnnames", null,
                                 null), new ColumnInfo(null,
                                     "id_collectionnname", null, null)));
        COLUMNS.add(new Pair<ColumnInfo,
                             ColumnInfo>(new ColumnInfo("collections",
                                 "id_files", null, null), new ColumnInfo(null,
                                     "id_file", null, null)));
        COLUMNS.add(
            new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo(
                    "saved_searches_panels", "id_saved_searches", null,
                    null), new ColumnInfo(
                        null, "id_saved_search", null, null)));
        COLUMNS.add(
            new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo(
                    "saved_searches_keywords", "id_saved_searches", null,
                    null), new ColumnInfo(
                        null, "id_saved_search", null, null)));
        COLUMNS.add(
            new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo(
                    "actions_after_db_insertion", "id_programs", null,
                    null), new ColumnInfo(null, "id_program", null, null)));
        COLUMNS.add(
            new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo(
                    "saved_searches", "sql_string", null,
                    null), new ColumnInfo(null, "custom_sql", null, null)));
    }

    private final List<Pair<ColumnInfo, ColumnInfo>> renameColumns =
        new ArrayList<Pair<ColumnInfo, ColumnInfo>>();

    void update(Connection con) throws SQLException {
        startMessage();
        setColumns(con);

        if (renameColumns.size() > 0) {
            renameColumns(con);
        }

        SplashScreen.INSTANCE.removeMessage();
    }

    private void setColumns(Connection con) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        renameColumns.clear();

        for (Pair<ColumnInfo, ColumnInfo> info : COLUMNS) {
            if (dbMeta.existsColumn(con, info.getFirst().getTableName(),
                                    info.getFirst().getColumnName())) {
                renameColumns.add(info);
            }
        }
    }

    private void renameColumns(Connection con) throws SQLException {
        for (Pair<ColumnInfo, ColumnInfo> info : renameColumns) {
            renameColumn(con, info);
        }
    }

    private void renameColumn(Connection con, Pair<ColumnInfo, ColumnInfo> info)
            throws SQLException {
        String tableName      = info.getFirst().getTableName();
        String fromColumnName = info.getFirst().getColumnName();
        String toColumnName   = info.getSecond().getColumnName();
        String sql = "ALTER TABLE " + tableName + " ALTER COLUMN "
                     + fromColumnName + " RENAME TO " + toColumnName;

        Database.execute(con, sql);

        String oldIndexName = "idx_" + tableName + "_" + fromColumnName;

        if (DatabaseMetadata.existsIndex(con, oldIndexName, tableName)) {
            sql = "ALTER INDEX " + oldIndexName + " RENAME TO idx_"
                   + tableName + "_" + toColumnName;
            Database.execute(con, sql);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesRenameColumns.Info"));
    }
}
