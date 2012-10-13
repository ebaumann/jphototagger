package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.keywords.KeywordType;

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
final class KeywordsDatabase extends Database {

    static final KeywordsDatabase INSTANCE = new KeywordsDatabase();
    private static final Logger LOGGER = Logger.getLogger(KeywordsDatabase.class.getName());

    private KeywordsDatabase() {
    }

    /**
     * Returns all keywords.
     *
     * @return all keywords
     */
    Collection<Keyword> getAllKeywords() {
        List<Keyword> keywords = new ArrayList<>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT id, id_parent, subject, real FROM hierarchical_subjects";
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) {
                    idParent = null;
                }
                keywords.add(new Keyword(rs.getLong(1), idParent, rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    boolean updateKeyword(Keyword keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        boolean updated = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            stmt = con.prepareStatement("UPDATE hierarchical_subjects"
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
            LOGGER.log(Level.FINER, stmt.toString());
            updated = stmt.executeUpdate() == 1;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(stmt);
            free(con);
        }

        return updated;
    }

    /**
     * Inserts a keyword. If successfully inserted the keyword
     * has a value for it's id ({@code Keyword#getId()}.
     *
     * <em>The keyword ({@code Keyword#getName()}) must have
     * a not empty string!</em>
     *
     * @param  keyword keyword
     * @return         true if updated
     */
    boolean insertKeyword(Keyword keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        boolean inserted = false;
        assert keyword.getName() != null : "Keyword is null!";
        assert !keyword.getName().trim().isEmpty() : "Keyword is empty!";
        if (hasParentChildKeywordWithEqualName(keyword)) {
            return false;
        }
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            String sql = "INSERT INTO hierarchical_subjects"
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
            LOGGER.log(Level.FINER, stmt.toString());
            inserted = stmt.executeUpdate() == 1;
            if (inserted) {
                keyword.setId(nextId);
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    int deleteAllKeywords() {
        Connection con = null;
        PreparedStatement stmt = null;
        int countAffected = 0;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM hierarchical_subjects");
            LOGGER.log(Level.FINER, stmt.toString());
            countAffected = stmt.executeUpdate();
            con.commit();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    boolean deleteKeywords(Collection<Keyword> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }
        boolean deleted = false;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM hierarchical_subjects WHERE id = ?");
            for (Keyword keyword : keywords) {
                stmt.setLong(1, keyword.getId());
                LOGGER.log(Level.FINER, stmt.toString());
                stmt.executeUpdate();
            }
            con.commit();
            deleted = true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return deleted;
    }

    private Keyword findKeyword(long id, Connection con) throws SQLException {
        Keyword keyword = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id, id_parent, subject, real"
                    + " FROM hierarchical_subjects WHERE id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, id);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) {
                    idParent = null;
                }
                keyword = new Keyword(rs.getLong(1), idParent, rs.getString(3), rs.getBoolean(4));
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
    List<Keyword> getParentKeywords(Keyword keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        List<Keyword> parents = new ArrayList<>();
        Connection con = null;
        try {
            con = getConnection();
            Long idParent = keyword.getIdParent();
            while (idParent != null) {
                Keyword parent = findKeyword(idParent, con);
                parents.add(parent);
                idParent = parent.getIdParent();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    Collection<Keyword> getChildKeywords(long idParent) {
        Collection<Keyword> children = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT id, id_parent, subject, real"
                    + " FROM hierarchical_subjects"
                    + " WHERE id_parent = ? ORDER BY subject ASC";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, idParent);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long idPar = rs.getLong(2);
                if (rs.wasNull()) {
                    idPar = null;
                }
                children.add(new Keyword(rs.getLong(1), idPar, rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    Collection<Keyword> getRootKeywords() {
        Collection<Keyword> children = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT id, id_parent, subject, real"
                    + " FROM hierarchical_subjects"
                    + " WHERE id_parent IS NULL ORDER BY subject ASC";
            stmt = con.prepareStatement(sql);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                Long idParent = rs.getLong(2);
                if (rs.wasNull()) {
                    idParent = null;
                }
                children.add(new Keyword(rs.getLong(1), idParent, rs.getString(3), rs.getBoolean(4)));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return children;
    }

    private synchronized long findNextId(Connection con) throws Exception {
        long id = 1;
        String sql = "SELECT MAX(id) FROM hierarchical_subjects";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
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
     * {@code Keyword#getName()}. The parent's ID is
     * {@code Keyword#getIdParent()}. The keyword's ID
     * {@code Keyword#getId()} will not be compared.
     *
     * @param  keyword keyword
     * @return         true if the keyword existsKeyword
     */
    boolean hasParentChildKeywordWithEqualName(Keyword keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        boolean exists = false;
        boolean parentIsRoot = keyword.getIdParent() == null;
        if (keyword.getName() == null) {
            return false;
        }
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = parentIsRoot
                    ? "SELECT COUNT(*) FROM hierarchical_subjects WHERE id_parent IS NULL AND subject = ?"
                    : "SELECT COUNT(*) FROM hierarchical_subjects WHERE id_parent = ? AND subject = ?";
            stmt = con.prepareStatement(sql);
            if (!parentIsRoot) {
                stmt.setLong(1, keyword.getIdParent());
            }
            stmt.setString(parentIsRoot
                    ? 1
                    : 2, keyword.getName());
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    /**
     * Returns whether a specific root keyword existsKeyword.
     *
     * @param  keyword keyword
     * @return         true if that keyword existsKeyword
     */
    boolean existsRootKeyword(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects WHERE  subject = ? AND id_parent IS NULL";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, keyword);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    boolean existsKeyword(String keyword) {
        if (keyword == null) {
            throw new NullPointerException("keyword == null");
        }
        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM hierarchical_subjects WHERE subject = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, keyword);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    int updateRenameAllKeywords(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }
        if (toName == null) {
            throw new NullPointerException("toName == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(true);
            String sql = "UPDATE hierarchical_subjects SET subject = ? WHERE subject = ? AND real = TRUE";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, toName);
            stmt.setString(2, fromName);
            LOGGER.log(Level.FINEST, stmt.toString());
            count = stmt.executeUpdate();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
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
    Collection<Collection<Keyword>> getParentKeywords(String keywordName, KeywordType select) {
        if (keywordName == null) {
            throw new NullPointerException("keywordName == null");
        }
        if (select == null) {
            throw new NullPointerException("select == null");
        }
        List<Collection<Keyword>> paths = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = "SELECT id_parent FROM hierarchical_subjects WHERE subject = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, keywordName);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                long idParent = rs.getLong(1);
                if (!rs.wasNull()) {
                    List<Keyword> path = new ArrayList<>();
                    addPathToRoot(path, idParent, select, con);
                    Collections.reverse(path);
                    paths.add(path);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return paths;
    }

    private void addPathToRoot(Collection<Keyword> path, long idParent, KeywordType select, Connection con) throws SQLException {
        Keyword keyword = findKeyword(idParent, con);

        if (keyword != null) {
            Boolean real = keyword.isReal() || (keyword.isReal() == null);

            if (select.equals(KeywordType.REAL_OR_HELPER_KEYWORD) || (select.equals(KeywordType.REAL_KEYWORD) && real)) {
                path.add(keyword);
            }

            Long idNextParent = keyword.getIdParent();

            if (idNextParent != null) {
                addPathToRoot(path, idNextParent, select, con);    // recursive
            }
        }
    }
}
