package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
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

    private static final List<DatabaseListener> databaseListener = new ArrayList<DatabaseListener>();

    public synchronized void addDatabaseListener(DatabaseListener listener) {
        databaseListener.add(listener);
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type) {
        
        DatabaseAction action = new DatabaseAction(type);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type, ImageFile imageFileData) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setImageFileData(imageFileData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type, SavedSearch savedSerachData) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setSavedSerachData(savedSerachData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type, String filename) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setFilename(filename);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type, List<String> filenames) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setFilenames(filenames);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected synchronized void notifyDatabaseListener(
        DatabaseAction.Type type, String filename, List<String> filenames) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setFilename(filename);
        action.setFilenames(filenames);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
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

    protected boolean notifyProgressListenerStart(ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressStarted(event);
            return event.isStop();
        }
        return false;
    }

    protected boolean notifyProgressListenerPerformed(ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressPerformed(event);
            return event.isStop();
        }
        return false;
    }

    protected void notifyProgressListenerEnd(ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    protected Database() {
    }
}
