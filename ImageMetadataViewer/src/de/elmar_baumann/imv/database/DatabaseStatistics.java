package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.selections.AllTables;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public class DatabaseStatistics extends Database {
    
    private static DatabaseStatistics instance = new DatabaseStatistics();
    
    public static DatabaseStatistics getInstance() {
        return instance;
    }
    
    private DatabaseStatistics() {
    }

    /**
     * Liefert die Anzahl der Datensätze für verschiedene Spaltenwerte.
     *
     * @param  column  Spalte
     * @return Anzahl oder -1 bei Fehlern
     */
    public int getDistinctCount(Column column) {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String query = "SELECT COUNT(*) FROM (SELECT DISTINCT " + // NOI18N
                column.getName() + // NOI18N
                " FROM " + // NOI18N
                column.getTable().getName() +
                ")"; // NOI18N
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Dateien in der Datenbank.
     *
     * @return Dateianzahl oder -1 bei Fehlern
     */
    public int getFileCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM files"); // NOI18N
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl aller Datensätze in allen Tabellen.
     *
     * @return Anzahl oder -1 bei Fehlern
     */
    public long getTotalRecordCount() {
        long count = -1;
        Connection connection = null;
        List<Table> tables = AllTables.get();
        try {
            connection = getConnection();
            for (Table table : tables) {
                Statement stmt = connection.createStatement();
                String query = "SELECT COUNT(*) FROM " + table.getName(); // NOI18N
                ResultSet rs = stmt.executeQuery(query);
                if (rs.next()) {
                    count += rs.getInt(1);
                }
                stmt.close();
            }
        } catch (SQLException ex) {
            count = -1;
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert die Anzahl der Thumbnails in der Datenbank.
     *
     * @return Thumbnailanzahl oder -1 bei Fehlern
     */
    public int getThumbnailCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM files WHERE thumbnail IS NOT NULL"); // NOI18N
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count;
    }
}
