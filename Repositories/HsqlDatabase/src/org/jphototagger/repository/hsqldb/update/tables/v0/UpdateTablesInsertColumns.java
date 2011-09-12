package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;
import org.jphototagger.repository.hsqldb.update.tables.ColumnInfo;
import org.jphototagger.repository.hsqldb.update.tables.IndexOfColumn;

/**
 * Adds new columns to the database.
 *
 * @author Elmar Baumann
 */
final class UpdateTablesInsertColumns {

    private static final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesInsertColumns.class.getName());

    static {
        columns.add(new ColumnInfo("programs", "parameters_after_filename", "BINARY", null));
        columns.add(new ColumnInfo("programs", "action", "BOOLEAN",
                new IndexOfColumn("programs", "action", "idx_programs_action", false)));
        columns.add(new ColumnInfo("programs", "input_before_execute", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "input_before_execute_per_file", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "single_file_processing", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "change_file", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "use_pattern", "BOOLEAN", null));
        columns.add(new ColumnInfo("programs", "pattern", "BINARY", null));
        columns.add(new ColumnInfo("hierarchical_subjects", "real", "BOOLEAN",
                new IndexOfColumn("hierarchical_subjects", "real", "idx_hierarchical_subjects_real",
                false)));
        columns.add(new ColumnInfo("xmp", "iptc4xmpcore_datecreated", "VARCHAR_IGNORECASE(32)",
                new IndexOfColumn("xmp", "iptc4xmpcore_datecreated", "idx_iptc4xmpcore_datecreated",
                false)));
        columns.add(new ColumnInfo("metadata_edit_templates", "rating", "BINARY", null));
        columns.add(new ColumnInfo("metadata_edit_templates", "iptc4xmpcore_datecreated", "BINARY", null));
        columns.add(new ColumnInfo("exif", "exif_lens", "VARCHAR_IGNORECASE(256)",
                new IndexOfColumn("exif", "exif_lens", "idx_exif_lens", false)));
        columns.add(new ColumnInfo("saved_searches", "search_type", "SMALLINT", null));
    }

    private final List<ColumnInfo> missingColumns = new ArrayList<ColumnInfo>();

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Inserting columns");
        fixBugs(con);
        setColumns(con);

        if (missingColumns.size() > 0) {
            addColumns(con);
        }

        addSpecialColumns(con);
    }

    private void setColumns(Connection con) throws SQLException {
        DatabaseMetadata dbMeta = DatabaseMetadata.INSTANCE;

        missingColumns.clear();

        for (ColumnInfo info : columns) {
            if (!dbMeta.existsColumn(con, info.getTableName(), info.getColumnName())) {
                missingColumns.add(info);
            }
        }
    }

    private void addColumns(Connection con) throws SQLException {
        for (ColumnInfo info : missingColumns) {
            addColumn(con, info);
        }
    }

    private void addColumn(Connection con, ColumnInfo info) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE " + info.getTableName() + " ADD COLUMN " + info.getColumnName() + " "
                    + info.getDataType();

            LOGGER.log(Level.FINER, sql);
            stmt.execute(sql);

            if (info.getIndex() != null) {
                sql = info.getIndex().getSql();
                LOGGER.log(Level.FINER, sql);
                stmt.execute(sql);
            }
        } finally {
            Database.close(stmt);
        }
    }

    private void fixBugs(Connection con) throws SQLException {
        fixBugsMetaDataTemplates(con);
    }

    private void fixBugsMetaDataTemplates(Connection con) throws SQLException {
        final String tableName = "metadata_edit_templates";
        final String columnName = "rating";

        if (!DatabaseMetadata.INSTANCE.existsColumn(con, tableName, columnName)) {
            return;
        }

        List<DatabaseMetadata.ColumnInfo> infos = DatabaseMetadata.INSTANCE.getColumnInfo(con, tableName, columnName);
        boolean hasInfo = infos.size() == 1;

        assert hasInfo : infos.size();

        if (hasInfo) {
            DatabaseMetadata.ColumnInfo info = infos.get(0);
            boolean typeOk = info.DATA_TYPE == java.sql.Types.BINARY;
            boolean indexOk = info.ORDINAL_POSITION == 21;
            boolean isOk = typeOk && indexOk;

            if (!isOk) {
                dropColumn(con, tableName, columnName);
            }
        }
    }

    void dropColumn(Connection con, String tableName, String columnName) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE " + tableName + " DROP " + columnName;

            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);
        } finally {
            Database.close(stmt);
        }
    }

    private void addSpecialColumns(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, "favorite_directories", "id")) {
            Statement stmt = null;

            try {
                stmt = con.createStatement();

                String sql = "ALTER TABLE favorite_directories ADD COLUMN" + " id BIGINT GENERATED BY DEFAULT"
                        + " AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY";

                LOGGER.log(Level.FINER, sql);
                stmt.executeUpdate(sql);
            } finally {
                Database.close(stmt);
            }
        }
    }
}
