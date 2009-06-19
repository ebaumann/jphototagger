package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.types.SubstringPosition;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseMaintainance extends Database {

    private static final DatabaseMaintainance instance =
            new DatabaseMaintainance();

    public static DatabaseMaintainance getInstacne() {
        return instance;
    }

    private DatabaseMaintainance() {
    }

    /**
     * Komprimiert die Datenbank.
     *
     * @return true, wenn die Datenbank erfolgreich komprimiert wurde
     */
    public boolean compressDatabase() {
        boolean success = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CHECKPOINT DEFRAG"); // NOI18N
            success = true;
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMaintainance.class, ex);
        } finally {
            free(connection);
        }
        return success;
    }

    /**
     * Replaces in a column all strings or substrings with another string.
     *
     * @param  column      column <em>has to be of the type</em>
     *                     {@link Column#DataType
     * @param  search      string to replace
     * @param  replacement string that replaces <code>search</code>
     * @param  pos         position of the string to search
     * @return             count of changed strings (affected rows)
     */
    public int replaceString(Column column, String search, String replacement,
            SubstringPosition pos) {
        int affectedRows = 0;
        if (!column.getDataType().equals(Column.DataType.STRING))
            return 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            String tableName = column.getTable().getName();
            String columnName = column.getName();
            String quotedSearch = escapeString(search);
            String quotedReplacement = escapeString(replacement);
            String sql = "UPDATE " + tableName + " SET " + columnName + // NOI18N
                    " = REPLACE(" + columnName + ", '" + quotedSearch + "', '" + // NOI18N
                    quotedReplacement + "') WHERE " + columnName + " " + // NOI18N
                    SubstringPosition.getSqlFilterOperator(pos) + " ?"; // NOI18N;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, SubstringPosition.getSqlFilter(pos, search));
            AppLog.logFiner(DatabaseMaintainance.class, stmt.toString());
            affectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return affectedRows;
    }

    private String escapeString(String s) {
        return s.replace("\\", "\\\\'").replace("'", "\\'"); // NOI18N
    }
}
