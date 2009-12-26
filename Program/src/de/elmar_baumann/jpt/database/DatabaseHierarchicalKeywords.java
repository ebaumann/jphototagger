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
import de.elmar_baumann.jpt.data.HierarchicalKeyword;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Handling null:
// ID: Can never be null
// ID parent: Can be null, must be handle when setting and getting
// Keyword: Can never be null
// Real: Can be null but it's ok to use false when reading and writing if null
/**
 * Contains hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-10
 */
public final class DatabaseHierarchicalKeywords extends Database {

    public static final DatabaseHierarchicalKeywords INSTANCE = new DatabaseHierarchicalKeywords();

    private DatabaseHierarchicalKeywords() {
    }

    /**
     * Returns all hierarchical keywords.
     *
     * @return all hierarchical keywords
     */
    public Collection<HierarchicalKeyword> getAll() {
        List<HierarchicalKeyword> keywords    = new ArrayList<HierarchicalKeyword>();
        Connection                connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT id, id_parent, subject, real" +
                    " FROM hierarchical_subjects";
            Statement stmt = connection.createStatement();
            logFinest(sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) idParent = null;
                keywords.add(new HierarchicalKeyword(rs.getLong(1), idParent,
                        rs.getString(3), rs.getBoolean(4)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
            keywords.clear();
        } finally {
            free(connection);
        }
        return keywords;
    }

    /**
     * Updates a hierarchical keyword.
     *
     * @param  keyword keyword
     * @return         true if updated
     */
    public boolean update(HierarchicalKeyword keyword) {
        boolean updated = false;
        Connection connection = null;
        assert keyword.getId() != null;

        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE hierarchical_subjects" +
                    " SET id_parent = ?, subject = ?, real = ?" +
                    " WHERE id = ?");
            if (keyword.getIdParent() == null) {
                stmt.setNull(1, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(1, keyword.getIdParent());
            }
            stmt.setString(2, keyword.getKeyword());
            if (keyword.isReal() == null) {
                stmt.setNull(3, java.sql.Types.BOOLEAN);
            } else {
                stmt.setBoolean(3, keyword.isReal());
            }
            stmt.setLong(4, keyword.getId());
            logFiner(stmt);
            updated = stmt.executeUpdate() == 1;
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return updated;
    }

    /**
     * Inserts a hierarchical keyword. If successfully inserted the keyword
     * has a value for it's id ({@link HierarchicalKeyword#getId()}.
     *
     * <em>The keyword ({@link HierarchicalKeyword#getKeyword()}) must have
     * a not empty string!</em>
     * 
     * @param  keyword keyword
     * @return         true if updated
     */
    public boolean insert(HierarchicalKeyword keyword) {
        boolean inserted = false;
        Connection connection = null;
        assert keyword.getKeyword() != null : "Keyword is null!";
        assert !keyword.getKeyword().trim().isEmpty() : "Keyword is empty!";
        if (parentHasChild(keyword)) return false;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            String sql = "INSERT INTO hierarchical_subjects" +
                    " (id, id_parent, subject, real) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            long nextId = getNextId(connection);
            stmt.setLong(1, nextId);
            if (keyword.getIdParent() == null) {
                stmt.setNull(2, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(2, keyword.getIdParent());
            }
            stmt.setString(3, keyword.getKeyword().trim());
            if (keyword.isReal() == null) {
                stmt.setNull(4, java.sql.Types.BOOLEAN);
            } else {
                stmt.setBoolean(4, keyword.isReal());
            }
            logFiner(stmt);
            inserted = stmt.executeUpdate() == 1;
            if (inserted) {
                keyword.setId(nextId);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Deletes keywords from the database. Deletes <em>no</em> children of that
     * keyword, all childrens should be in the collection of keywords. Does only
     * commit when all childrens were deleted.
     *
     * @param  keywords keywords
     * @return          true if successfull
     */
    public boolean delete(Collection<HierarchicalKeyword> keywords) {
        boolean    deleted    = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM hierarchical_subjects WHERE id = ?");
            for (HierarchicalKeyword keyword : keywords) {
                stmt.setLong(1, keyword.getId());
                logFiner(stmt);
                stmt.executeUpdate();
            }
            connection.commit();
            deleted = true;
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return deleted;
    }

    private HierarchicalKeyword getKeyword(long id, Connection connection) throws SQLException {
        HierarchicalKeyword keyword = null;
        String sql = "SELECT id, id_parent, subject, real" +
                     " FROM hierarchical_subjects" +
                     " WHERE id = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);
        logFinest(stmt);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Long idParent = rs.getLong(2);
            if (rs.wasNull()) idParent = null;
            keyword = new HierarchicalKeyword(
                    rs.getLong(1), idParent, rs.getString(3), rs.getBoolean(4));
        }
        stmt.close();
        return keyword;
    }

    /**
     * Returns all parents of a keyword.
     *
     * @param  keyword keyword
     * @return         Parents or empty List if the keyword has no parent
     */
    public List<HierarchicalKeyword> getParents(HierarchicalKeyword keyword) {
        List<HierarchicalKeyword> parents    = new ArrayList<HierarchicalKeyword>();
        Connection                connection = null;
        try {
            connection = getConnection();
            Long idParent = keyword.getIdParent();
            while (idParent != null) {
                HierarchicalKeyword parent = getKeyword(idParent, connection);
                parents.add(parent);
                idParent = parent.getIdParent();
            }
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return parents;
    }

    /**
     * Returns all children of a parent keyword orderd ascending by the keyword.
     *
     * @param  idParent ID of the parent keyword
     * @return          children or empty collection if that parent has no
     *                  children
     */
    public Collection<HierarchicalKeyword> getChildren(long idParent) {
        Collection<HierarchicalKeyword> children =
                new ArrayList<HierarchicalKeyword>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id, id_parent, subject, real" +
                    " FROM hierarchical_subjects" +
                    " WHERE id_parent = ? ORDER BY subject ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, idParent);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long idPar = rs.getLong(2);
                if (rs.wasNull()) idPar = null;
                children.add(new HierarchicalKeyword(
                        rs.getLong(1), idPar, rs.getString(3), rs.getBoolean(4)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return children;
    }

    /**
     * Return all root keywords: keywords with no parents.
     *
     * @return keyword with no parents ordered ascending by their keyword
     */
    public Collection<HierarchicalKeyword> getRoots() {
        Collection<HierarchicalKeyword> children =
                new ArrayList<HierarchicalKeyword>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id, id_parent, subject, real" +
                    " FROM hierarchical_subjects" +
                    " WHERE id_parent IS NULL ORDER BY subject ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) idParent = null;
                children.add(new HierarchicalKeyword(rs.getLong(1), idParent,
                        rs.getString(3), rs.getBoolean(4)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return children;
    }

    private synchronized long getNextId(Connection connection)
            throws Exception {
        long id = 1;
        String sql =
                "SELECT MAX(id) FROM hierarchical_subjects";
        Statement stmt = connection.createStatement();
        logFinest(sql);
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            id = rs.getLong(1) + 1;
        }
        stmt.close();
        return id;
    }

    /**
     * Returns whether a parent has a child with a specific
     * {@link HierarchicalKeyword#getKeyword()}. The parent's ID is
     * {@link HierarchicalKeyword#getIdParent()}. The keyword's ID
     * {@link HierarchicalKeyword#getId()} will not be compared.
     *
     * @param  keyword keyword
     * @return         true if the keyword exists
     */
    public boolean parentHasChild(HierarchicalKeyword keyword) {
        boolean     exists      = false;
        boolean    parentIsRoot = keyword.getIdParent() == null;
        Connection connection   = null;

        assert keyword.getKeyword() != null;

        if (keyword.getKeyword() == null) return false;

        try {
            connection = getConnection();
            String sql = parentIsRoot
                    ? "SELECT COUNT(*) FROM hierarchical_subjects" +
                      " WHERE id_parent IS NULL AND subject = ?"
                    : "SELECT COUNT(*) FROM hierarchical_subjects" +
                      " WHERE id_parent = ? AND subject = ?";
            
            PreparedStatement stmt = connection.prepareStatement(sql);

            if (!parentIsRoot) {
                stmt.setLong(1, keyword.getIdParent());
            }
            stmt.setString(parentIsRoot ? 1 : 2, keyword.getKeyword());

            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns whether a specific root keyword exists.
     *
     * @param  keyword keyword
     * @return         true if that keyword exists
     */
    public boolean existsRootKeyword(String keyword) {
        boolean    exists     = false;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects" +
                         " WHERE  subject = ? AND id_parent IS NULL";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, keyword);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    private boolean existsKeyword(HierarchicalKeyword kw) {
        boolean    exists     = false;
        Connection connection = null;

        if (kw.getIdParent() == null) return existsRootKeyword(kw.getKeyword());

        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects" +
                         " WHERE  subject = ? AND id_parent = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, kw.getKeyword());
            stmt.setLong  (2, kw.getIdParent());
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    public boolean existsKeyword(String keyword) {
        boolean    exists     = false;
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects" +
                         " WHERE  subject = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, keyword);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    public enum Select {

        /**
         * Select all keywords
         */
        ALL_KEYWORDS,
        /**
         * Select (only) real keywords. Real keywords are keywords where
         * {@link HierarchicalKeyword#isReal()} returns true or null.
         */
        REAL_KEYWORDS,
    }

    /**
     * Returns all possible parents of a keyword name.
     *
     * @param  keywordName Name of the keyword
     * @param  select      Keywords to add (filter)
     * @return             Possible keyword's parents. The collection is
     *                     empty if the keyword has no parents. If more than one
     *                     keyword has the same name, the collection contains a
     *                     collection of every keyword's parents if it has
     *                     parents. The keywords ordered by their path, the
     *                     leftmost keyword is the root keyword.
     */
    public Collection<Collection<HierarchicalKeyword>> getParents(
            String keywordName, Select select) {
        List<Collection<HierarchicalKeyword>> paths =
                new ArrayList<Collection<HierarchicalKeyword>>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id_parent FROM hierarchical_subjects" +
                    " WHERE subject = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, keywordName);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long idParent = rs.getLong(1);
                if (!rs.wasNull()) {
                    List<HierarchicalKeyword> path =
                            new ArrayList<HierarchicalKeyword>();
                    addPathToRoot(path, idParent, select, connection);
                    Collections.reverse(path);
                    paths.add(path);
                }
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalKeywords.class, ex);
        } finally {
            free(connection);
        }
        return paths;
    }

    private void addPathToRoot(
            Collection<HierarchicalKeyword> path, long idParent, Select select,
            Connection connection)
            throws SQLException {
        HierarchicalKeyword keyword = getKeyword(idParent, connection);
        if (keyword != null) {
            Boolean real = keyword.isReal() || keyword.isReal() == null;
            if (select.equals(Select.ALL_KEYWORDS) ||
                    select.equals(Select.REAL_KEYWORDS) && real) {
                path.add(keyword);
            }
            Long idNextParent = keyword.getIdParent();
            if (idNextParent != null) {
                addPathToRoot(path, idNextParent, select, connection); // recursive
            }
        }
    }
}
