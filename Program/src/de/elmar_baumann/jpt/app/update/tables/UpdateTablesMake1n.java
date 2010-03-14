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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.SplashScreen;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.database.DatabaseSavedSearches;

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
 * @version 2010-03-13
 */
public final class UpdateTablesMake1n {
    private static final Map<ColumnInfo, ColumnInfo> TARGET_COL_OF =
        new HashMap<ColumnInfo, ColumnInfo>();
    private static final String TMP_ID_COL = "id_tmp";

    static {
        TARGET_COL_OF.put(
            new ColumnInfo("exif", "exif_recording_equipment", null, null),
            new ColumnInfo(
                "exif_recording_equipment", "equipment", null, null));
        TARGET_COL_OF.put(new ColumnInfo("exif", "exif_lens", null, null),
                          new ColumnInfo("exif_lens", "lens", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_creator", null, null),
                          new ColumnInfo("dc_creator", "creator", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_rights", null, null),
                          new ColumnInfo("dc_rights", "rights", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "iptc4xmpcore_location", null, null),
            new ColumnInfo("iptc4xmpcore_location", "location", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_authorsposition", null, null),
            new ColumnInfo(
                "photoshop_authorsposition", "authorsposition", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_captionwriter", null, null),
            new ColumnInfo(
                "photoshop_captionwriter", "captionwriter", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_city", null, null),
                          new ColumnInfo("photoshop_city", "city", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_country", null, null),
            new ColumnInfo("photoshop_country", "country", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_credit", null, null),
            new ColumnInfo("photoshop_credit", "credit", null, null));
        TARGET_COL_OF.put(
            new ColumnInfo("xmp", "photoshop_source", null, null),
            new ColumnInfo("photoshop_source", "source", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_state", null, null),
                          new ColumnInfo("photoshop_state", "state", null,
                                         null));
    }

    UpdateTablesMake1n() {}

    void update(Connection connection) throws SQLException {
        SplashScreen.INSTANCE.setMessage("UpdateTablesMake1n.Info");
        moveContent(connection);
    }

    private void moveContent(Connection connection) throws SQLException {
        connection.setAutoCommit(true);

        for (ColumnInfo source : TARGET_COL_OF.keySet()) {
            if (DatabaseMetadata.INSTANCE.existsColumn(connection,
                    source.getTableName(), source.getColumnName())) {
                ColumnInfo target = TARGET_COL_OF.get(source);
                Statement  stmt   = null;
                ResultSet  rs     = null;

                try {
                    addTempIdColumn(connection, target.getTableName());
                    stmt = connection.createStatement();

                    String sourceTableName = source.getTableName();
                    String sourceColumn    = source.getColumnName();
                    String sql             = "SELECT id, " + sourceColumn
                                             + " FROM " + sourceTableName;

                    AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
                    rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        Long   sourceId    = rs.getLong(1);
                        String sourceValue = rs.getString(2);

                        if (!rs.wasNull()) {
                            copy(connection, sourceId, sourceValue, target);
                        }
                    }

                    alterSourceTable(connection, source, target);
                    link(connection, source, target);
                    DatabaseSavedSearches.INSTANCE.tagSearchesIfStmtContains(
                        sourceColumn, "!");
                    dropTempIdColumn(connection, target.getTableName());
                } finally {
                    Database.close(rs, stmt);
                }
            }
        }
    }

    private void link(Connection connection, ColumnInfo source,
                      ColumnInfo target)
            throws SQLException {
        Statement         stmtQuery  = null;
        PreparedStatement stmtUpdate = null;
        ResultSet         rsQuery    = null;
        String            linkColumn = "id_" + target.getTableName();

        try {
            stmtQuery = connection.createStatement();

            String sqlUpdate = "UPDATE " + source.getTableName() + " SET "
                               + linkColumn + " = ? WHERE id = ?";

            stmtUpdate = connection.prepareStatement(sqlUpdate);

            String sqlQuery = "SELECT id, " + TMP_ID_COL + " FROM "
                              + target.getTableName();

            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sqlQuery);
            rsQuery = stmtQuery.executeQuery(sqlQuery);

            while (rsQuery.next()) {
                Long idTarget = rsQuery.getLong(1);
                Long idSource = rsQuery.getLong(2);

                stmtUpdate.setLong(1, idTarget);
                stmtUpdate.setLong(2, idSource);
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   stmtUpdate);
                stmtUpdate.executeUpdate();
            }
        } finally {
            Database.close(rsQuery, stmtQuery);
            Database.close(stmtUpdate);
        }
    }

    private void alterSourceTable(Connection connection, ColumnInfo source,
                                  ColumnInfo target)
            throws SQLException {
        Statement stmt = null;

        try {
            stmt = connection.createStatement();

            String newColumn   = "id_" + target.getTableName();
            String oldColumn   = source.getColumnName();
            String sourceTable = source.getTableName();

            addNewColumn(connection, sourceTable, newColumn,
                         target.getTableName());
            dropOldColumn(connection, sourceTable, oldColumn, stmt);
        } finally {
            Database.close(stmt);
        }
    }

    private void addNewColumn(Connection connection, String sourceTable,
                              String newColumn, String targetTable)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(connection, sourceTable,
                newColumn)) {
            Statement stmt = null;

            try {
                stmt = connection.createStatement();

                String sqlAddColumn = "ALTER TABLE " + sourceTable
                                      + " ADD COLUMN " + newColumn + " BIGINT";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlAddColumn);
                stmt.executeUpdate(sqlAddColumn);

                String sqlAddForeignKey = "ALTER TABLE " + sourceTable
                                          + " ADD FOREIGN KEY (" + newColumn
                                          + ") REFERENCES " + targetTable
                                          + "(id)" + " ON DELETE SET NULL";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlAddForeignKey);
                stmt.executeUpdate(sqlAddForeignKey);

                String indexname      = "idx_" + sourceTable + "_" + newColumn;
                String sqlCreateIndex = "CREATE INDEX " + indexname + " ON "
                                        + sourceTable + " (" + newColumn + ")";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlCreateIndex);
                stmt.executeUpdate(sqlCreateIndex);
                connection.commit();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void dropOldColumn(Connection connection, String sourceTable,
                               String oldColumn, Statement stmt)
            throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(connection, sourceTable,
                oldColumn)) {
            String sqlDropColumn = "ALTER TABLE " + sourceTable + " DROP "
                                   + oldColumn;

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sqlDropColumn);
            stmt.executeUpdate(sqlDropColumn);

            try {
                String sqlDropIndex = "DROP INDEX idx_" + sourceTable + "_"
                                      + oldColumn + " IF EXISTS";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING,
                                   sqlDropIndex);
                stmt.executeUpdate(sqlDropIndex);
            } catch (SQLException ex) {
                AppLogger.logSevere(UpdateTablesMake1n.class, ex);
            }
        }
    }

    private void copy(Connection connection, Long sourceId, String sourceValue,
                      ColumnInfo target)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String targetTable  = target.getTableName();
            String targetColumn = target.getColumnName();

            if (!Database.exists(connection, targetTable, targetColumn,
                                 sourceValue)) {
                stmt = connection.prepareStatement("INSERT INTO " + targetTable
                                                   + " (" + targetColumn + ", "
                                                   + TMP_ID_COL
                                                   + ") VALUES (?, ?)");
                stmt.setString(1, sourceValue);
                stmt.setLong(2, sourceId);
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt);
                stmt.executeUpdate();
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void addTempIdColumn(Connection connection, String tableName)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(connection, tableName,
                TMP_ID_COL)) {
            Statement stmt = null;

            try {
                stmt = connection.createStatement();

                String sql = "ALTER TABLE " + tableName + " ADD COLUMN "
                             + TMP_ID_COL + " BIGINT";

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.executeUpdate(sql);
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void dropTempIdColumn(Connection connection, String tableName)
            throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(connection, tableName,
                TMP_ID_COL)) {
            Statement stmt = null;

            try {
                stmt = connection.createStatement();

                String sql = "ALTER TABLE " + tableName + " DROP " + TMP_ID_COL;

                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
                stmt.executeUpdate(sql);
            } finally {
                Database.close(stmt);
            }
        }
    }
}
