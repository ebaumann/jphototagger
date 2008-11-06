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
public class UpdateTablesAddNewColumns {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();
    private List<ColumnInfo> missingColumns = new ArrayList<ColumnInfo>();
    private static List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
    

    static {
        columns.add(new ColumnInfo("programs", "action", "BOOLEAN",
            new IndexOfColumn("programs", "action", "idx_programs_action", false)));
        columns.add(new ColumnInfo("programs", "input_before_execute", "BOOLEAN",
            null));
    }

    synchronized void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (missingColumns.size() > 0) {
            addColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        missingColumns.clear();
        for (ColumnInfo info : columns) {
            if (!dbMeta.existsColumn(connection, info.tableName, info.columnName)) {
                missingColumns.add(info);
            }
        }
    }

    private synchronized void addColumns(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        for (ColumnInfo info : missingColumns) {
            addColumn(connection, info);
        }
        dialog.setIntermediate(false);
    }

    private void addColumn(Connection connection, ColumnInfo info) throws SQLException {
        setMessage(info.tableName, info.columnName);
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + info.tableName + " ADD COLUMN " + // NOI18N
            info.columnName + " " + info.dataType); // NOI18N
        if (info.index != null) {
            stmt.execute(info.index.getSql());
        }
    }

    private void setMessage(String tableName, String columnName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("UpdateTablesAddNewColumns.InformationMessage.AddColumns"));
        Object[] params = {tableName, columnName};
        messages.message(msg.format(params));
    }

    private static class ColumnInfo {

        public String tableName;
        public String columnName;
        public String dataType;
        public IndexOfColumn index;

        public ColumnInfo(String tableName, String columnName, String dataType, IndexOfColumn index) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.dataType = dataType;
            this.index = index;
        }
    }
}
