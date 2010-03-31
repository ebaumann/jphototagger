/*
 * @(#)DatabaseSavedSearches.java    Created on 2008-10-21
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.data.SavedSearchPanel;

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
 * @author  Elmar Baumann
 */
public final class DatabaseSavedSearches extends Database {
    public static final DatabaseSavedSearches INSTANCE =
        new DatabaseSavedSearches();

    private DatabaseSavedSearches() {}

    private String getInsertSql() {
        return "INSERT INTO saved_searches (name"    // -- 1 --
               + ", sql_string"                      // -- 2 --
               + ", search_type"                     // -- 3 --
               + ") VALUES (?, ?, ?)";
    }

    public boolean insert(SavedSearch savedSearch) {
        boolean           inserted = false;
        Connection        con      = null;
        PreparedStatement stmt     = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getInsertSql());
            stmt.setString(1, savedSearch.getName());

            String customSql = savedSearch.getCustomSql();

            if (customSql != null) {
                stmt.setBytes(2, customSql.getBytes());
            }

            setSearchType(stmt, 3, savedSearch);
            logFiner(stmt);
            stmt.executeUpdate();

            long                   id       = findId(con,
                                                  savedSearch.getName());
            List<SavedSearchPanel> panels   = savedSearch.getPanels();
            List<String>           keywords = savedSearch.getKeywords();

