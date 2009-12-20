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
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseListener;
import de.elmar_baumann.jpt.event.DatabaseProgramEvent;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Base class of specialized database classes.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    private static final Set<DatabaseListener> DATABASE_LISTENERS =
            new LinkedHashSet<DatabaseListener>();

    public synchronized void addDatabaseListener(DatabaseListener listener) {
        DATABASE_LISTENERS.add(listener);
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseImageEvent.Type type,
            ImageFile               imageFile) {

        DatabaseImageEvent event = new DatabaseImageEvent(type);

        event.setImageFile(imageFile);
        for (DatabaseListener listener : DATABASE_LISTENERS) {
            listener.actionPerformed(event);
        }
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseImageEvent.Type  type,
            ImageFile                oldImageFile,
            ImageFile                newImageFile) {

        DatabaseImageEvent event = new DatabaseImageEvent(type);

        event.setImageFile(newImageFile);
        event.setOldImageFile(oldImageFile);
        for (DatabaseListener listener : DATABASE_LISTENERS) {
            listener.actionPerformed(event);
        }
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseProgramEvent.Type type,
            Program                   program) {

        DatabaseProgramEvent event = new DatabaseProgramEvent(type);

        event.setProgram(program);
        for (DatabaseListener listener : DATABASE_LISTENERS) {
            listener.actionPerformed(event);
        }
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseImageCollectionEvent.Type type,
            String                            collectionName,
            Collection<String>                filenames) {

        DatabaseImageCollectionEvent evt = new DatabaseImageCollectionEvent(
                                               type, collectionName, filenames);

        for (DatabaseListener listener : DATABASE_LISTENERS) {
            listener.actionPerformed(evt);
        }
    }

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
            } catch (SQLException ex) {
                AppLog.logSevere(Database.class, ex);
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
        } catch (SQLException ex) {
            AppLog.logSevere(Database.class, ex);
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
        AppLog.logFiner(getClass(), AppLog.USE_STRING, sql);
    }

    protected void logFiner(PreparedStatement stmt) {
        AppLog.logFiner(getClass(), AppLog.USE_STRING, stmt.toString());
    }

    protected void logFinest(String sql) {
        AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
    }

    protected void logFinest(PreparedStatement stmt) {
        AppLog.logFinest(getClass(), AppLog.USE_STRING, stmt.toString());
    }

    protected Database() {
    }
}
