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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.dialog.LongMessageDialog;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class of specialized database classes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    public static void errorMessageSqlException(SQLException ex) {
        LongMessageDialog dlg = new LongMessageDialog(null, true);
        dlg.setTitle(JptBundle.INSTANCE.getString("DatabaseTables.Error.Title"));
        dlg.setMessage(getExceptionMessage(ex));
        dlg.setVisible(true);
    }

    private static String getExceptionMessage(SQLException ex) {
        return JptBundle.INSTANCE.getString("DatabaseTables.Error", ex.getLocalizedMessage());
    }

    public static boolean execute(Connection connection, String sql) throws SQLException {
        Statement stmt        = null;
        boolean   isResultSet = false;
        try {
            stmt = connection.createStatement();
            isResultSet = stmt.execute(sql);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return isResultSet;
    }

    /**
     * Returns a connection from the Connection Pool.
     * @return The connection from the pool.
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        return ConnectionPool.INSTANCE.getConnection();
    }

    /**
     * Frees a connection in the Connection Pool so it can be reused at a later time.
     * @param connection  The connection to be freed.
     */
    protected void free(Connection connection) {
        if (connection == null) return;
        try {
            ConnectionPool.INSTANCE.free(connection);
        } catch (Exception ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(Statement stmt) {
        if (stmt == null) return;
        try {
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(PreparedStatement stmt) {
        if (stmt == null) return;
        try {
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(ResultSet rs, Statement stmt) {
        try {
            if (rs   != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs   != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    /**
     * Rolls back the transaction, catches and logs an exception when thrown
     * through <code>Connection#rollback()</code>.
     *
     * @param connection  connection
     */
    public static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (Exception ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    protected Double getDouble(ResultSet rs, int colIndex) throws SQLException {
        double d = rs.getDouble(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return d;
    }

    protected Short getShort(ResultSet rs, int colIndex) throws SQLException {
        short s = rs.getShort(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return s;
    }

    protected Integer getInt(ResultSet rs, int colIndex) throws SQLException {
        int i = rs.getInt(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return i;
    }

    protected Long getLong(ResultSet rs, int colIndex) throws SQLException {
        long l = rs.getLong(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return l;
    }

    protected Long getLongMinMax(ResultSet rs, int colIndex, long min, long max) throws SQLException {
        assert min <= max : "min: " + min + ", max: " + max;
        long l = rs.getLong(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return l < min ? min : l > max ? max : l;
    }

    protected String getString(ResultSet rs, int colIndex) throws SQLException {
        String s = rs.getString(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return s;
    }

    protected Class<?> getClassFromName(ResultSet rs, int colIndex) throws SQLException {
        String classname = rs.getString(colIndex);
        if (classname == null) return null;
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
        return null;
    }

    protected Date getDate(ResultSet rs, int colIndex) throws SQLException {
        Date d = rs.getDate(colIndex);
        if (rs.wasNull()) {
            return null;
        }
        return d;
    }

    protected void setBoolean(Boolean value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BOOLEAN);
        } else {
            stmt.setBoolean(paramIndex, value);
        }
    }

    protected void setDouble(Double value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DOUBLE);
        } else {
            stmt.setDouble(paramIndex, value);
        }
    }

    protected void setShort(Short value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.SMALLINT);
        } else {
            stmt.setShort(paramIndex, value);
        }
    }

    protected void setInt(Integer value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, value);
        }
    }

    protected void setLong(Long value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, value);
        }
    }

    protected void setString(String value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, value);
        }
    }

    protected void setClassname(Class<?> clazz, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (clazz == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, clazz.getName());
        }
    }

    protected void setDate(Date value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DATE);
        } else {
            stmt.setDate(paramIndex, value);
        }
    }

    protected void setBoolean(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof Boolean : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BOOLEAN);
        } else {
            stmt.setBoolean(paramIndex, (Boolean) value);
        }
    }

    protected void setDouble(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof Double : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DOUBLE);
        } else {
            stmt.setDouble(paramIndex, (Double) value);
        }
    }

    protected void setShort(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.SMALLINT);
        } else {
            stmt.setShort(paramIndex, (Short) value);
        }
    }

    protected void setInt(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof Integer : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, (Integer) value);
        }
    }

    protected void setLong(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof Long : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, (Long) value);
        }
    }

    protected void setLongMinMax(Object value, long min, long max, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert min <= max : "min: " + min + ", max: " + max;
        assert value == null || value instanceof Long : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            Long v = (Long) value;
            stmt.setLong(paramIndex, v < min ? min : v > max ? max : v);
        }
    }

    protected void setString(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof String : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, (String) value);
        }
    }

    protected void setDate(Object value, PreparedStatement stmt, int paramIndex) throws SQLException {
        assert value == null || value instanceof Date : value;
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DATE);
        } else {
            stmt.setDate(paramIndex, (Date) value);
        }
    }

    protected boolean notifyProgressListenerStart(
            ProgressListener listener,
            ProgressEvent    event) {

        if (listener != null) {
            listener.progressStarted(event);
            return event.isStop();
        }
        return false;
    }

    protected boolean notifyProgressListenerPerformed(
            ProgressListener listener,
            ProgressEvent    event) {

        if (listener != null) {
            listener.progressPerformed(event);
            return event.isStop();
        }
        return false;
    }

    protected void notifyProgressListenerEnd(
            ProgressListener listener,
            ProgressEvent    event) {

        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    protected void logFiner(String sql) {
        AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
    }

    protected void logFiner(PreparedStatement stmt) {
        AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt.toString());
    }

    protected void logFinest(String sql) {
        AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
    }

    protected void logFinest(PreparedStatement stmt) {
        AppLogger.logFinest(getClass(), AppLogger.USE_STRING, stmt.toString());
    }

    protected Database() {
    }
}
