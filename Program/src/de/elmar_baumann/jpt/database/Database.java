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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Base class of specialized database classes.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    /**
     * Returns a connection from the Connection Pool.
     * @return The connection from the pool.
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    /**
     * Frees a connection in the Connection Pool so it can be reused at a later time.
     * @param connection  The connection to be freed.
     */
    protected void free(Connection connection) {
        if (connection != null) {
            try {
                ConnectionPool.getInstance().free(connection);
            } catch (Exception ex) {
                AppLogger.logSevere(Database.class, ex);
            }
        }
    }

    /**
     * Rolls back the transaction, catches and logs an exception when thrown
     * through <code>Connection#rollback()</code>.
     *
     * @param connection  connection
     */
    protected void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (Exception ex) {
            AppLogger.logSevere(Database.class, ex);
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

    protected void setLong(PreparedStatement stmt, int paramIndex, Long value) throws SQLException {
        if (value == null) {
            stmt.setNull(paramIndex, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(paramIndex, value);
        }
    }

    protected void setString(PreparedStatement stmt, int paramIndex, String s) throws SQLException {
        if (s == null) {
            stmt.setNull(paramIndex, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(paramIndex, s);
        }
    }
}
