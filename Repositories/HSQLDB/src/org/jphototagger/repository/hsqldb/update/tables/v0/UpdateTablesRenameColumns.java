package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesRenameColumns {

    private static final Collection<ColumnRenameInfo> COLUMN_RENAME_INFOS = new ArrayList<ColumnRenameInfo>();

    static {
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("programs", "parameters", null, null),
                new ColumnInfo(null, "parameters_before_filename", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("xmp", "id_files", null, null),
                new ColumnInfo(null, "id_file", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("exif", "id_files", null, null),
                new ColumnInfo(null, "id_file", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("collections", "id_collectionnnames", null, null),
                new ColumnInfo(null, "id_collectionnname", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("collections", "id_files", null, null),
                new ColumnInfo(null, "id_file", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("saved_searches_panels", "id_saved_searches", null, null),
                new ColumnInfo(null, "id_saved_search", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("saved_searches_keywords", "id_saved_searches", null, null),
                new ColumnInfo(null, "id_saved_search", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("actions_after_db_insertion", "id_programs", null, null),
                new ColumnInfo(null, "id_program", null, null)));
        COLUMN_RENAME_INFOS.add(new ColumnRenameInfo(
                new ColumnInfo("saved_searches", "sql_string", null, null),
                new ColumnInfo(null, "custom_sql", null, null)));
    }
    private final Collection<ColumnRenameInfo> columnToRenameInfos = new ArrayList<ColumnRenameInfo>();

    void update(Connection con) throws SQLException {
        Logger.getLogger(UpdateTablesRenameColumns.class.getName()).log(Level.INFO, "Renaming Columns");
        setColumns(con);

        if (columnToRenameInfos.size() > 0) {
            renameColumns(con);
        }
    }

    private void setColumns(Connection con) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        columnToRenameInfos.clear();

        for (ColumnRenameInfo columnRenameInfo : COLUMN_RENAME_INFOS) {
            ColumnInfo oldColumnInfo = columnRenameInfo.getOldColumnInfo();
            String oldTableName = oldColumnInfo.getTableName();
            String oldColumnName = oldColumnInfo.getColumnName();

            if (dbMeta.existsColumn(con, oldTableName, oldColumnName)) {
                columnToRenameInfos.add(columnRenameInfo);
            }
        }
    }

    private void renameColumns(Connection con) throws SQLException {
        for (ColumnRenameInfo info : columnToRenameInfos) {
            renameColumn(con, info);
        }
    }

    private void renameColumn(Connection con, ColumnRenameInfo info) throws SQLException {
        String tableName = info.getOldColumnInfo().getTableName();
        String fromColumnName = info.getOldColumnInfo().getColumnName();
        String toColumnName = info.getNewColumnInfo().getColumnName();

        if (DatabaseMetadata.INSTANCE.existsColumn(con, tableName, fromColumnName)
                && !DatabaseMetadata.INSTANCE.existsColumn(con, tableName, toColumnName)) {
            String sql = "ALTER TABLE " + tableName + " ALTER COLUMN " + fromColumnName + " RENAME TO " + toColumnName;

            Database.execute(con, sql);

            String fromIndexName = "idx_" + tableName + "_" + fromColumnName;
            String toIndexName = "idx_" + tableName + "_" + toColumnName;

            if (DatabaseMetadata.existsIndex(con, fromIndexName, tableName)
                    && !DatabaseMetadata.existsIndex(con, toIndexName, tableName)) {
                sql = "ALTER INDEX " + fromIndexName + " RENAME TO " + toIndexName;
                Database.execute(con, sql);
            }
        }
    }
}
