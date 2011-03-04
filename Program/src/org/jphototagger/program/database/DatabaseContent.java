package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.metadata.Column;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseContent extends Database {
    public static final DatabaseContent INSTANCE = new DatabaseContent();

    private DatabaseContent() {}

    /**
     * Returns the distinct ascending sorted values of a database column.
     *
     * @param  column column
     * @return        distinct sorted values of that column
     */
    public Set<String> getDistinctValuesOf(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        Set<String> content = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String columnName = column.getName();

            stmt = con.createStatement();

            String sql = "SELECT DISTINCT " + columnName 
                         + " FROM " + column.getTablename()
                         + " WHERE " + columnName
                         + " IS NOT NULL ORDER BY 1 ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                content.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseContent.class, ex);
            content.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return content;
    }

    /**
     * Collects for each column of a set the results of
     * {@link #getDistinctValuesOf(Column)}.
     *
     * @param  columns columns
     * @return         distinct values of columns (not sorted)
     */
    public Set<String> getDistinctValuesOf(Set<Column> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Set<String> content = new LinkedHashSet<String>();

        for (Column column : columns) {
            content.addAll(getDistinctValuesOf(column));
        }

        return content;
    }
}
