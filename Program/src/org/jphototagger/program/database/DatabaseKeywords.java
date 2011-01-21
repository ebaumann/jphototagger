package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Keyword;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//Handling null:
//ID: Can never be null
//ID parent: Can be null, must be handle when setting and getting
//Keyword: Can never be null
//Real: Can be null but it's ok to use false when reading and writing if null

/**
 * Contains keywords.
 *
 * @author Elmar Baumann
 */
public final class DatabaseKeywords extends Database {
    public static final DatabaseKeywords INSTANCE = new DatabaseKeywords();

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

    private DatabaseKeywords() {}

    /**
     * Returns all keywords.
     *
     * @return all keywords
     */
    public Collection<Keyword> getAll() {
        List<Keyword> keywords = new ArrayList<Keyword>();
        Connection    con      = null;
        Statement     stmt     = null;
        ResultSet     rs       = null;

        try {
            con = getConnection();

            String sql = "SELECT id, id_parent, subject, real"
                         + " FROM hierarchical_subjects";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Long idParent = rs.getLong(2);

                if (rs.wasNull()) {
                    idParent = null;
                }

                keywords.add(new Keyword(rs.getLong(1), idParent,
                                         rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            keywords.clear();
        } finally {
            close(rs, stmt);
            free(con);
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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        boolean           updated = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        assert keyword.getId() != null;

        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement(
                "UPDATE hierarchical_subjects"
                + " SET id_parent = ?, subject = ?, real = ?"
                + " WHERE id = ?");

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
            free(con);
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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        boolean inserted = false;

        assert keyword.getName() != null : "Keyword is null!";
        assert !keyword.getName().trim().isEmpty() : "Keyword is empty!";

        if (hasParentChildWithEqualName(keyword)) {
            return false;
        }

        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            String sql =
                "INSERT INTO hierarchical_subjects"
                + " (id, id_parent, subject, real) VALUES (?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);

            long nextId = findNextId(con);

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
        } finally {
            close(stmt);
            free(con);
        }

        return inserted;
    }

    /**
     * Deletes all keywords.
     *
     * @return count of deleted keywords
     */
    public int deleteAllKeywords() {
        Connection        con           = null;
        PreparedStatement stmt          = null;
        int               countAffected = 0;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM hierarchical_subjects");
            logFiner(stmt);
            countAffected = stmt.executeUpdate();
            con.commit();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffected;
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
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        boolean           deleted = false;
        Connection        con     = null;
        PreparedStatement stmt    = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM hierarchical_subjects WHERE id = ?");

            for (Keyword keyword : keywords) {
                stmt.setLong(1, keyword.getId());
                logFiner(stmt);
                stmt.executeUpdate();
            }

            con.commit();
            deleted = true;
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return deleted;
    }

    private Keyword findKeyword(long id, Connection con) throws SQLException {
        Keyword           keyword = null;
        PreparedStatement stmt    = null;
        ResultSet         rs      = null;

        try {
            String sql = "SELECT id, id_parent, subject, real"
                         + " FROM hierarchical_subjects WHERE id = ?";

            stmt = con.prepareStatement(sql);
            stmt.setLong(1, id);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Long idParent = rs.getLong(2);

                if (rs.wasNull()) {
                    idParent = null;
                }

                keyword = new Keyword(rs.getLong(1), idParent, rs.getString(3),
                                      rs.getBoolean(4));
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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        List<Keyword> parents = new ArrayList<Keyword>();
        Connection    con     = null;

        try {
            con = getConnection();

            Long idParent = keyword.getIdParent();

            while (idParent != null) {
                Keyword parent = findKeyword(idParent, con);

                parents.add(parent);
                idParent = parent.getIdParent();
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            free(con);
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
        Connection          con      = null;
        PreparedStatement   stmt     = null;
        ResultSet           rs       = null;

        try {
            con = getConnection();

            String sql = "SELECT id, id_parent, subject, real"
                         + " FROM hierarchical_subjects"
                         + " WHERE id_parent = ? ORDER BY subject ASC";

            stmt = con.prepareStatement(sql);
            stmt.setLong(1, idParent);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Long idPar = rs.getLong(2);

                if (rs.wasNull()) {
                    idPar = null;
                }

                children.add(new Keyword(rs.getLong(1), idPar, rs.getString(3),
                                         rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
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
        Connection          con      = null;
        PreparedStatement   stmt     = null;
        ResultSet           rs       = null;

        try {
            con = getConnection();

            String sql = "SELECT id, id_parent, subject, real"
                         + " FROM hierarchical_subjects"
                         + " WHERE id_parent IS NULL ORDER BY subject ASC";

            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Long idParent = rs.getLong(2);

                if (rs.wasNull()) {
                    idParent = null;
                }

                children.add(new Keyword(rs.getLong(1), idParent,
                                         rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return children;
    }

    private synchronized long findNextId(Connection con) throws Exception {
        long      id   = 1;
        String    sql  = "SELECT MAX(id) FROM hierarchical_subjects";
        Statement stmt = null;
        ResultSet rs   = null;

        try {
            stmt = con.createStatement();
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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        boolean exists       = false;
        boolean parentIsRoot = keyword.getIdParent() == null;

        assert keyword.getName() != null;

        if (keyword.getName() == null) {
            return false;
        }

        Connection        con  = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        try {
            con = getConnection();

            String sql = parentIsRoot
                         ? "SELECT COUNT(*) FROM hierarchical_subjects"
                           + " WHERE id_parent IS NULL AND subject = ?"
                         : "SELECT COUNT(*) FROM hierarchical_subjects"
                           + " WHERE id_parent = ? AND subject = ?";

            stmt = con.prepareStatement(sql);

            if (!parentIsRoot) {
                stmt.setLong(1, keyword.getIdParent());
            }

            stmt.setString(parentIsRoot
                           ? 1
                           : 2, keyword.getName());
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
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
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM hierarchical_subjects"
                         + " WHERE  subject = ? AND id_parent IS NULL";

            stmt = con.prepareStatement(sql);
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
            free(con);
        }

        return exists;
    }

    public boolean exists(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }

        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM hierarchical_subjects"
                         + " WHERE subject = ?";

            stmt = con.prepareStatement(sql);
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
            free(con);
        }

        return exists;
    }

    /**
     * Renames all real subjects whith a specific name regardless of their
     * parent.
     *
     * @param  fromName old name
     * @param  toName   new name
     * @return          count of renamed subjects
     */
    public int updateRenameAll(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

        try {
            con = getConnection();
            con.setAutoCommit(true);

            String sql = "UPDATE hierarchical_subjects SET subject = ?"
                         + " WHERE subject = ? AND real = TRUE";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            logFinest(stmt);
            count = stmt.executeUpdate();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return count;
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
    public Collection<Collection<Keyword>> getParents(String keywordName,
            Select select) {
        if (keywordName == null) {
            throw new NullPointerException("keywordName == null");
        }

        if (select == null) {
            throw new NullPointerException("select == null");
        }

        List<Collection<Keyword>> paths = new ArrayList<Collection<Keyword>>();
        Connection                con   = null;
        PreparedStatement         stmt  = null;
        ResultSet                 rs    = null;

        try {
            con = getConnection();

            String sql =
                "SELECT id_parent FROM hierarchical_subjects WHERE subject = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, keywordName);
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                long idParent = rs.getLong(1);

                if (!rs.wasNull()) {
                    List<Keyword> path = new ArrayList<Keyword>();

                    addPathToRoot(path, idParent, select, con);
                    Collections.reverse(path);
                    paths.add(path);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseKeywords.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return paths;
    }

    private void addPathToRoot(Collection<Keyword> path, long idParent,
                               Select select, Connection con)
            throws SQLException {
        Keyword keyword = findKeyword(idParent, con);

        if (keyword != null) {
            Boolean real = keyword.isReal() || (keyword.isReal() == null);

            if (select.equals(Select.ALL_KEYWORDS)
                    || (select.equals(Select.REAL_KEYWORDS) && real)) {
                path.add(keyword);
            }

            Long idNextParent = keyword.getIdParent();

            if (idNextParent != null) {
                addPathToRoot(path, idNextParent, select, con);    // recursive
            }
        }
    }
}
