package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.app.update.tables.ColumnInfo;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.resource.JptBundle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Drops unused columns.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesDropColumns {
    private static final List<ColumnInfo> COLUMNS = new ArrayList<ColumnInfo>();

    static {
        COLUMNS.add(new ColumnInfo("autoscan_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("favorite_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("file_exclude_pattern", "id", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "id", null, null));
        COLUMNS.add(new ColumnInfo("xmp", "iptc4xmpcore_countrycode", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "iptc4xmpcoreCountrycode", null, null));
        COLUMNS.add(new ColumnInfo("saved_searches", "is_query", null, null));
    }

    private final List<ColumnInfo> dropColumns = new ArrayList<ColumnInfo>();

    void update(Connection con) throws SQLException {
        startMessage();
        setColumns(con);

        if (dropColumns.size() > 0) {
            dropColumns(con);
        }

        SplashScreen.INSTANCE.removeMessage();
    }

    private void setColumns(Connection con) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        dropColumns.clear();

        for (ColumnInfo info : COLUMNS) {
            if (dbMeta.existsColumn(con, info.getTableName(), info.getColumnName())) {
                dropColumns.add(info);
            }
        }
    }

    private void dropColumns(Connection con) throws SQLException {
        for (ColumnInfo info : dropColumns) {
            dropColumn(con, info.getTableName(), info.getColumnName());
        }
    }

    private void dropColumn(Connection con, String tableName, String columnName) throws SQLException {
        Database.execute(con, "ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("UpdateTablesDropColumns.Info"));
    }
}