            insertSavedSearchPanels(con, id, panels);
            insertSavedSearchKeywords(con, id, keywords);
            con.commit();
            inserted = true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return inserted;
    }

    public boolean insertOrUpdate(SavedSearch savedSearch) {
        if (exists(savedSearch)) {
            return update(savedSearch);
        } else {
            return insert(savedSearch);
        }
    }

    private void setSearchType(PreparedStatement stmt, int parameterIndex,
                               SavedSearch search)
            throws SQLException {
        if (search == null) {
            return;
        }

        SavedSearch.Type type = (search.getType() == null)
                                ? SavedSearch.Type.KEYWORDS_AND_PANELS
                                : search.getType();

        stmt.setShort(parameterIndex, type.getValue());
    }

    private String getInsertSavedSearchPanelsSql() {
        return "INSERT INTO saved_searches_panels (id_saved_search"    // 1
               + ", panel_index"                                       // 2
               + ", bracket_left_1"                                    // 3
               + ", operator_id"                                       // 4
               + ", bracket_left_2"                                    // 5
               + ", column_id"                                         // 6
               + ", comparator_id"                                     // 7
               + ", value"                                             // 8
               + ", bracket_right)"                                    // 9
               + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    private void insertSavedSearchPanels(Connection con, long idSavedSearch,
            List<SavedSearchPanel> panels)
            throws SQLException {
        if ((idSavedSearch > 0) && (panels != null)) {
            PreparedStatement stmt = null;

            try {
                stmt = con.prepareStatement(getInsertSavedSearchPanelsSql());
                stmt.setLong(1, idSavedSearch);

                for (SavedSearchPanel panel : panels) {
                    stmt.setInt(2, panel.getPanelIndex());
                    stmt.setBoolean(3, panel.isBracketLeft1Selected());
                    stmt.setInt(4, panel.getOperatorId());
                    stmt.setBoolean(5, panel.isBracketLeft2Selected());
                    stmt.setInt(6, panel.getColumnId());
                    stmt.setInt(7, panel.getComparatorId());
                    stmt.setString(8, panel.getValue());
                    stmt.setBoolean(9, panel.isBracketRightSelected());
                    logFiner(stmt);
                    stmt.executeUpdate();
                }
            } finally {
                close(stmt);
            }
        }
    }

    private String getInsertSavedSearchKeywordsSql() {
        return "INSERT INTO saved_searches_keywords "
               + "(id_saved_search, keyword)" + " VALUES (?, ?)";
    }

    private void insertSavedSearchKeywords(Connection con, long idSavedSearch,
            List<String> keywords)
            throws SQLException {
        if ((idSavedSearch > 0) && (keywords != null)) {
            PreparedStatement stmt = null;

            try {
                stmt = con.prepareStatement(getInsertSavedSearchKeywordsSql());
                stmt.setLong(1, idSavedSearch);

                for (String keyword : keywords) {
                    stmt.setString(2, keyword);
                    logFiner(stmt);
                    stmt.executeUpdate();
                }
            } finally {
                close(stmt);
            }
        }
    }

    private long findId(Connection con, String name) throws SQLException {
        long              id   = -1;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(
                "SELECT id FROM saved_searches WHERE name = ?");
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }

        return id;
    }

    /**
     * Liefert die Anzahl gespeicherter Suchen.
     *
     * @return Anzahl oder -1 bei Fehlern.
     */
    public int getCount() {
        int        count = -1;
        Connection con   = null;
        Statement  stmt  = null;
        ResultSet  rs    = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT COUNT(*) FROM saved_searches";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
    }

    /**
     * Liefert, ob eine gespeicherte Suche existiert.
     *
     * @param  name Name der gespeicherten Suche
     * @return true, wenn die gespeicherte Suche existiert
     * @see    #exists(org.jphototagger.program.data.SavedSearch)
     */
    public boolean exists(String name) {
        Connection con = null;

        try {
            con = getConnection();

            long id = findId(con, name);

            return id > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            free(con);
        }

        return false;
    }

    public boolean exists(SavedSearch savedSearch) {
        return exists(savedSearch.getName());
    }

    public boolean delete(String name) {
        boolean           deleted = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM saved_searches WHERE name = ?");
            stmt.setString(1, name);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            con.commit();
            deleted = count > 0;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    public boolean updateRename(String fromName, String toName) {
        boolean           renamed = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE saved_searches SET name = ? WHERE name = ?");
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            logFiner(stmt);

            int count = stmt.executeUpdate();

            renamed = count > 0;

            if (renamed) {
                List<String> info = new ArrayList<String>();

                info.add(fromName);
                info.add(toName);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return renamed;
    }

    public boolean update(SavedSearch savedSearch) {
        delete(savedSearch.getName());

        return insert(savedSearch);
    }

    private String getFindSql() {
        return "SELECT name, sql_string, search_type"
               + " FROM saved_searches WHERE name = ?";
    }

    public SavedSearch find(String name) {
        SavedSearch       savedSearch = null;
        Connection        con         = null;
        PreparedStatement stmt        = null;
        ResultSet         rs          = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(getFindSql());
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                savedSearch = new SavedSearch();
                savedSearch.setName(rs.getString(1));
                savedSearch.setCustomSql(new String(rs.getBytes(2)));
                setSearchType(rs, 3, savedSearch);
                setSavedSearchPanels(con, savedSearch);
                setSavedSearchKeywords(con, savedSearch);
            }
        } catch (Exception ex) {
            savedSearch = null;
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return savedSearch;
    }

    private void setSearchType(ResultSet rs, int columnIndex,
                               SavedSearch search)
            throws SQLException {
        short            value = rs.getShort(columnIndex);
        SavedSearch.Type type  = rs.wasNull()
                                 ? SavedSearch.Type.KEYWORDS_AND_PANELS
                                 : SavedSearch.Type.fromValue(value);

        search.setType(type);
    }

    private String getGetAllSql() {
        return "SELECT name, sql_string, search_type"
               + " FROM saved_searches ORDER BY name";
    }

    /**
     * Liefert alle gespeicherten Suchen.
     *
     * @return Gespeicherte Suchen
     */
    public List<SavedSearch> getAll() {
        List<SavedSearch> searches = new ArrayList<SavedSearch>();
        Connection        con      = null;
        Statement         stmt     = null;
        ResultSet         rs       = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = getGetAllSql();

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                SavedSearch savedSearch = new SavedSearch();

                savedSearch.setName(rs.getString(1));
                savedSearch.setCustomSql(new String(rs.getBytes(2)));
                setSearchType(rs, 3, savedSearch);
                setSavedSearchPanels(con, savedSearch);
                setSavedSearchKeywords(con, savedSearch);
                searches.add(savedSearch);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseSavedSearches.class, ex);
            searches.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return searches;
    }

    public void tagSearchesIfStmtContains(String what, String tag) {
        for (SavedSearch search : getAll()) {
            ParamStatement stmt = search.getParamStatement();

            if ((stmt != null) && stmt.getSql().contains(what)) {
                String name = search.getName();

                if (!name.startsWith(tag) &&!name.endsWith(tag)) {
                    delete(name);
                    search.setName(tag + name + tag);
                    insertOrUpdate(search);
                }
            }
        }
    }

    private String getSetSavedSearchPanelsSql() {
        return "SELECT saved_searches_panels.panel_index"    // -- 1 --
               + ", saved_searches_panels.bracket_left_1"    // -- 2 --
               + ", saved_searches_panels.operator_id"       // -- 3 --
               + ", saved_searches_panels.bracket_left_2"    // -- 4 --
               + ", saved_searches_panels.column_id"         // -- 5 --
               + ", saved_searches_panels.comparator_id"     // -- 6 --
               + ", saved_searches_panels.value"             // -- 7 --
               + ", saved_searches_panels.bracket_right"     // -- 8 --
               + " FROM saved_searches_panels INNER JOIN saved_searches"
               + " ON saved_searches_panels.id_saved_search"
               + " = saved_searches.id AND saved_searches.name = ?"
               + " ORDER BY saved_searches_panels.panel_index ASC";
    }

    private void setSavedSearchPanels(Connection con, SavedSearch savedSearch)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(getSetSavedSearchPanelsSql());
            stmt.setString(1, savedSearch.getName());
            logFinest(stmt);
            rs = stmt.executeQuery();

            List<SavedSearchPanel> panels = new ArrayList<SavedSearchPanel>();

            while (rs.next()) {
                SavedSearchPanel panel = new SavedSearchPanel();

                panel.setPanelIndex(rs.getInt(1));
                panel.setBracketLeft1Selected(rs.getBoolean(2));
                panel.setOperatorId(rs.getInt(3));
                panel.setBracketLeft2Selected(rs.getBoolean(4));
                panel.setColumnId(rs.getInt(5));
                panel.setComparatorId(rs.getInt(6));
                panel.setValue(rs.getString(7));
                panel.setBracketRightSelected(rs.getBoolean(8));
                panels.add(panel);
            }

            savedSearch.setPanels(panels);
        } finally {
            close(rs, stmt);
        }
    }

    private String getSetSavedSearchKeywordsSql() {
        return "SELECT keyword"    // -- 1 --
               + " FROM saved_searches_keywords INNER JOIN saved_searches"
               + " ON saved_searches_keywords.id_saved_search"
               + " = saved_searches.id AND saved_searches.name = ?"
               + " ORDER BY keyword ASC";
    }

    private void setSavedSearchKeywords(Connection con, SavedSearch savedSearch)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            stmt = con.prepareStatement(getSetSavedSearchKeywordsSql());
            stmt.setString(1, savedSearch.getName());
            logFinest(stmt);
            rs = stmt.executeQuery();

            List<String> keywords = new ArrayList<String>();

            while (rs.next()) {
                keywords.add(rs.getString(1));
            }

            savedSearch.setKeywords(keywords);
        } finally {
            close(rs, stmt);
        }
    }
}
