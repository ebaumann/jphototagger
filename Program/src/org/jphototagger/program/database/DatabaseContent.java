package org.jphototagger.program.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.metadata.MetaDataValue;

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
    public Set<String> getDistinctValuesOf(MetaDataValue column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        Set<String> content = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String columnName = column.getValueName();

            stmt = con.createStatement();

            String sql = "SELECT DISTINCT " + columnName
                         + " FROM " + column.getCategory()
                         + " WHERE " + columnName
                         + " IS NOT NULL ORDER BY 1 ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                content.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseContent.class.getName()).log(Level.SEVERE, null, ex);
            content.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return content;
    }

    /**
     * Collects for each column of a set the results of
     * {@link #getDistinctValuesOf(MetaDataValue)}.
     *
     * @param  columns columns
     * @return         distinct values of columns (not sorted)
     */
    public Set<String> getDistinctValuesOf(Set<MetaDataValue> columns) {
        if (columns == null) {
            throw new NullPointerException("columns == null");
        }

        Set<String> content = new LinkedHashSet<String>();

        for (MetaDataValue column : columns) {
            content.addAll(getDistinctValuesOf(column));
        }

        return content;
    }
}
