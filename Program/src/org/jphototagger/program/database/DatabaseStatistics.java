package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.Column;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseStatistics extends Database {
    public static final DatabaseStatistics INSTANCE = new DatabaseStatistics();

    private DatabaseStatistics() {}

    /**
     * Returns the count of records in a table of specific column (where
     * the column value is not NULL).
     *
     * @param  column  column
     * @return count count of records in the column's table where
     * <code>column</code> is not null
     */
    public int getTotalRecordCountOf(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM " + column.getTablename()
                         + " WHERE " + column.getName() + " IS NOT NULL";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseStatistics.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Liefert die Anzahl der Dateien in der Datenbank.
     *
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getFileCount() {
        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM files";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseStatistics.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Liefert die Anzahl der Dateien mit XMP-Daten in der Datenbank.
     *
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getXmpCount() {
        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql =
                "SELECT COUNT(*)"
                + " FROM xmp LEFT JOIN files ON xmp.id_file = files.id";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseStatistics.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Returns whether one column in a list of columns has at least one value.
     *
     * @param  columns  columns
     * @param  value    value
     * @return true if the value existsValueIn into the column
     */
    public boolean existsValueIn(List<Column> columns, String value) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con = getConnection();

            int size = columns.size();

            for (int i = 0; !exists && (i < size); i++) {
                Column column = columns.get(i);

                stmt = con.prepareStatement("SELECT COUNT(*) FROM "
                                            + column.getTablename() + " WHERE "
                                            + column.getName() + " = ?");
                stmt.setString(1, value);
                logFinest(stmt);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }

                rs.close();
                stmt.close();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseStatistics.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Returns whether a column has at least one value.
     *
     * @param  column  column
     * @param  value   value
     * @return true if the value existsValueIn in the column
     */
    public boolean existsValueIn(Column column, String value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM "
                                        + column.getTablename() + " WHERE "
                                        + column.getName() + " = ?");
            stmt.setString(1, value);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseStatistics.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }
}
