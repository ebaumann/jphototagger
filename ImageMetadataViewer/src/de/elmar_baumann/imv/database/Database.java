package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ErrorEvent;
import de.elmar_baumann.imv.event.listener.ErrorListeners;
import de.elmar_baumann.imv.event.ProgressEvent;
import de.elmar_baumann.imv.event.ProgressListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class of specialized database classes.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class Database {

    private static List<DatabaseListener> databaseListener = new ArrayList<DatabaseListener>();

    public void addDatabaseListener(DatabaseListener listener) {
        databaseListener.add(listener);
    }

    protected void notifyDatabaseListener(
        DatabaseAction.Type type) {
        
        DatabaseAction action = new DatabaseAction(type);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected void notifyDatabaseListener(
        DatabaseAction.Type type, ImageFile imageFileData) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setImageFileData(imageFileData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected void notifyDatabaseListener(
        DatabaseAction.Type type, SavedSearch savedSerachData) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setSavedSerachData(savedSerachData);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected void notifyDatabaseListener(
        DatabaseAction.Type type, String filename) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setFilename(filename);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected void notifyDatabaseListener(
        DatabaseAction.Type type, List<String> filenames) {
        
        DatabaseAction action = new DatabaseAction(type);
        action.setFilenames(filenames);
        for (DatabaseListener listener : databaseListener) {
            listener.actionPerformed(action);
        }
    }

    protected void notifyDatabaseListener(
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
     */
    protected Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    /**
     * Frees a connection in the Connection Pool so it can be reused at a later time.
     * @param The connection to be freed.
     */
    protected void free(Connection connection) {
        if (connection != null) {
            try {
                ConnectionPool.getInstance().free(connection);
            } catch (SQLException ex) {
                handleException(ex, Level.SEVERE);
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
            handleException(ex, Level.SEVERE);
        }
    }

    /**
     * Method for exception logging.
     * 
     * @param ex The exception to log.
     * @param logLevel The log level.
     */
    protected void handleException(Exception ex, Level logLevel) {
        Logger.getLogger(Database.class.getName()).log(logLevel, null, ex);
        ErrorListeners.getInstance().notifyErrorListener(
            new ErrorEvent(ex.toString(), this));
    }

    protected void logStatement(PreparedStatement stmt, Level level) {
        Logger.getLogger(Database.class.getName()).log(level, stmt.toString());
    }

    protected boolean notifyProgressListenerStart(
        ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressStarted(event);
            return event.isStop();
        }
        return false;
    }

    protected boolean notifyProgressListenerPerformed(
        ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressPerformed(event);
            return event.isStop();
        }
        return false;
    }

    protected void notifyProgressListenerEnd(
        ProgressListener listener, ProgressEvent event) {
        
        if (listener != null) {
            listener.progressEnded(event);
        }
    }

    protected Database() {
    }
}
