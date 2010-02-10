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
import de.elmar_baumann.jpt.data.Keyword;
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
 * Contains keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-10
 */
public final class DatabaseKeywords extends Database {

    public static final DatabaseKeywords INSTANCE = new DatabaseKeywords();

    private DatabaseKeywords() {
    }

    /**
     * Returns all keywords.
     *
     * @return all keywords
     */
    public Collection<Keyword> getAll() {
        List<Keyword> keywords    = new ArrayList<Keyword>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT id, id_parent, subject, real FROM hierarchical_subjects";
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) idParent = null;
                keywords.add(new Keyword(rs.getLong(1), idParent,
                        rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            keywords.clear();
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return keywords;
    }

    /**
     * Updates a keyword.
     *
     * @param  keyword keyword
     * @return         true if updated
     */
    public boolean update(Keyword keyword) {
        boolean updated = false;
        Connection connection = null;
        PreparedStatement stmt = null;

        assert keyword.getId() != null;

        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            stmt = connection.prepareStatement(
                    "UPDATE hierarchical_subjects" +
                    " SET id_parent = ?, subject = ?, real = ?" +
                    " WHERE id = ?");
            if (keyword.getIdParent() == null) {
                stmt.setNull(1, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(1, keyword.getIdParent());
            }
            stmt.setString(2, keyword.getName());
            if (keyword.isReal() == null) {
                stmt.setNull(3, java.sql.Types.BOOLEAN);
            } else {
                stmt.setBoolean(3, keyword.isReal());
            }
            stmt.setLong(4, keyword.getId());
            logFiner(stmt);
            updated = stmt.executeUpdate() == 1;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return updated;
    }

    /**
     * Inserts a keyword. If successfully inserted the keyword
     * has a value for it's id ({@link Keyword#getId()}.
     *
     * <em>The keyword ({@link Keyword#getName()}) must have
     * a not empty string!</em>
     *
     * @param  keyword keyword
     * @return         true if updated
     */
    public boolean insert(Keyword keyword) {
        boolean inserted = false;
        assert keyword.getName() != null : "Keyword is null!";
        assert !keyword.getName().trim().isEmpty() : "Keyword is empty!";
        if (hasParentChildWithEqualName(keyword)) return false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            String sql = "INSERT INTO hierarchical_subjects" +
                         " (id, id_parent, subject, real) VALUES (?, ?, ?, ?)";
            stmt = connection.prepareStatement(sql);
            long nextId = findNextId(connection);
            stmt.setLong(1, nextId);
            if (keyword.getIdParent() == null) {
                stmt.setNull(2, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(2, keyword.getIdParent());
            }
            stmt.setString(3, keyword.getName().trim());
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
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
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
    public boolean delete(Collection<Keyword> keywords) {
        boolean    deleted    = false;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            stmt = connection.prepareStatement(
                    "DELETE FROM hierarchical_subjects WHERE id = ?");
            for (Keyword keyword : keywords) {
                stmt.setLong(1, keyword.getId());
                logFiner(stmt);
                stmt.executeUpdate();
            }
            connection.commit();
            deleted = true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            rollback(connection);
        } finally {
            close(stmt);
            free(connection);
        }
        return deleted;
    }

    private Keyword findKeyword(long id, Connection connection) throws SQLException {
        Keyword keyword = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id, id_parent, subject, real" +
                         " FROM hierarchical_subjects" +
                         " WHERE id = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) idParent = null;
                keyword = new Keyword(
                        rs.getLong(1), idParent, rs.getString(3), rs.getBoolean(4));
            }
        } finally {
            close(rs, stmt);
        }
        return keyword;
    }

    /**
     * Returns all parents of a keyword.
     *
     * @param  keyword keyword
     * @return         Parents or empty List if the keyword has no parent
     */
    public List<Keyword> getParents(Keyword keyword) {
        List<Keyword> parents    = new ArrayList<Keyword>();
        Connection                connection = null;
        try {
            connection = getConnection();
            Long idParent = keyword.getIdParent();
            while (idParent != null) {
                Keyword parent = findKeyword(idParent, connection);
                parents.add(parent);
                idParent = parent.getIdParent();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
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
    public Collection<Keyword> getChildren(long idParent) {
        Collection<Keyword> children = new ArrayList<Keyword>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT id, id_parent, subject, real" +
                         " FROM hierarchical_subjects" +
                         " WHERE id_parent = ? ORDER BY subject ASC";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, idParent);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long idPar = rs.getLong(2);
                if (rs.wasNull()) idPar = null;
                children.add(new Keyword(
                        rs.getLong(1), idPar, rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return children;
    }

    /**
     * Return all root keywords: keywords with no parents.
     *
     * @return keyword with no parents ordered ascending by their keyword
     */
    public Collection<Keyword> getRoots() {
        Collection<Keyword> children = new ArrayList<Keyword>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT id, id_parent, subject, real" +
                         " FROM hierarchical_subjects" +
                         " WHERE id_parent IS NULL ORDER BY subject ASC";
            stmt = connection.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) idParent = null;
                children.add(new Keyword(rs.getLong(1), idParent,
                        rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return children;
    }

    private synchronized long findNextId(Connection connection) throws Exception {
        long      id   = 1;
        String    sql  = "SELECT MAX(id) FROM hierarchical_subjects";
        Statement stmt = null;
        ResultSet rs   = null;

        try {
            stmt = connection.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                id = rs.getLong(1) + 1;
            }
        } finally {
            close(rs, stmt);
        }
        return id;
    }

    /**
     * Returns whether a parent has a child with a specific
     * {@link Keyword#getName()}. The parent's ID is
     * {@link Keyword#getIdParent()}. The keyword's ID
     * {@link Keyword#getId()} will not be compared.
     *
     * @param  keyword keyword
     * @return         true if the keyword exists
     */
    public boolean hasParentChildWithEqualName(Keyword keyword) {
        boolean    exists       = false;
        boolean    parentIsRoot = keyword.getIdParent() == null;

        assert keyword.getName() != null;

        if (keyword.getName() == null) return false;

        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            String sql = parentIsRoot
                    ? "SELECT COUNT(*) FROM hierarchical_subjects" +
                      " WHERE id_parent IS NULL AND subject = ?"
                    : "SELECT COUNT(*) FROM hierarchical_subjects" +
                      " WHERE id_parent = ? AND subject = ?";

            stmt = connection.prepareStatement(sql);

            if (!parentIsRoot) {
                stmt.setLong(1, keyword.getIdParent());
            }
            stmt.setString(parentIsRoot ? 1 : 2, keyword.getName());

            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
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
        boolean           exists     = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects" +
                         " WHERE  subject = ? AND id_parent IS NULL";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, keyword);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    public boolean exists(String keyword) {
        boolean           exists     = false;
        Connection        connection = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;
        try {
            connection = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects" +
                         " WHERE subject = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, keyword);
            logFinest(stmt);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return exists;
    }

    /**
     * Renames all real subjects whith a specific name regardless of their parent.
     *
     * @param  oldName old name
     * @param  newName new name
     * @return         count of renamed subjects
     */
    public int updateRenameAll(String oldName, String newName) {
        int count = 0;
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = getConnection();
            String sql = "UPDATE hierarchical_subjects SET subject = ?" +
                         " WHERE subject = ? AND real = TRUE";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            logFinest(stmt);
            count = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(stmt);
            free(connection);
        }
        return count;
    }

    public enum Select {

        /**
         * Select all keywords
         */
        ALL_KEYWORDS,
        /**
         * Select (only) real keywords. Real keywords are keywords where
         * {@link Keyword#isReal()} returns true or null.
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
    public Collection<Collection<Keyword>> getParents(String keywordName, Select select) {
        List<Collection<Keyword>> paths = new ArrayList<Collection<Keyword>>();
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getConnection();
            String sql = "SELECT id_parent FROM hierarchical_subjects WHERE subject = ?";
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, keywordName);
            logFinest(stmt);
            rs = stmt.executeQuery();
            while (rs.next()) {
                long idParent = rs.getLong(1);
                if (!rs.wasNull()) {
                    List<Keyword> path = new ArrayList<Keyword>();
                    addPathToRoot(path, idParent, select, connection);
                    Collections.reverse(path);
                    paths.add(path);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(connection);
        }
        return paths;
    }

    private void addPathToRoot(Collection<Keyword> path, long idParent, Select select, Connection connection) throws SQLException {
        Keyword keyword = findKeyword(idParent, connection);
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
