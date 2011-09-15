package org.jphototagger.repository.hsqldb.update.tables.v0;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.repository.RepositoryMaintainance;
import org.jphototagger.repository.hsqldb.Database;
import org.jphototagger.repository.hsqldb.DatabaseMetadata;
import org.jphototagger.repository.hsqldb.SavedSearchesDatabase;
import org.jphototagger.repository.hsqldb.update.tables.ColumnInfo;
import org.openide.util.Lookup;

/**
 * Moves content of a table column into another table related 1:n (one to many)
 * to that table.
 * <p>
 * Works only, if the column data type is a string.
 *
 * @author Elmar Baumann
 */
public final class UpdateTablesMake1n {

    private static final Map<ColumnInfo, ColumnInfo> TARGET_COL_OF = new HashMap<ColumnInfo, ColumnInfo>();
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesMake1n.class.getName());

    static {
        TARGET_COL_OF.put(new ColumnInfo("exif", "exif_recording_equipment", null, null),
                new ColumnInfo("exif_recording_equipment", "equipment", null, null));
        TARGET_COL_OF.put(new ColumnInfo("exif", "exif_lens", null, null),
                new ColumnInfo("exif_lenses", "lens", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_creator", null, null),
                new ColumnInfo("dc_creators", "creator", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "dc_rights", null, null),
                new ColumnInfo("dc_rights", "rights", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "iptc4xmpcore_location", null, null),
                new ColumnInfo("iptc4xmpcore_locations", "location", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_authorsposition", null, null),
                new ColumnInfo("photoshop_authorspositions", "authorsposition", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_captionwriter", null, null),
                new ColumnInfo("photoshop_captionwriters", "captionwriter", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_city", null, null),
                new ColumnInfo("photoshop_cities", "city", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_country", null, null),
                new ColumnInfo("photoshop_countries", "country", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_credit", null, null),
                new ColumnInfo("photoshop_credits", "credit", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_source", null, null),
                new ColumnInfo("photoshop_sources", "source", null, null));
        TARGET_COL_OF.put(new ColumnInfo("xmp", "photoshop_state", null, null),
                new ColumnInfo("photoshop_states", "state", null, null));
    }

    UpdateTablesMake1n() {
    }

    void update(Connection con) throws SQLException {
        LOGGER.log(Level.INFO, "Updating 1:n relationships");
        moveContent(con);
    }

    private String getLinkColumn(String targetTable) {
        return "id_" + targetTable;
    }

    private void moveContent(Connection con) throws SQLException {
        con.setAutoCommit(true);

        boolean compress = false;

        for (ColumnInfo source : TARGET_COL_OF.keySet()) {
            if (DatabaseMetadata.INSTANCE.existsColumn(con, source.getTableName(), source.getColumnName())) {
                ColumnInfo target = TARGET_COL_OF.get(source);
                Statement stmt = null;
                ResultSet rs = null;

                compress = true;

                try {
                    String sourceTable = source.getTableName();
                    String sourceColumn = source.getColumnName();
                    String targetTable = target.getTableName();
                    String sql = "SELECT id, " + sourceColumn + " FROM " + sourceTable;

                    addLinkColumn(con, sourceTable, targetTable);
                    stmt = con.createStatement();
                    LOGGER.log(Level.FINEST, sql);
                    rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        Long sourceId = rs.getLong(1);
                        String sourceValue = rs.getString(2);

                        if (!rs.wasNull()) {
                            copy(con, source, sourceId, sourceValue, target);
                        }
                    }

                    dropColumn(con, sourceTable, sourceColumn);
                    SavedSearchesDatabase.INSTANCE.tagSearchesIfStmtContains(sourceColumn, "!");
                } finally {
                    Database.close(rs, stmt);
                }
            }
        }

        if (compress) {
            RepositoryMaintainance repo = Lookup.getDefault().lookup(RepositoryMaintainance.class);

            repo.compressRepository();
        }
    }

    private void addLinkColumn(Connection con, String sourceTable, String targetTable) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String linkColumn = getLinkColumn(targetTable);

            addColumn(con, sourceTable, linkColumn, targetTable);
        } finally {
            Database.close(stmt);
        }
    }

    private void addColumn(Connection con, String sourceTable, String newColumn, String targetTable)
            throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsColumn(con, sourceTable, newColumn)) {
            Statement stmt = null;

            try {
                stmt = con.createStatement();

                String sqlAddColumn = "ALTER TABLE " + sourceTable + " ADD COLUMN " + newColumn + " BIGINT";

                LOGGER.log(Level.FINER, sqlAddColumn);
                stmt.executeUpdate(sqlAddColumn);

                String sqlAddForeignKey = "ALTER TABLE " + sourceTable + " ADD FOREIGN KEY (" + newColumn
                        + ") REFERENCES " + targetTable + "(id) ON DELETE SET NULL";

                LOGGER.log(Level.FINER, sqlAddForeignKey);
                stmt.executeUpdate(sqlAddForeignKey);

                String indexname = "idx_" + sourceTable + "_" + newColumn;
                String sqlCreateIndex = "CREATE INDEX " + indexname + " ON " + sourceTable + " (" + newColumn + ")";

                LOGGER.log(Level.FINER, sqlCreateIndex);
                stmt.executeUpdate(sqlCreateIndex);
                con.commit();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void dropColumn(Connection con, String table, String column) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsColumn(con, table, column)) {
            Statement stmt = null;

            try {
                String sql = "ALTER TABLE " + table + " DROP " + column;

                stmt = con.createStatement();
                LOGGER.log(Level.FINER, sql);
                stmt.executeUpdate(sql);
                sql = "DROP INDEX idx_" + table + "_" + column + " IF EXISTS";
                LOGGER.log(Level.FINER, sql);
                stmt.executeUpdate(sql);
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void copy(Connection con, ColumnInfo source, Long sourceId, String sourceValue, ColumnInfo target)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String targetTable = target.getTableName();
            String targetColumn = target.getColumnName();
            String sourceTable = source.getTableName();
            String linkColumn = getLinkColumn(targetTable);

            insertValueIntoTargetTable(con, targetTable, targetColumn, sourceValue);
            createLink(con, sourceTable, linkColumn, targetTable, targetColumn, sourceValue, sourceId);
        } finally {
            Database.close(stmt);
        }
    }

    private void insertValueIntoTargetTable(Connection con, String targetTable, String targetColumn, String value)
            throws SQLException {
        if (!Database.exists(con, targetTable, targetColumn, value)) {
            PreparedStatement stmt = null;

            try {
                String sql = "INSERT INTO " + targetTable + " (" + targetColumn + ") VALUES (?)";

                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                LOGGER.log(Level.FINER, sql);
                stmt.executeUpdate();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void createLink(Connection con, String sourceTable, String linkColumn, String targetTable,
            String targetColumn, String targetValue, Long sourceId)
            throws SQLException {
        PreparedStatement stmt = null;

        try {
            String sql = "UPDATE " + sourceTable + " SET " + linkColumn + " = ? WHERE " + sourceTable + ".id = ?";

            stmt = con.prepareStatement(sql);
            stmt.setLong(1, Database.getId(con, targetTable, targetColumn, targetValue));
            stmt.setLong(2, sourceId);
            LOGGER.log(Level.FINER, stmt.toString());
            stmt.executeUpdate();
        } finally {
            Database.close(stmt);
        }
    }
}
