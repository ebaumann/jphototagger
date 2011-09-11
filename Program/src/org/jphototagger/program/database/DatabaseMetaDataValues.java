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
final class DatabaseMetaDataValues extends Database {
    static final DatabaseMetaDataValues INSTANCE = new DatabaseMetaDataValues();

    private DatabaseMetaDataValues() {}

    /**
     * Returns the distinct ascending sorted values of a database column.
     *
     * @param  metaDataValue
     * @return        distinct sorted values
     */
    Set<String> getDistinctMetaDataValues(MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("column == null");
        }

        Set<String> content = new LinkedHashSet<String>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String columnName = metaDataValue.getValueName();

            stmt = con.createStatement();

            String sql = "SELECT DISTINCT " + columnName
                         + " FROM " + metaDataValue.getCategory()
                         + " WHERE " + columnName
                         + " IS NOT NULL ORDER BY 1 ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                content.add(rs.getString(1));
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseMetaDataValues.class.getName()).log(Level.SEVERE, null, ex);
            content.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return content;
    }

    /**
     * Collects for each column of a set the results of
     * {@link #getDistinctMetaDataValues(MetaDataValue)}.
     *
     * @param  metaDataValues
     * @return         distinct values (not sorted)
     */
    Set<String> getDistinctMetaDataValues(Set<MetaDataValue> metaDataValues) {
        if (metaDataValues == null) {
            throw new NullPointerException("columns == null");
        }

        Set<String> content = new LinkedHashSet<String>();

        for (MetaDataValue column : metaDataValues) {
            content.addAll(getDistinctMetaDataValues(column));
        }

        return content;
    }
}
