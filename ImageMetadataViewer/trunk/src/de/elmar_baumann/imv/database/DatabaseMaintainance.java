package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.event.DatabaseAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseMaintainance extends Database {
    
    private static final DatabaseMaintainance instance = new DatabaseMaintainance();
    
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
            notifyDatabaseListener(DatabaseAction.Type.MAINTAINANCE_DATABASE_COMPRESSED);
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseMaintainance.class, ex);
        } finally {
            free(connection);
        }
        return success;
    }

}
