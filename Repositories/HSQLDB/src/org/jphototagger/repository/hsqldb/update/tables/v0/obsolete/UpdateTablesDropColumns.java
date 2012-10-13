package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;

/**
 * Drops unused columns.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesDropColumns {

    private static final List<ColumnInfo> COLUMNS = new ArrayList<>();

    static {
        COLUMNS.add(new ColumnInfo("autoscan_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("favorite_directories", "id", null, null));
        COLUMNS.add(new ColumnInfo("file_exclude_pattern", "id", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "id", null, null));
        COLUMNS.add(new ColumnInfo("xmp", "iptc4xmpcore_countrycode", null, null));
        COLUMNS.add(new ColumnInfo("metadata_edit_templates", "iptc4xmpcoreCountrycode", null, null));
        COLUMNS.add(new ColumnInfo("saved_searches", "is_query", null, null));
    }
    private final List<ColumnInfo> dropColumns = new ArrayList<>();

    void update(Connection con) throws SQLException {
        Logger.getLogger(UpdateTablesDropColumns.class.getName()).log(Level.INFO, "Dropping columns");
        setColumns(con);

        if (dropColumns.size() > 0) {
            dropColumns(con);
        }
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
}
