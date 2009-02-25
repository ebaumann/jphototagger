package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
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
        columns.add(new ColumnInfo("programs", "parameters_after_filename", "BINARY",
            null));
        columns.add(new ColumnInfo("programs", "action", "BOOLEAN",
            new IndexOfColumn("programs", "action", "idx_programs_action", false)));
        columns.add(new ColumnInfo("programs", "input_before_execute", "BOOLEAN",
            null));
        columns.add(new ColumnInfo("programs", "input_before_execute_per_file", "BOOLEAN",
            null));
        columns.add(new ColumnInfo("programs", "single_file_processing", "BOOLEAN",
            null));
        columns.add(new ColumnInfo("programs", "change_file", "BOOLEAN",
            null));
    }

    synchronized void update(Connection connection) throws SQLException {
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

    private synchronized void addColumns(Connection connection) throws SQLException {
        dialog.setIndeterminate(true);
        messages.message(Bundle.getString("UpdateTablesAddNewColumns.InformationMessage.update"));
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
        MessageFormat msg = new MessageFormat(
            Bundle.getString("UpdateTablesAddNewColumns.InformationMessage.AddColumns"));
        Object[] params = {tableName, columnName};
        messages.message(msg.format(params));
    }
}
