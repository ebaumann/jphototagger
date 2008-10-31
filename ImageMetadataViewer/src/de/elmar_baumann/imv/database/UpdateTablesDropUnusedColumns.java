package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsId;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalCategoriesId;
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
 * Drops unused columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/31
 */
class UpdateTablesDropUnusedColumns {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();
    private List<Column> dropColumns = new ArrayList<Column>();
    private List<Pair<String, String>> dropColumnOfTable = new ArrayList<Pair<String, String>>();
    private static List<Column> columns = new ArrayList<Column>();
    private static List<Pair<String, String>> columnOfTable = new ArrayList<Pair<String, String>>();
    

    static {
        columns.add(ColumnXmpDcSubjectsId.getInstance());
        columns.add(ColumnXmpPhotoshopSupplementalCategoriesId.getInstance());

        columnOfTable.add(new Pair<String, String>("autoscan_directories", "id"));
        columnOfTable.add(new Pair<String, String>("favorite_directories", "id"));
        columnOfTable.add(new Pair<String, String>("file_exclude_pattern", "id"));
        columnOfTable.add(new Pair<String, String>("metadata_edit_templates", "id"));
    }

    synchronized void update(Connection connection) throws SQLException {
        setColumns(connection);
        setColumnOfTable(connection);
        if (dropColumns.size() > 0) {
            dropColumns(connection);
        }
        if (dropColumnOfTable.size() > 0) {
            dropColumnOfTable(connection);
        }
    }

    private synchronized void dropColumns(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        for (Column column : dropColumns) {
            dropColumn(connection, column.getTable().getName(), column.getName());
        }
        dialog.setIntermediate(false);
    }

    private synchronized void dropColumnOfTable(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        for (Pair<String, String> pair : columnOfTable) {
            dropColumn(connection, pair.getFirst(), pair.getSecond());
        }
        dialog.setIntermediate(false);
    }

    private void dropColumn(Connection connection, String tableName, String columnName) throws SQLException {
        setMessage(tableName, columnName);
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName); // NOI18N
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        dropColumns.clear();
        for (Column column : columns) {
            if (dbMeta.existsColumn(connection, column.getTable().getName(), column.getName())) {
                dropColumns.add(column);
            }
        }
    }

    private void setColumnOfTable(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        dropColumnOfTable.clear();
        for (Pair<String, String> pair : columnOfTable) {
            if (dbMeta.existsColumn(connection, pair.getFirst(), pair.getSecond())) {
                dropColumnOfTable.add(pair);
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
