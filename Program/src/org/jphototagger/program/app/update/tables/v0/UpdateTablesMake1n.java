/*
 * @(#)UpdateTablesMake1n.java    Created on 2010-03-13
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
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMaintainance;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * Moves content of a table column into another table related 1:n (one to many)
 * to that table.
 * <p>
 * Works only, if the column data type is a string.
 *
 * @author  Elmar Baumann
 */
public final class UpdateTablesMake1n {
    private static final Map<ColumnInfo, ColumnInfo> TARGET_COL_OF =
        new HashMap<ColumnInfo, ColumnInfo>();

    static {
        TARGET_COL_OF.put(
            new ColumnInfo("exif", "exif_recording_equipment", null, null),
            new ColumnInfo(
                "exif_recording_equipment", "equipment", null, null));
        TARGET_COL_OF.put(new ColumnInfo("exif", "exif_lens", null, null),
                          new ColumnInfo("exif_lenses", "lens", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_creator", null, null),
                          new ColumnInfo("dc_creators", "creator", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_rights", null, null),
                          new ColumnInfo("dc_rights", "rights", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "iptc4xmpcore_location", null, null),
            new ColumnInfo("iptc4xmpcore_locations", "location", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_authorsposition", null, null),
            new ColumnInfo(
                "photoshop_authorspositions", "authorsposition", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_captionwriter", null, null),
            new ColumnInfo(
                "photoshop_captionwriters", "captionwriter", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_city", null, null),
                          new ColumnInfo("photoshop_cities", "city", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_country", null, null),
            new ColumnInfo("photoshop_countries", "country", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_credit", null, null),
            new ColumnInfo("photoshop_credits", "credit", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_source", null, null),
            new ColumnInfo("photoshop_sources", "source", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_state", null, null),
                          new ColumnInfo("photoshop_states", "state", null,
                                         null));
    }

    UpdateTablesMake1n() {}

    void update(Connection con) throws SQLException {
        startMessage();
        moveContent(con);
        SplashScreen.INSTANCE.removeMessage();
    }

    private String getLinkColumn(String targetTable) {
        return "id_" + targetTable;
    }

    private void moveContent(Connection con) throws SQLException {
        con.setAutoCommit(true);

        boolean compress = false;

        for (ColumnInfo source : TARGET_COL_OF.keySet()) {
            if (DatabaseMetadata.INSTANCE.existsColumn(con,
                    source.getTableName(), source.getColumnName())) {
                ColumnInfo target = TARGET_COL_OF.get(source);
                Statement  stmt   = null;
                ResultSet  rs     = null;

                compress = true;

                try {
                    String sourceTable  = source.getTableName();
                    String sourceColumn = source.getColumnName();
                    String targetTable  = target.getTableName();
                    String sql = "SELECT id, " + sourceColumn + " FROM "
                                 + sourceTable;

                    addLinkColumn(con, sourceTable, targetTable);
                    stmt = con.createStatement();
                    AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
                    rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        Long   sourceId    = rs.getLong(1);
                        String sourceValue = rs.getString(2);

                        if (!rs.wasNull()) {
                            copy(con, source, sourceId, sourceValue, target);
                        }
                    }

                    dropColumn(con, sourceTable, sourceColumn);
                    DatabaseSavedSearches.INSTANCE.tagSearchesIfStmtContains(
                        sourceColumn, "!");
                } finally {
                    Database.close(rs, stmt);
                }
            }
        }

        if (compress) {
            DatabaseMaintainance.INSTANCE.compressDatabase();
        }
    }

    private void addLinkColumn(Connection con, String sourceTable,
                               String targetTable)
            throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String linkColumn = getLinkColumn(targetTable);

            addColumn(con, sourceTable, linkColumn, targetTable);
        } finally {
            Database.close(stmt);
        }
    }

    private void addColumn(Connection con, String sourceTable,
                           String newColumn, String targetTable)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, sourceTable,
                newColumn)) {
            Statement stmt = null;

            try {
                stmt = con.createStatement();

                String sqlAddColumn = "ALTER TABLE " + sourceTable
                                      + " ADD COLUMN " + newColumn + " BIGINT";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlAddColumn);
                stmt.executeUpdate(sqlAddColumn);

                String sqlAddForeignKey = "ALTER TABLE " + sourceTable
                                          + " ADD FOREIGN KEY (" + newColumn
                                          + ") REFERENCES " + targetTable
                                          + "(id) ON DELETE SET NULL";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlAddForeignKey);
                stmt.executeUpdate(sqlAddForeignKey);

                String indexname = "idx_" + sourceTable + "_" + newColumn;
                String sqlCreateIndex = "CREATE INDEX " + indexname + " ON "
                                        + sourceTable + " (" + newColumn + ")";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlCreateIndex);
                stmt.executeUpdate(sqlCreateIndex);
                con.commit();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void dropColumn(Connection con, String table, String column)
            throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, table, column)) {
            Statement stmt = null;

            try {
                String sql = "ALTER TABLE " + table + " DROP " + column;

                stmt = con.createStatement();
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.executeUpdate(sql);
                sql = "DROP INDEX idx_" + table + "_" + column + " IF EXISTS";
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                AppLogger.logSevere(UpdateTablesMake1n.class, ex);
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void copy(Connection con, ColumnInfo source, Long sourceId,
                      String sourceValue, ColumnInfo target)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String targetTable  = target.getTableName();
            String targetColumn = target.getColumnName();
            String sourceTable  = source.getTableName();
            String linkColumn   = getLinkColumn(targetTable);

            insertValueIntoTargetTable(con, targetTable, targetColumn,
                                       sourceValue);
            createLink(con, sourceTable, linkColumn, targetTable, targetColumn,
                       sourceValue, sourceId);
        } finally {
            Database.close(stmt);
        }
    }

    private void insertValueIntoTargetTable(Connection con, String targetTable,
            String targetColumn, String value)
            throws SQLException {
        if (!Database.exists(con, targetTable, targetColumn, value)) {
            PreparedStatement stmt = null;

            try {
                String sql = "INSERT INTO " + targetTable + " (" + targetColumn
                             + ") VALUES (?)";

                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.executeUpdate();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void createLink(Connection con, String sourceTable,
                            String linkColumn, String targetTable,
                            String targetColumn, String targetValue,
                            Long sourceId)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE " + sourceTable + " SET " + linkColumn
                         + " = ? WHERE " + sourceTable + ".id = ?";

            stmt = con.prepareStatement(sql);
            stmt.setLong(1, Database.getId(con, targetTable, targetColumn,
                                           targetValue));
            stmt.setLong(2, sourceId);
            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt);
            stmt.executeUpdate();
        } finally {
            Database.close(stmt);
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesMake1n.Info"));
    }
}
