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
 * Drops unused columns.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/31
 */
class UpdateTablesDropColumns {

    private UpdateTablesMessages messages = UpdateTablesMessages.getInstance();
    private ProgressDialog dialog = messages.getProgressDialog();
    private List<ColumnInfo> dropColumns = new ArrayList<ColumnInfo>();
    private static List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
    

    static {
        
        columns.add(new ColumnInfo("xmp_dc_subjects", "id", null, null));
        columns.add(new ColumnInfo("xmp_photoshop_supplementalcategories", "id", null, null));
        columns.add(new ColumnInfo("autoscan_directories", "id", null, null));
        columns.add(new ColumnInfo("favorite_directories", "id", null, null));
        columns.add(new ColumnInfo("file_exclude_pattern", "id", null, null));
        columns.add(new ColumnInfo("metadata_edit_templates", "id", null, null));
    }

    synchronized void update(Connection connection) throws SQLException {
        setColumns(connection);
        if (dropColumns.size() > 0) {
            dropColumns(connection);
        }
    }

    private void setColumns(Connection connection) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.getInstance();
        dropColumns.clear();
        for (ColumnInfo info : columns) {
            if (dbMeta.existsColumn(connection, info.tableName, info.columnName)) {
                dropColumns.add(info);
            }
        }
    }

    private synchronized void dropColumns(Connection connection) throws SQLException {
        dialog.setIntermediate(true);
        messages.message(Bundle.getString("UpdateTablesDropUnusedColumns.InformationMessage.update"));
        for (ColumnInfo info : dropColumns) {
            dropColumn(connection, info.tableName, info.columnName);
        }
        dialog.setIntermediate(false);
    }

    private void dropColumn(Connection connection, String tableName, String columnName) throws SQLException {
        setMessage(tableName, columnName);
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName); // NOI18N
    }

    private void setMessage(String tableName, String columnName) {
        MessageFormat msg = new MessageFormat(
            Bundle.getString("UpdateTablesDropUnusedColumns.InformationMessage"));
        Object[] params = {tableName, columnName};
        messages.message(msg.format(params));
    }
}
