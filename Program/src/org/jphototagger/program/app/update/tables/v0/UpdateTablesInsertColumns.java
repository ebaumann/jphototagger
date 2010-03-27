/*
 * @(#)UpdateTablesInsertColumns.java    Created on 2008-11-06
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

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.app.update.tables.ColumnInfo;
import org.jphototagger.program.app.update.tables.IndexOfColumn;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds new columns to the database.
 *
 * @author  Elmar Baumann
 */
final class UpdateTablesInsertColumns {
    private static final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

    static {
        columns.add(new ColumnInfo("programs", "parameters_after_filename",
                                   "BINARY", null));
        columns.add(new ColumnInfo("programs", "action", "BOOLEAN",
                                   new IndexOfColumn("programs", "action",
                                       "idx_programs_action", false)));
        columns.add(new ColumnInfo("programs", "input_before_execute",
                                   "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "input_before_execute_per_file",
                                   "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "single_file_processing",
                                   "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "change_file", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "use_pattern", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "pattern", "BINARY", null));
        columns.add(new ColumnInfo("hierarchical_subjects", "real", "BOOLEAN",
                                   new IndexOfColumn("hierarchical_subjects",
                                       "real",
                                       "idx_hierarchical_subjects_real",
                                       false)));
        columns.add(new ColumnInfo("xmp", "iptc4xmpcore_datecreated",
                                   "VARCHAR_IGNORECASE(32)",
                                   new IndexOfColumn("xmp",
                                       "iptc4xmpcore_datecreated",
                                       "idx_iptc4xmpcore_datecreated", false)));
        columns.add(new ColumnInfo("metadata_edit_templates", "rating",
                                   "BINARY", null));
        columns.add(new ColumnInfo("metadata_edit_templates",
                                   "iptc4xmpcore_datecreated", "BINARY", null));
        columns.add(new ColumnInfo("exif", "exif_lens",
                                   "VARCHAR_IGNORECASE(256)",
                                   new IndexOfColumn("exif", "exif_lens",
                                       "idx_exif_lens", false)));
        columns.add(new ColumnInfo("saved_searches", "search_type", "SMALLINT",
                                   null));
    }

    private final List<ColumnInfo> missingColumns = new ArrayList<ColumnInfo>();

    void update(Connection con) throws SQLException {
        fixBugs(con);
        setColumns(con);

        if (missingColumns.size() > 0) {
            addColumns(con);
        }
        SplashScreen.INSTANCE.setMessage("");
    }

    private void setColumns(Connection con) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        missingColumns.clear();

        for (ColumnInfo info : columns) {
            if (!dbMeta.existsColumn(con, info.getTableName(),
                                     info.getColumnName())) {
                missingColumns.add(info);
            }
        }
    }

    private void addColumns(Connection con) throws SQLException {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString(
                "UpdateTablesInsertColumns.Info.update"));

        for (ColumnInfo info : missingColumns) {
            addColumn(con, info);
        }
    }

    private void addColumn(Connection con, ColumnInfo info)
            throws SQLException {
        setMessage(info.getTableName(), info.getColumnName());

        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE " + info.getTableName() + " ADD COLUMN "
                         + info.getColumnName() + " " + info.getDataType();

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.execute(sql);

            if (info.getIndex() != null) {
                sql = info.getIndex().getSql();
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.execute(sql);
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void setMessage(String tableName, String columnName) {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString(
                "UpdateTablesInsertColumns.Info", tableName, columnName));
    }

    private void fixBugs(Connection con) throws SQLException {
        fixBugsMetaDataTemplates(con);
    }

    private void fixBugsMetaDataTemplates(Connection con) throws SQLException {
        final String tableName  = "metadata_edit_templates";
        final String columnName = "rating";

        if (!DatabaseMetadata.INSTANCE.existsColumn(con, tableName,
                columnName)) {
            return;
        }

        List<DatabaseMetadata.ColumnInfo> infos =
            DatabaseMetadata.INSTANCE.getColumnInfo(con, tableName, columnName);
        boolean hasInfo = infos.size() == 1;

        assert hasInfo : infos.size();

        if (hasInfo) {
            DatabaseMetadata.ColumnInfo info    = infos.get(0);
            boolean                     typeOk  = info.DATA_TYPE
                                                  == java.sql.Types.BINARY;
            boolean                     indexOk = info.ORDINAL_POSITION == 21;
            boolean                     isOk    = typeOk && indexOk;

            if (!isOk) {
                SplashScreen.INSTANCE.setMessage(
                    JptBundle.INSTANCE.getString(
                        "UpdateTablesInsertColumns.Info.DropColumnMetaDataTemplates",
                        tableName, columnName));
                dropColumn(con, tableName, columnName);
            }
        }
    }

    void dropColumn(Connection con, String tableName, String columnName)
            throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE " + tableName + " DROP " + columnName;

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);
        } finally {
            Database.close(stmt);
        }
    }
}
