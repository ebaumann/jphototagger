package org.jphototagger.program.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 *
 *
 * @author Elmar Baumann
 */
final class DatabaseStatistics extends Database {

    static final DatabaseStatistics INSTANCE = new DatabaseStatistics();

    private DatabaseStatistics() {
    }

    /**
     * Returns the count of records in a table of specific column (where
     * the column value is not NULL).
     *
     * @param  metaDataValue  column
     * @return count count of records in the column's table where
     * <code>column</code> is not null
     */
    int getCountOfMetaDataValue(MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        int count = -1;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM " + metaDataValue.getCategory()
                    + " WHERE " + metaDataValue.getValueName() + " IS NOT NULL";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseStatistics.class.getName()).log(Level.SEVERE, null, ex);
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
    int getFileCount() {
        int count = -1;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM files";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseStatistics.class.getName()).log(Level.SEVERE, null, ex);
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
    int getXmpCount() {
        int count = -1;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM xmp LEFT JOIN files ON xmp.id_file = files.id";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Returns whether one column in a list of columns has at least one value.
     *
     * @param  metaDataValues
     * @param  value    value
     * @return true if the value existsValueInMetaDataValues into the column
     */
    boolean existsValueInMetaDataValues(String value, List<MetaDataValue> metaDataValues) {
        if (metaDataValues == null) {
            throw new NullPointerException("columns == null");
        }

        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            int size = metaDataValues.size();

            for (int i = 0; !exists && (i < size); i++) {
                MetaDataValue column = metaDataValues.get(i);

                stmt = con.prepareStatement("SELECT COUNT(*) FROM " + column.getCategory()
                        + " WHERE " + column.getValueName() + " = ?");
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
            Logger.getLogger(DatabaseStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Returns whether a column has at least one value.
     *
     * @param  metaDataValue  column
     * @param  value   value
     * @return true if the value existsValueInMetaDataValues in the column
     */
    boolean existsMetaDataValue(MetaDataValue metaDataValue, String value) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM " + metaDataValue.getCategory()
                    + " WHERE " + metaDataValue.getValueName() + " = ?");
            stmt.setString(1, value);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseStatistics.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }
}
