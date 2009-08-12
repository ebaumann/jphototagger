package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.SubstringPosition;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseMaintainance extends Database {

    public static final DatabaseMaintainance INSTANCE =
            new DatabaseMaintainance();

    private DatabaseMaintainance() {
    }

    /**
     * Shuts down the database.
     */
    public void shutdown() {
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            AppLog.logInfo(DatabaseMaintainance.class,
                    Bundle.getString("DatabaseMaintainance.Info.Shutdown")); // NOI18N
            stmt.executeUpdate("SHUTDOWN"); // NOI18N
        } catch (SQLException ex) {
            AppLog.logSevere(Database.class, ex);
            MessageDisplayer.error(null, "DatabaseMaintainance.Error.Shutdown"); // NOI18N
        }
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
     *                     {@link Column#dataType}
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
            String quotedSearch = escapeStringForQuotes(search);
            String quotedReplacement = escapeStringForQuotes(replacement);
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
            AppLog.logSevere(DatabaseImageFiles.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return affectedRows;
    }

    private String escapeStringForQuotes(String s) {
        return s.replace("'", "\\'"); // NOI18N
    }
}
