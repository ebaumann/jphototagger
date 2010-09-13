/*
 * @(#)Database.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.database;

import org.jphototagger.lib.dialog.LongMessageDialog;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;

import java.io.File;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Base class of specialized database classes.
 *
 * @author  Elmar Baumann
 */
public class Database {
    protected Database() {}

    public static void errorMessageSqlException(SQLException ex) {
        LongMessageDialog dlg = new LongMessageDialog(null, true);

        dlg.setTitle(
            JptBundle.INSTANCE.getString("DatabaseTables.Error.Title"));
        dlg.setLongMessage(getExceptionMessage(ex));
        dlg.setVisible(true);
    }

    private static String getExceptionMessage(SQLException ex) {
        return JptBundle.INSTANCE.getString("DatabaseTables.Error",
                ex.getLocalizedMessage());
    }

    public static boolean execute(Connection con, String sql)
            throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (sql == null) {
            throw new NullPointerException("sql == null");
        }

        Statement stmt        = null;
        boolean   isResultSet = false;

        try {
            stmt = con.createStatement();
            AppLogger.logFiner(Database.class, AppLogger.USE_STRING, sql);
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
     * Returns the path of file as it will be stored into the database.
     * <p>
     * This maybe differend from {@link File#getAbsolutePath()}.
     *
     * @param  file file
     * @return      path as stored in the database
     */
    public static String getFilePath(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return file.getAbsolutePath();
    }

    /**
     * Returns a file from a file path as stored into the database.
     *
     * @param  filePath path as returned from {@link #getFilePath(File)}
     * @return file
     */
    public static File getFile(String filePath) {
        if (filePath == null) {
            throw new NullPointerException("filePath == null");
        }

        return new File(filePath);
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
     * Frees a connection in the Connection Pool so it can be reused at a late
     * time.
     *
     * @param con the connection to be freed; null is allowed
     *            (then this mehtod does nothing)
     */
    protected void free(Connection con) {
        if (con == null) {
            return;
        }

        try {
            ConnectionPool.INSTANCE.free(con);
        } catch (Exception ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(Statement stmt) {
        if (stmt == null) {
            return;
        }

        try {
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(PreparedStatement stmt) {
        if (stmt == null) {
            return;
        }

        try {
            stmt.close();
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }

            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static void close(ResultSet rs, PreparedStatement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }

            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    /**
     * Rolls back the transaction, catches and logs an exception when thrown
     * through <code>Connection#rollback()</code>.
     *
     * @param con  connection
     */
    public static void rollback(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.rollback();
        } catch (Exception ex) {
            AppLogger.logSevere(Database.class, ex);
        }
    }

    public static Long getId(Connection con, String tablename,
                             String columnName, String value)
            throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        Long              id   = null;

        if (value == null) {
            return null;
        }

        try {
            String sql = "SELECT id FROM " + tablename + " WHERE " + columnName
                         + " = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, value);
            AppLogger.logFinest(Database.class, AppLogger.USE_STRING, stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }

        return id;
    }

    protected Long getId(String tablename, String columnName, String value)
            throws SQLException {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        Long              id   = null;

        if (value == null) {
            return null;
        }

        try {
            String sql = "SELECT id FROM " + tablename + " WHERE " + columnName
                         + " = ?";

            con  = getConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, value);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
            free(con);
        }

        return id;
    }

    protected Long ensureValueExists(String tablename, String columnName,
                                     String value)
            throws SQLException {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        if (value == null) {
            return null;
        }

        Long id = getId(tablename, columnName, value);

        if (id == null) {
            PreparedStatement stmt = null;
            Connection        con  = null;

            try {
                String sql = "INSERT INTO " + tablename + " (" + columnName
                             + ") VALUES (?)";

                con = getConnection();
                con.setAutoCommit(true);
                stmt = con.prepareStatement(sql);
                stmt.setString(1, value);
                logFiner(stmt);
                stmt.executeUpdate();
                id = getId(tablename, columnName, value);
            } finally {
                close(stmt);
                free(con);
            }
        }

        return id;
    }

    public static long getCount(Connection con, String tablename,
                                String columnName, String value)
            throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        long              count = 0;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM " + tablename
                                        + " WHERE " + columnName + " = ?");
            stmt.setString(1, value);
            AppLogger.logFinest(Database.class, AppLogger.USE_STRING, stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }

        return count;
    }

    public static long getCount(Connection con, String tablename,
                                String columnName, long value)
            throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        long              count = 0;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM " + tablename
                                        + " WHERE " + columnName + " = ?");
            stmt.setLong(1, value);
            AppLogger.logFinest(Database.class, AppLogger.USE_STRING, stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }

        return count;
    }

    public static boolean exists(Connection con, String tablename,
                                 String columnName, String value)
            throws SQLException {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        if (columnName == null) {
            throw new NullPointerException("columnName == null");
        }

        return getCount(con, tablename, columnName, value) > 0;
    }

    protected Double getDouble(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        double d = rs.getDouble(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return d;
    }

    protected Short getShort(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        short s = rs.getShort(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return s;
    }

    protected Integer getInt(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        int i = rs.getInt(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return i;
    }

    protected Long getLong(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        long l = rs.getLong(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return l;
    }

    protected Long getLongMinMax(ResultSet rs, int colIndex, long min, long max)
            throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        if (min > max) {
            throw new IllegalArgumentException("min > max!");
        }

        long l = rs.getLong(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return (l < min)
               ? min
               : (l > max)
                 ? max
                 : l;
    }

    protected String getString(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        String s = rs.getString(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return s;
    }

    protected Class<?> getClassFromName(ResultSet rs, int colIndex)
            throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        String classname = rs.getString(colIndex);

        if (classname == null) {
            return null;
        }

        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException ex) {
            AppLogger.logSevere(Database.class, ex);
        }

        return null;
    }

    protected Date getDate(ResultSet rs, int colIndex) throws SQLException {
        if (rs == null) {
            throw new NullPointerException("rs == null");
        }

        Date d = rs.getDate(colIndex);

        if (rs.wasNull()) {
            return null;
        }

        return d;
    }

    protected void setBoolean(Boolean value, PreparedStatement stmt,
                              int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BOOLEAN);
        } else {
            stmt.setBoolean(paramIndex, value);
        }
    }

    protected void setDouble(Double value, PreparedStatement stmt,
                             int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DOUBLE);
        } else {
            stmt.setDouble(paramIndex, value);
        }
    }

    protected void setShort(Short value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.SMALLINT);
        } else {
            stmt.setShort(paramIndex, value);
        }
    }

    protected void setInt(Integer value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, value);
        }
    }

    protected void setLong(Long value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, value);
        }
    }

    protected void setString(String value, PreparedStatement stmt,
                             int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, value);
        }
    }

    protected void setClassname(Class<?> clazz, PreparedStatement stmt,
                                int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (clazz == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, clazz.getName());
        }
    }

    protected void setDate(Date value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DATE);
        } else {
            stmt.setDate(paramIndex, value);
        }
    }

    protected void setBoolean(Object value, PreparedStatement stmt,
                              int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isBoolean = (value == null) || (value instanceof Boolean);

        if (!isBoolean) {
            throw new IllegalArgumentException("Not a Boolean: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BOOLEAN);
        } else {
            stmt.setBoolean(paramIndex, (Boolean) value);
        }
    }

    protected void setDouble(Object value, PreparedStatement stmt,
                             int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isDouble = (value == null) || (value instanceof Double);

        if (!isDouble) {
            throw new IllegalArgumentException("Not a Double: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DOUBLE);
        } else {
            stmt.setDouble(paramIndex, (Double) value);
        }
    }

    protected void setShort(Object value, PreparedStatement stmt,
                            int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isShort = (value == null) || (value instanceof Short);

        if (!isShort) {
            throw new IllegalArgumentException("Not a Short: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.SMALLINT);
        } else {
            stmt.setShort(paramIndex, (Short) value);
        }
    }

    protected void setInt(Object value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isInteger = (value == null) || (value instanceof Integer);

        if (!isInteger) {
            throw new IllegalArgumentException("Not an Integer: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.INTEGER);
        } else {
            stmt.setInt(paramIndex, (Integer) value);
        }
    }

    protected void setLong(Object value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isLong = (value == null) || (value instanceof Long);

        if (!isLong) {
            throw new IllegalArgumentException("Not a Long: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, (Long) value);
        }
    }

    protected void setLongMinMax(Object value, long min, long max,
                                 PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        if (min > max) {
            throw new IllegalArgumentException("min > max");
        }

        boolean isLong = (value == null) || (value instanceof Long);

        if (!isLong) {
            throw new IllegalArgumentException("Not a Long: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            Long v = (Long) value;

            stmt.setLong(paramIndex, (v < min)
                                     ? min
                                     : (v > max)
                                       ? max
                                       : v);
        }
    }

    protected void setString(Object value, PreparedStatement stmt,
                             int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isString = (value == null) || (value instanceof String);

        if (!isString) {
            throw new IllegalArgumentException("Not a string: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, (String) value);
        }
    }

    protected void setDate(Object value, PreparedStatement stmt, int paramIndex)
            throws SQLException {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        boolean isDate = (value == null) || (value instanceof Date);

        if (!isDate) {
            throw new IllegalArgumentException("Not a Date: " + value);
        }

        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.DATE);
        } else {
            stmt.setDate(paramIndex, (Date) value);
        }
    }

    /**
     * Notifies a progress listener, that the progress has been started.
     *
     * @param  listener listener, can be null
     * @param  event     event
     * @return          {@link ProgressEvent#isCancel()}
     */
    protected boolean notifyProgressListenerStart(ProgressListener listener,
            ProgressEvent event) {
        if (listener != null) {
            listener.progressStarted(event);

            return event.isCancel();
        }

        return false;
    }

    /**
     * Notifies a progress listener, that the progress has been performed.
     *
     * @param  listener listener, can be null
     * @param  event     event
     * @return          {@link ProgressEvent#isCancel()}
     */
    protected boolean notifyProgressListenerPerformed(
            ProgressListener listener, ProgressEvent event) {
        if (listener != null) {
            listener.progressPerformed(event);

            return event.isCancel();
        }

        return false;
    }

    /**
     * Notifies a progress listener, that the progress has been ended.
     *
     * @param  listener listener, can be null
     * @param  event     event
     */
    protected void notifyProgressListenerEnd(ProgressListener listener,
            ProgressEvent event) {
        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    protected void logFiner(String sql) {
        if (sql == null) {
            throw new NullPointerException("sql == null");
        }

        AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
    }

    protected void logFiner(PreparedStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt.toString());
    }

    protected void logFinest(String sql) {
        if (sql == null) {
            throw new NullPointerException("sql == null");
        }

        AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
    }

    protected void logFinest(PreparedStatement stmt) {
        if (stmt == null) {
            throw new NullPointerException("stmt == null");
        }

        AppLogger.logFinest(getClass(), AppLogger.USE_STRING, stmt.toString());
    }
}
