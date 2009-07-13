package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds new columns to the database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
final class UpdateTablesAddColumns {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog dialog = messages.getProgressDialog();
    private final List<ColumnInfo> missingColumns = new ArrayList<ColumnInfo>();
    private static final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();


    static {
        columns.add(new ColumnInfo("programs", "parameters_after_filename", "BINARY", // NOI18N
                null));
        columns.add(new ColumnInfo("programs", "action", "BOOLEAN", // NOI18N
                new IndexOfColumn("programs", "action", "idx_programs_action", // NOI18N
                false)));
        columns.add(new ColumnInfo("programs", "input_before_execute", "BOOLEAN", // NOI18N
                null));
        columns.add(new ColumnInfo("programs", "input_before_execute_per_file", "BOOLEAN", // NOI18N
                null));
        columns.add(new ColumnInfo("programs", "single_file_processing", "BOOLEAN", // NOI18N
                null));
        columns.add(new ColumnInfo("programs", "change_file", "BOOLEAN", // NOI18N
                null));
    }

    void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (missingColumns.size() > 0) {
            addColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;
        missingColumns.clear();
        for (ColumnInfo info : columns) {
            if (!dbMeta.existsColumn(connection, info.getTableName(), info.getColumnName())) {
                missingColumns.add(info);
            }
        }
    }

    private void addColumns(Connection connection) throws SQLException {
        dialog.setIndeterminate(true);
        messages.message(Bundle.getString("UpdateTablesAddColumns.InformationMessage.update")); // NOI18N
        for (ColumnInfo info : missingColumns) {
            addColumn(connection, info);
        }
        dialog.setIndeterminate(false);
    }

    private void addColumn(Connection connection, ColumnInfo info) throws SQLException {
        setMessage(info.getTableName(), info.getColumnName());
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + info.getTableName() + " ADD COLUMN " + // NOI18N
                info.getColumnName() + " " + info.getDataType()); // NOI18N
        if (info.getIndex() != null) {
            stmt.execute(info.getIndex().getSql());
        }
    }

    private void setMessage(String tableName, String columnName) {
        messages.message(Bundle.getString("UpdateTablesAddColumns.InformationMessage.AddColumns", // NOI18N
                tableName, columnName));
    }
}
