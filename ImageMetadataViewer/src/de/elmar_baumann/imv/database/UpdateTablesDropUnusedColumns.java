package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsId;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalCategoriesId;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Drops unused columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/31
 */
class UpdateTablesDropUnusedColumns {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();
    private List<Column> dropColumns = new ArrayList<Column>();
    private static List<Column> columns = new ArrayList<Column>();
    

    static {
        columns.add(ColumnXmpDcSubjectsId.getInstance());
        columns.add(ColumnXmpPhotoshopSupplementalCategoriesId.getInstance());
    }

    synchronized void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (dropColumns.size() > 0) {
            dropColumns(connection);
        }
    }

    private synchronized void dropColumns(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        for (Column column : dropColumns) {
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            setMessage(tableName, columnName);
            Statement stmt = connection.createStatement();
            stmt.execute(
                "ALTER TABLE " + tableName + " DROP COLUMN " + columnName); // NOI18N
        }
        dialog.setIntermediate(false);
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        dropColumns.clear();
        for (Column column : columns) {
            if (dbMeta.existsColumn(
                connection, column.getTable().getName(), column.getName())) {
                dropColumns.add(column);
            }
        }
    }

    private void setMessage(String tableName, String columnName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("UpdateTablesDropUnusedColumns.InformationMessage"));
        Object[] params = {tableName, columnName};
        messages.message(msg.format(params));
    }
}
