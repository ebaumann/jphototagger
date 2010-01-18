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
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.data.SavedSearchPanel;
import de.elmar_baumann.jpt.data.SavedSearchParamStatement;
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
 * @version 2008-10-21
 */
public final class DatabaseSavedSearches extends Database {

    public static final DatabaseSavedSearches INSTANCE = new DatabaseSavedSearches();

    private DatabaseSavedSearches() {
    }

    /**
     * Fügt eine gespeicherte Suche ein. Existiert die Suche, wird
     * {@link #update(de.elmar_baumann.jpt.data.SavedSearch)}
     * aufgerufen.
     *
     * @param   savedSearch Suche
     * @return              true bei Erfolg
     */
    public boolean insertOrUpdate(SavedSearch savedSearch) {
        boolean inserted = false;
        SavedSearchParamStatement paramStmt = savedSearch.getParamStatement();
        List<SavedSearchPanel> panels = savedSearch.getPanels();
        if (paramStmt != null && !paramStmt.getName().isEmpty()) {
            if (exists(savedSearch)) {
                return update(savedSearch);
            }
            Connection connection = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(false);
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO saved_searches" +
                        " (name" +       // -- 1 --
                        ", sql_string" + // -- 2 --
                        ", is_query)" +  // -- 3 --
                        " VALUES (?, ?, ?)");
                stmt.setString( 1, paramStmt.getName());
                stmt.setBytes(  2, paramStmt.getSql().getBytes());
                stmt.setBoolean(3, paramStmt.isQuery());
                logFiner(stmt);
                stmt.executeUpdate();
                long id = findId(connection, paramStmt.getName());
                insertSavedSearchValues(connection, id, paramStmt.getValues());
                insertSavedSearchPanels(connection, id, panels);
                connection.commit();
                inserted = true;
                stmt.close();
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseSavedSearches.class, ex);
                rollback(connection);
            } finally {
                free(connection);
            }
        }

        return inserted;
    }

    private void insertSavedSearchValues(
            Connection connection, long idSavedSearch, List<String> values)
            throws SQLException {

        if (idSavedSearch > 0 && values != null && values.size() > 0) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO saved_searches_values (" +
                    "id_saved_searches" +
                    ", value" +
                    ", value_index" +
                    ")" +
                    " VALUES (?, ?, ?)");
            stmt.setLong(1, idSavedSearch);
            int size = values.size();
            for (int index = 0; index < size; index++) {
                String value = values.get(index);
                stmt.setString(2, value);
                stmt.setInt(3, index);
                logFiner(stmt);
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    private void insertSavedSearchPanels(
            Connection connection,
            long idSavedSearch,
            List<SavedSearchPanel> panels)
            throws SQLException {

        if (idSavedSearch > 0 && panels != null) {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO" +
                    " saved_searches_panels (" +
                    "id_saved_searches" + // -- 1 --
                    ", panel_index" +     // -- 2 --
                    ", bracket_left_1" +  // -- 3 --
                    ", operator_id" +     // -- 4 --
                    ", bracket_left_2" +  // -- 5 --
                    ", column_id" +       // -- 6 --
                    ", comparator_id" +   // -- 7 --
                    ", value" +           // -- 8 --
                    ", bracket_right)" +  // -- 9 --
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt.setLong(       1, idSavedSearch);
            for (SavedSearchPanel panel : panels) {
                stmt.setInt(    2, panel.getPanelIndex());
                stmt.setBoolean(3, panel.isBracketLeft1Selected());
                stmt.setInt(    4, panel.getOperatorId());
                stmt.setBoolean(5, panel.isBracketLeft2Selected());
                stmt.setInt(    6, panel.getColumnId());
                stmt.setInt(    7, panel.getComparatorId());
                stmt.setString( 8, panel.getValue());
                stmt.setBoolean(9, panel.isBracketRightSelected());
                logFiner(stmt);
                stmt.executeUpdate();
            }
            stmt.close();
        }
    }

    private long findId(Connection connection, String name) throws
            SQLException {
        long id = -1;
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM saved_searches WHERE name = ?");
        stmt.setString(1, name);
        logFinest(stmt);
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
    public int getCount() {
        int count = -1;
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT COUNT(*) FROM saved_searches";
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
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
     * @see    #exists(de.elmar_baumann.jpt.data.SavedSearch)
     */
    public boolean exists(String name) {
        Connection connection = null;
        try {
            connection = getConnection();
            long id = findId(connection, name);
            return id > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            free(connection);
        }
        return false;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     *
     * @param  savedSearch search Gespeicherte Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #exists(java.lang.String)
     */
    public boolean exists(SavedSearch savedSearch) {
        return exists(savedSearch.getName());
    }

    /**
     * Löscht eine gespeicherte Suche.
     *
     * @param  name Name der Suche
     * @return true bei Erfolg
     */
    public boolean delete(String name) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM saved_searches WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            connection.commit();
            deleted = count > 0;
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
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
    public boolean updateRename(String oldName, String newName) {
        boolean renamed = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE saved_searches SET name = ? WHERE name = ?");
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFiner(stmt);
            int count = stmt.executeUpdate();
            renamed = count > 0;
            if (renamed) {
                List<String> info = new ArrayList<String>();
                info.add(oldName);
                info.add(newName);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            free(connection);
        }
        return renamed;
    }

    /**
     * Aktualisiert eine gespeicherte Suche.
     *
     * @param  savedSearch Suche
     * @return             true bei Erfolg
     */
    public boolean update(SavedSearch savedSearch) {
        if (savedSearch.hasParamStatement()) {
            boolean updated =
                    delete(savedSearch.getParamStatement().getName()) &&
                    insertOrUpdate(savedSearch);
            return updated;
        }
        return false;
    }

    /**
     * Liefert eine gespeicherte Suche mit bestimmtem Namen.
     *
     * @param  name Name
     * @return      Suche oder null, wenn die Suche nicht erzeugt wurde
     */
    public SavedSearch find(String name) {
        SavedSearch savedSearch = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT" +
                    " name" +        // -- 1 --
                    ", sql_string" + // -- 2 --
                    ", is_query" +   // -- 3 --
                    " FROM saved_searches WHERE name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                savedSearch = new SavedSearch();
                SavedSearchParamStatement paramStmt =
                        new SavedSearchParamStatement();
                paramStmt.setName(         rs.getString(1));
                paramStmt.setSql(new String(rs.getBytes(2)));
                paramStmt.setQuery(       rs.getBoolean(3));
                savedSearch.setParamStatement(paramStmt);
                setSavedSearchValues(connection, savedSearch);
                setSavedSearchPanels(connection, savedSearch);
            }
            stmt.close();
        } catch (Exception ex) {
            savedSearch = null;
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            free(connection);
        }
        return savedSearch;
    }

    /**
     * Liefert alle gespeicherten Suchen.
     *
     * @return Gespeicherte Suchen
     */
    public List<SavedSearch> getAll() {
        List<SavedSearch> allData = new ArrayList<SavedSearch>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT" +
                    " name" +        // -- 1 --
                    ", sql_string" + // -- 2 --
                    ", is_query" +   // -- 3 --
                    " FROM saved_searches ORDER BY name";
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SavedSearch savedSearch = new SavedSearch();
                SavedSearchParamStatement paramStmt =
                        new SavedSearchParamStatement();
                paramStmt.setName(rs.getString(1));
                paramStmt.setSql(new String(rs.getBytes(2)));
                paramStmt.setQuery(rs.getBoolean(3));
                savedSearch.setParamStatement(paramStmt);
                setSavedSearchValues(connection, savedSearch);
                setSavedSearchPanels(connection, savedSearch);
                allData.add(savedSearch);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
            allData.clear();
        } finally {
            free(connection);
        }
        return allData;
    }

    private void setSavedSearchValues(
            Connection connection, SavedSearch savedSearch)
            throws SQLException {
        assert savedSearch.getParamStatement() != null :
                "Searches statement is null!";
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT" +
                " saved_searches_values.value" +
                " FROM" +
                " saved_searches_values INNER JOIN saved_searches" +
                " ON saved_searches_values.id_saved_searches = saved_searches.id" +
                " AND saved_searches.name = ?" +
                " ORDER BY saved_searches_values.value_index ASC");
        stmt.setString(1, savedSearch.getParamStatement().getName());
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        List<String> values = new ArrayList<String>();
        while (rs.next()) {
            values.add(rs.getString(1));
        }
        stmt.close();
        if (values.size() > 0) {
            SavedSearchParamStatement paramStmt =
                    savedSearch.getParamStatement();
            paramStmt.setValues(values);
            savedSearch.setParamStatement(paramStmt);
        }
    }

    private void setSavedSearchPanels(
            Connection connection, SavedSearch savedSearch)
            throws SQLException {
        assert savedSearch.getParamStatement() != null : "Statement is null!";
        PreparedStatement stmt = connection.prepareStatement("SELECT" +
                " saved_searches_panels.panel_index" +     // -- 1 --
                ", saved_searches_panels.bracket_left_1" + // -- 2 --
                ", saved_searches_panels.operator_id" +    // -- 3 --
                ", saved_searches_panels.bracket_left_2" + // -- 4 --
                ", saved_searches_panels.column_id" +      // -- 5 --
                ", saved_searches_panels.comparator_id" +  // -- 6 --
                ", saved_searches_panels.value" +          // -- 7 --
                ", saved_searches_panels.bracket_right" +  // -- 8 --
                " FROM" +
                " saved_searches_panels INNER JOIN saved_searches" +
                " ON saved_searches_panels.id_saved_searches = saved_searches.id" +
                " AND saved_searches.name = ?" +
                " ORDER BY saved_searches_panels.panel_index ASC");
        stmt.setString(1, savedSearch.getParamStatement().getName());
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();
        while (rs.next()) {
            SavedSearchPanel panel = new SavedSearchPanel();
            panel.setPanelIndex(              rs.getInt(1));
            panel.setBracketLeft1Selected(rs.getBoolean(2));
            panel.setOperatorId(              rs.getInt(3));
            panel.setBracketLeft2Selected(rs.getBoolean(4));
            panel.setColumnId(                rs.getInt(5));
            panel.setComparatorId(            rs.getInt(6));
            panel.setValue(                rs.getString(7));
            panel.setBracketRightSelected(rs.getBoolean(8));
            panels.add(panel);
        }
        stmt.close();
        if (panels.size() > 0) {
            savedSearch.setPanels(panels);
        }
    }
}
