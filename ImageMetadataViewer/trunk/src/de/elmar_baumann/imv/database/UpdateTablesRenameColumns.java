package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import de.elmar_baumann.lib.generics.Pair;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
final class UpdateTablesRenameColumns {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog dialog = messages.getProgressDialog();
    private final List<Pair<ColumnInfo, ColumnInfo>> renameColumns =
            new ArrayList<Pair<ColumnInfo, ColumnInfo>>();
    private static final List<Pair<ColumnInfo, ColumnInfo>> COLUMNS =
            new ArrayList<Pair<ColumnInfo, ColumnInfo>>();

    static {
        COLUMNS.add(new Pair<ColumnInfo, ColumnInfo>(
                new ColumnInfo("programs", "parameters", null, null), // NOI18N
                new ColumnInfo(null, "parameters_before_filename", null, null))); // NOI18N
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
            if (dbMeta.existsColumn(
                    connection, info.getFirst().getTableName(), info.getFirst().
                    getColumnName())) {
                renameColumns.add(info);
            }
        }
    }

    private void renameColumns(Connection connection) throws SQLException {
        dialog.setIndeterminate(true);
        messages.message(Bundle.getString(
                "UpdateTableRenameColumns.Info.update")); // NOI18N
        for (Pair<ColumnInfo, ColumnInfo> info : renameColumns) {
            renameColumn(connection, info);
        }
        dialog.setIndeterminate(false);
    }

    private void renameColumn(Connection connection,
            Pair<ColumnInfo, ColumnInfo> info) throws SQLException {
        setMessage(info.getFirst().getTableName(),
                info.getFirst().getColumnName());
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + // NOI18N
                info.getFirst().getTableName() +
                " ALTER COLUMN " + // NOI18N
                info.getFirst().getColumnName() + // NOI18N
                " RENAME TO " + // NOI18N
                info.getSecond().getColumnName());
    }

    private void setMessage(String tableName, String columnName) {
        messages.message(Bundle.getString(
                "UpdateTableRenameColumns.Info.RenameColumn", // NOI18N
                tableName, columnName));
    }
}
