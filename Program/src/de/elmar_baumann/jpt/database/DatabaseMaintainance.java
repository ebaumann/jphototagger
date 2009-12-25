/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.types.SubstringPosition;
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
                    "DatabaseMaintainance.Info.Shutdown");
            stmt.executeUpdate("SHUTDOWN");
        } catch (SQLException ex) {
            AppLog.logSevere(Database.class, ex);
            MessageDisplayer.error(null, "DatabaseMaintainance.Error.Shutdown");
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
            stmt.executeUpdate("CHECKPOINT DEFRAG");
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
            String sql = "UPDATE " + tableName + " SET " + columnName +
                    " = REPLACE(" + columnName + ", '" + quotedSearch + "', '" +
                    quotedReplacement + "') WHERE " + columnName + " " +
                    SubstringPosition.getSqlFilterOperator(pos) + " ?"; //;
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, SubstringPosition.getSqlFilter(pos, search));
            logFiner(stmt);
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
        return s.replace("'", "\\'");
    }
}
