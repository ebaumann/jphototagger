package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.SavedSearch;
import de.elmar_baumann.imv.data.SavedSearchPanel;
import de.elmar_baumann.imv.data.SavedSearchParamStatement;
import de.elmar_baumann.imv.event.DatabaseAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/21
 */
public final class DatabaseSavedSearches extends Database {

    public static final DatabaseSavedSearches INSTANCE = new DatabaseSavedSearches();

    private DatabaseSavedSearches() {
    }

    /**
     * Fügt eine gespeicherte Suche ein. Existiert die Suche, wird
     * {@link #updateSavedSearch(de.elmar_baumann.imv.data.SavedSearch)}
     * aufgerufen.
     *
     * @param  data Suche
     * @return true bei Erfolg
     */
    public synchronized boolean insertSavedSearch(SavedSearch data) {
        boolean inserted = false;
        SavedSearchParamStatement stmtData = data.getParamStatements();
        List<SavedSearchPanel> panelData = data.getPanels();
        if (stmtData != null && !stmtData.getName().isEmpty()) {
            if (existsSavedSearch(data)) {
                return updateSavedSearch(data);
            }
            Connection connection = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO saved_searches" + // NOI18N
                    " (name" + // NOI18N -- 1 --
                    ", sql_string" + // NOI18N -- 2 --
                    ", is_query)" + // NOI18N -- 3 --
                    " VALUES (?, ?, ?)"); // NOI18N
                stmt.setString(1, stmtData.getName());
                stmt.setBytes(2, stmtData.getSql().getBytes());
                stmt.setBoolean(3, stmtData.isQuery());
                AppLog.logFiner(DatabaseSavedSearches.class, stmt.toString());
                stmt.executeUpdate();
                long id = getIdSavedSearch(connection, stmtData.getName());
                insertSavedSearchValues(connection, id, stmtData.getValues());
                insertSavedSearchPanelData(connection, id, panelData);
                connection.commit();
                inserted = true;
                notifyDatabaseListener(DatabaseAction.Type.SAVED_SEARCH_INSERTED, data);
                stmt.close();
            } catch (SQLException ex) {
                AppLog.logWarning(getClass(), ex);
                rollback(connection);
            } finally {
                free(connection);
            }
        }

        return inserted;
    }

    private synchronized void insertSavedSearchValues(
        Connection connection, long idSavedSearch, List<String> values) throws SQLException {
        if (idSavedSearch > 0 && values.size() > 0) {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO saved_searches_values (" + // NOI18N
                "id_saved_searches" + // NOI18N
                ", value" + // NOI18N
                ", value_index" + // NOI18N
                ")" + // NOI18N
                " VALUES (?, ?, ?)"); // NOI18N
            stmt.setLong(1, idSavedSearch);
            int size = values.size();
            for (int index = 0; index < size; index++) {
                String value = values.get(index);
                stmt.setString(2, value);
                stmt.setInt(3, index);
                AppLog.logFiner(DatabaseSavedSearches.class, stmt.toString());
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    private synchronized void insertSavedSearchPanelData(
        Connection connection, long idSavedSearch, List<SavedSearchPanel> panelData) throws SQLException {
        if (idSavedSearch > 0 && panelData != null) {
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO" + // NOI18N
                " saved_searches_panels (" + // NOI18N
                "id_saved_searches" + // NOI18N -- 1 --
                ", panel_index" + // NOI18N -- 2 --
                ", bracket_left_1" + // NOI18N -- 3 --
                ", operator_id" + // NOI18N -- 4 --
                ", bracket_left_2" + // NOI18N -- 5 --
                ", column_id" + // NOI18N -- 6 --
                ", comparator_id" + // NOI18N -- 7 --
                ", value" + // NOI18N -- 8 --
                ", bracket_right)" + // NOI18N -- 9 --
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N
            stmt.setLong(1, idSavedSearch);
            for (SavedSearchPanel data : panelData) {
                stmt.setInt(2, data.getPanelIndex());
                stmt.setBoolean(3, data.isBracketLeft1Selected());
                stmt.setInt(4, data.getOperatorId());
                stmt.setBoolean(5, data.isBracketLeft2Selected());
                stmt.setInt(6, data.getColumnId());
                stmt.setInt(7, data.getComparatorId());
                stmt.setString(8, data.getValue());
                stmt.setBoolean(9, data.isBracketRightSelected());
                AppLog.logFiner(DatabaseSavedSearches.class, stmt.toString());
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    private long getIdSavedSearch(Connection connection, String name) throws SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT id FROM saved_searches WHERE name = ?"); // NOI18N
        stmt.setString(1, name);
        AppLog.logFinest(DatabaseSavedSearches.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getLong(1);
        }
        stmt.close();
        return id;
    }

    /**
     * Liefert die Anzahl gespeicherter Suchen.
     *
     * @return Anzahl oder -1 bei Fehlern.
     */
    public int getSavedSearchesCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM saved_searches"); // NOI18N
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(getClass(), ex);
        } finally {
            free(connection);
        }
        return count;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     *
     * @param  name Name der gespeicherten Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #existsSavedSearch(de.elmar_baumann.imv.data.SavedSearch)
     */
    public boolean existsSavedSearch(String name) {
        Connection connection = null;
        try {
            connection = getConnection();
            long id = getIdSavedSearch(connection, name);
            return id > 0;
        } catch (SQLException ex) {
            AppLog.logWarning(getClass(), ex);
        } finally {
            free(connection);
        }
        return false;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     *
     * @param  search search Gespeicherte Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #existsSavedSearch(java.lang.String)
     */
    public boolean existsSavedSearch(SavedSearch search) {
        return existsSavedSearch(search.getName());
    }

    /**
     * Löscht eine gespeicherte Suche.
     *
     * @param  name Name der Suche
     * @return true bei Erfolg
     */
    public synchronized boolean deleteSavedSearch(String name) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM saved_searches WHERE name = ?"); // NOI18N
            stmt.setString(1, name);
            AppLog.logFiner(DatabaseSavedSearches.class, stmt.toString());
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            if (deleted) {
                notifyDatabaseListener(DatabaseAction.Type.SAVED_SEARCH_DELETED, name);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(getClass(), ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return deleted;
    }

    /**
     * Benennt eine gespeicherte Suche um.
     *
     * @param  oldName Alter Name
     * @param  newName Neuer Name
     * @return true bei Erfolg
     */
    public synchronized boolean updateRenameSavedSearch(String oldName, String newName) {
        boolean renamed = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE saved_searches SET name = ? WHERE name = ?"); // NOI18N
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            AppLog.logFiner(DatabaseSavedSearches.class, stmt.toString());
            int count = stmt.executeUpdate();
            renamed = count > 0;
            if (renamed) {
                List<String> info = new ArrayList<String>();
                info.add(oldName);
                info.add(newName);
                notifyDatabaseListener(DatabaseAction.Type.SAVED_SEARCH_UPDATED, info);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(getClass(), ex);
        } finally {
            free(connection);
        }
        return renamed;
    }

    /**
     * Aktualisiert eine gespeicherte Suche.
     *
     * @param  data Suche
     * @return true bei Erfolg
     */
    public synchronized boolean updateSavedSearch(SavedSearch data) {
        if (data.hasParamStatement() && data.getParamStatements() != null) {
            boolean updated = 
                deleteSavedSearch(data.getParamStatements().getName()) && 
                insertSavedSearch(data);
            if (updated) {
                notifyDatabaseListener(DatabaseAction.Type.SAVED_SEARCH_UPDATED, data);
            }
            return updated;
        }
        return false;
    }

    /**
     * Liefert eine gespeicherte Suche mit bestimmtem Namen.
     *
     * @param  name Name
     * @return Suche oder null, wenn die Suche nicht erzeugt wurde
     */
    public SavedSearch getSavedSearch(String name) {
        SavedSearch data = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT" + // NOI18N
                " name" + // NOI18N -- 1 --
                ", sql_string" + // NOI18N -- 2 --
                ", is_query" + // NOI18N -- 3 --
                " FROM saved_searches WHERE name = ?"); // NOI18N
            stmt.setString(1, name);
            AppLog.logFinest(DatabaseSavedSearches.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                data = new SavedSearch();
                SavedSearchParamStatement paramStatementData = new SavedSearchParamStatement();
                paramStatementData.setName(rs.getString(1));
                paramStatementData.setSql(new String(rs.getBytes(2)));
                paramStatementData.setQuery(rs.getBoolean(3));
                data.setParamStatements(paramStatementData);
                setSavedSearchValues(connection, data);
                setSavedSearchPanels(connection, data);
            }
            stmt.close();
        } catch (SQLException ex) {
            data = null;
            AppLog.logWarning(getClass(), ex);
        } finally {
            free(connection);
        }
        return data;
    }

    /**
     * Liefert alle gespeicherten Suchen.
     *
     * @return Gespeicherte Suchen
     */
    public List<SavedSearch> getSavedSearches() {
        List<SavedSearch> allData = new ArrayList<SavedSearch>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT" + // NOI18N
                " name" + // NOI18N -- 1 --
                ", sql_string" + // NOI18N -- 2 --
                ", is_query" + // NOI18N -- 3 --
                " FROM saved_searches ORDER BY name"); // NOI18N
            while (rs.next()) {
                SavedSearch data = new SavedSearch();
                SavedSearchParamStatement paramStatementData = new SavedSearchParamStatement();
                paramStatementData.setName(rs.getString(1));
                paramStatementData.setSql(new String(rs.getBytes(2)));
                paramStatementData.setQuery(rs.getBoolean(3));
                data.setParamStatements(paramStatementData);
                setSavedSearchValues(connection, data);
                setSavedSearchPanels(connection, data);
                allData.add(data);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(getClass(), ex);
            allData.clear();
        } finally {
            free(connection);
        }
        return allData;
    }

    private void setSavedSearchValues(Connection connection, SavedSearch data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
            "SELECT" + // NOI18N
            " saved_searches_values.value" + // NOI18N
            " FROM" + // NOI18N
            " saved_searches_values INNER JOIN saved_searches" + // NOI18N
            " ON saved_searches_values.id_saved_searches = saved_searches.id" + // NOI18N
            " AND saved_searches.name = ?" + // NOI18N
            " ORDER BY saved_searches_values.value_index ASC"); // NOI18N
        stmt.setString(1, data.getParamStatements().getName());
        AppLog.logFinest(DatabaseSavedSearches.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        List<String> values = new ArrayList<String>();
        while (rs.next()) {
            values.add(rs.getString(1));
        }
        stmt.close();
        if (values.size() > 0) {
            data.getParamStatements().setValues(values);
        }
    }

    private void setSavedSearchPanels(Connection connection, SavedSearch data) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("SELECT" + // NOI18N
            " saved_searches_panels.panel_index" + // NOI18N -- 1 --
            ", saved_searches_panels.bracket_left_1" + // NOI18N -- 2 --
            ", saved_searches_panels.operator_id" + // NOI18N -- 3 --
            ", saved_searches_panels.bracket_left_2" + // NOI18N -- 4 --
            ", saved_searches_panels.column_id" + // NOI18N -- 5 --
            ", saved_searches_panels.comparator_id" + // NOI18N -- 6 --
            ", saved_searches_panels.value" + // NOI18N -- 7 --
            ", saved_searches_panels.bracket_right" + // NOI18N -- 8 --
            " FROM" + // NOI18N
            " saved_searches_panels INNER JOIN saved_searches" + // NOI18N
            " ON saved_searches_panels.id_saved_searches = saved_searches.id" + // NOI18N
            " AND saved_searches.name = ?" + // NOI18N
            " ORDER BY saved_searches_panels.panel_index ASC"); // NOI18N
        stmt.setString(1, data.getParamStatements().getName());
        AppLog.logFinest(DatabaseSavedSearches.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        List<SavedSearchPanel> allPanelData = new ArrayList<SavedSearchPanel>();
        while (rs.next()) {
            SavedSearchPanel panelData = new SavedSearchPanel();
            panelData.setPanelIndex(rs.getInt(1));
            panelData.setBracketLeft1Selected(rs.getBoolean(2));
            panelData.setOperatorId(rs.getInt(3));
            panelData.setBracketLeft2Selected(rs.getBoolean(4));
            panelData.setColumnId(rs.getInt(5));
            panelData.setComparatorId(rs.getInt(6));
            panelData.setValue(rs.getString(7));
            panelData.setBracketRightSelected(rs.getBoolean(8));
            allPanelData.add(panelData);
        }
        stmt.close();
        if (allPanelData.size() > 0) {
            data.setPanels(allPanelData);
        }
    }
}
