package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class of specialized database classes.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    private static final List<DatabaseListener> databaseListener =
            new ArrayList<DatabaseListener>();

    public synchronized void addDatabaseListener(DatabaseListener listener) {
        databaseListener.add(listener);
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseImageEvent.Type type, ImageFile imageFile) {

        DatabaseImageEvent event = new DatabaseImageEvent(type);
        event.setImageFile(imageFile);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(event);
        }
    }

    protected synchronized void notifyDatabaseListener(
            DatabaseProgramEvent.Type type, Program program) {

        DatabaseProgramEvent event = new DatabaseProgramEvent(type);
        event.setProgram(program);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(event);
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
            AppLog.logWarning(Database.class, ex);
        }
    }

    protected boolean notifyProgressListenerStart(ProgressListener listener,
            ProgressEvent event) {

        if (listener != null) {
            listener.progressStarted(event);
            return event.isStop();
        }
        return false;
    }

    protected boolean notifyProgressListenerPerformed(ProgressListener listener,
            ProgressEvent event) {

        if (listener != null) {
            listener.progressPerformed(event);
            return event.isStop();
        }
        return false;
    }

    protected void notifyProgressListenerEnd(ProgressListener listener,
            ProgressEvent event) {

        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    protected Database() {
    }
}
