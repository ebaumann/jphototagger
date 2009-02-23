package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.database.metadata.Column;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseContent extends Database {
    
    public static final DatabaseContent INSTANCE = new DatabaseContent();
    
    private DatabaseContent() {
    }

    /**
     * Liefert den Inhalt einer ganzen Tabellenspalte.
     * 
     * @param column Tabellenspalte
     * @return Werte DISTINCT
     */
    public Set<String> getContent(Column column) {
        Set<String> content = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String columnName = column.getName();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT DISTINCT " + // NOI18N
                columnName +
                " FROM " + // NOI18N
                column.getTable().getName() +
                " WHERE " + // NOI18N
                columnName +
                " IS NOT NULL"); // NOI18N

            while (resultSet.next()) {
                content.add(resultSet.getString(1));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
            content.clear();
        } finally {
            free(connection);
        }
        return content;
    }

    /**
     * Liefert den Inhalt von Tabellenspalten.
     * 
     * @param columns Tabellenspalten
     * @return Werte DISTINCT
     */
    public Set<String> getContent(Set<Column> columns) {
        Set<String> content = new LinkedHashSet<String>();
        for (Column column : columns) {
            content.addAll(getContent(column));
        }
        return content;
    }

}
