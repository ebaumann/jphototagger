package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import de.elmar_baumann.lib.template.Pair;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
class UpdateTableRenameColumns {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();
    private List<Pair<ColumnInfo, ColumnInfo>> renameColumns = new ArrayList<Pair<ColumnInfo, ColumnInfo>>();
    private static List<Pair<ColumnInfo, ColumnInfo>> columns = new ArrayList<Pair<ColumnInfo, ColumnInfo>>();
    

    static {
        columns.add(new Pair<ColumnInfo, ColumnInfo>(
            new ColumnInfo("programs", "parameters", null, null),
            new ColumnInfo(null, "parameters_before_filename", null, null)));
    }

    synchronized void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (renameColumns.size() > 0) {
            renameColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        renameColumns.clear();
        for (Pair<ColumnInfo, ColumnInfo> info : columns) {
            if (dbMeta.existsColumn(
                connection, info.getFirst().tableName, info.getFirst().columnName)) {
                renameColumns.add(info);
            }
        }
    }

    private synchronized void renameColumns(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        messages.message(Bundle.getString("UpdateTableRenameColumns.InformationMessage.update"));
        for (Pair<ColumnInfo, ColumnInfo> info : renameColumns) {
            renameColumn(connection, info);
        }
        dialog.setIntermediate(false);
    }

    private void renameColumn(Connection connection, Pair<ColumnInfo, ColumnInfo> info) throws SQLException {
        setMessage(info.getFirst().tableName, info.getFirst().columnName);
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + // NOI18N
            info.getFirst().tableName +
            " ALTER COLUMN " + // NOI18N
            info.getFirst().columnName + // NOI18N
            " RENAME TO " + // NOI18N
            info.getSecond().columnName);
    }

    private void setMessage(String tableName, String columnName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("UpdateTableRenameColumns.InformationMessage.RenameColumn"));
        Object[] params = {tableName, columnName};
        messages.message(msg.format(params));
    }
}
