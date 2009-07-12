package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.HierarchicalSubject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains hierarchical subjects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/07/10
 */
public final class DatabaseHierarchicalSubjects extends Database {

    public static final DatabaseHierarchicalSubjects INSTANCE =
            new DatabaseHierarchicalSubjects();

    private DatabaseHierarchicalSubjects() {
    }

    /**
     * Returns all hierarchical subjects.
     *
     * @return all hierarchical subjects
     */
    public Collection<HierarchicalSubject> getAll() {
        List<HierarchicalSubject> subjects =
                new ArrayList<HierarchicalSubject>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id, id_parent, subject FROM hierarchical_subjects"; // NOI18N
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            AppLog.logFinest(DatabaseHierarchicalSubjects.class, sql);
            while (rs.next()) {
                subjects.add(new HierarchicalSubject(
                        rs.getLong(1), rs.getLong(2), rs.getString(3)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
            subjects.clear();
        } finally {
            free(connection);
        }
        return subjects;
    }

    /**
     * Updates a hierarchical subject.
     *
     * @param  subject subject
     * @return         true if updated
     */
    public boolean update(HierarchicalSubject subject) {
        boolean updated = false;
        Connection connection = null;
        assert subject.getId() != null : "ID of subject is null!";
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE hierarchical_subjects" + // NOI18N
                    " SET id_parent = ?, subject = ? WHERE id = ?"); // NOI18N
            stmt.setLong(1, subject.getIdParent());
            stmt.setString(2, subject.getSubject());
            stmt.setLong(3, subject.getId());
            AppLog.logFiner(DatabaseHierarchicalSubjects.class, stmt.toString());
            updated = stmt.executeUpdate() == 1;
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
        } finally {
            free(connection);
        }
        return updated;
    }

    /**
     * Inserts a hierarchical subject. If successfully inserted the subject
     * has a value for it's id ({@link HierarchicalSubject#getId()}.
     *
     * <em>The subject ({@link HierarchicalSubject#getSubject()}) must have
     * a not empty string!</em>
     * 
     * @param  subject subject
     * @return         true if updated
     */
    public boolean insert(HierarchicalSubject subject) {
        boolean inserted = false;
        Connection connection = null;
        assert subject.getSubject() != null : "Subject is null!";
        assert !subject.getSubject().trim().isEmpty() : "Subject empty!";
        if (parentHasChild(subject)) {
            return false;
        }
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO hierarchical_subjects (id" + // NOI18N
                    (subject.getIdParent() == null
                    ? "" // NOI18N
                    : ", id_parent") + // NOI18N
                    ", subject)" + // NOI18N
                    (subject.getIdParent() == null
                    ? " VALUES (?, ?)" // NOI18N
                    : " VALUES (?, ?, ?)")); // NOI18N
            long nextId = getNextId(connection);
            stmt.setLong(1, nextId);
            if (subject.getIdParent() != null) {
                stmt.setLong(2, subject.getIdParent());
            }
            stmt.setString(subject.getIdParent() == null
                    ? 2
                    : 3, subject.getSubject().trim());
            AppLog.logFiner(DatabaseHierarchicalSubjects.class, stmt.toString());
            inserted = stmt.executeUpdate() == 1;
            if (inserted) {
                subject.setId(nextId);
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return inserted;
    }

    /**
     * Deletes subjects from the database. Deletes <em>no</em> children of that
     * subject, all childrens should be in the collection of subjects. Does only
     * commit when all childrens were deleted.
     *
     * @param  subjects subjects
     * @return          true if successfull
     */
    public boolean delete(Collection<HierarchicalSubject> subjects) {
        boolean deleted = false;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM hierarchical_subjects WHERE id = ?"); // NOI18N
            for (HierarchicalSubject subject : subjects) {
                stmt.setLong(1, subject.getId());
                AppLog.logFiner(
                        DatabaseHierarchicalSubjects.class, stmt.toString());
                stmt.executeUpdate();
            }
            connection.commit();
            deleted = true;
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return deleted;
    }

    private HierarchicalSubject getSubject(long id, Connection connection)
            throws SQLException {
        HierarchicalSubject subject = null;
        String sql =
                "SELECT id, id_parent, subject FROM hierarchical_subjects" + // NOI18N
                " WHERE id = ?"; // NOI18N
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);
        AppLog.logFinest(DatabaseHierarchicalSubjects.class, stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            subject = new HierarchicalSubject(
                    rs.getLong(1), rs.getLong(2), rs.getString(3));
        }
        stmt.close();
        return subject;
    }

    /**
     * Returns all children of a parent subject orderd ascending by the subject.
     *
     * @param  idParent ID of the parent subject
     * @return          children or empty collection if that parent has no
     *                  children
     */
    public Collection<HierarchicalSubject> getChildren(long idParent) {
        Collection<HierarchicalSubject> children =
                new ArrayList<HierarchicalSubject>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id, id_parent, subject FROM hierarchical_subjects" + // NOI18N
                    " WHERE id_parent = ? ORDER BY subject ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, idParent);
            AppLog.logFinest(DatabaseHierarchicalSubjects.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                children.add(new HierarchicalSubject(
                        rs.getLong(1), rs.getLong(2), rs.getString(3)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
        } finally {
            free(connection);
        }
        return children;
    }

    /**
     * Return all root subjects: subjects with no parents.
     *
     * @return subject with no parents ordered ascending by their subject
     */
    public Collection<HierarchicalSubject> getRoots() {
        Collection<HierarchicalSubject> children =
                new ArrayList<HierarchicalSubject>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id, id_parent, subject FROM hierarchical_subjects" + // NOI18N
                    " WHERE id_parent IS NULL ORDER BY subject ASC"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            AppLog.logFinest(DatabaseHierarchicalSubjects.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                children.add(new HierarchicalSubject(
                        rs.getLong(1), rs.getLong(2), rs.getString(3)));
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
        } finally {
            free(connection);
        }
        return children;
    }

    private synchronized long getNextId(Connection connection)
            throws Exception {
        long id = 1;
        String sql =
                "SELECT MAX(id) FROM hierarchical_subjects"; // NOI18N
        Statement stmt = connection.createStatement();
        AppLog.logFinest(DatabaseHierarchicalSubjects.class, sql);
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            id = rs.getLong(1) + 1;
        }
        stmt.close();
        return id;
    }

    /**
     * Returns whether a parent has a child with a specific
     * {@link HierarchicalSubject#getSubject()}. The parent's ID is
     * {@link HierarchicalSubject#getIdParent()}. The subject's ID
     * {@link HierarchicalSubject#getId()} will not be compared.
     *
     * @param  subject subject
     * @return true if the subject exists
     */
    public boolean parentHasChild(HierarchicalSubject subject) {
        boolean exists = false;
        Connection connection = null;
        assert subject.getSubject() != null;
        if (subject.getSubject() == null) {
            return false;
        }
        try {
            connection = getConnection();
            String sql =
                    "SELECT COUNT(*) FROM hierarchical_subjects" + // NOI18N
                    " WHERE id_parent" + // NOI18N
                    (subject.getIdParent() == null
                    ? " IS NULL" // NOI18N
                    : " = ?") + // NOI18N
                    " AND subject = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            if (subject.getIdParent() != null) {
                stmt.setLong(1, subject.getIdParent());
            }
            stmt.setString(subject.getIdParent() == null
                    ? 1
                    : 2,
                    subject.getSubject());
            AppLog.logFinest(DatabaseHierarchicalSubjects.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
        } finally {
            free(connection);
        }
        return exists;
    }

    /**
     * Returns the names of a subject's parents.
     *
     * @param  subjectName name of the subject
     * @return             names of the subject's parents. The collection ist
     *                     empty if the subject has no parents. If there are
     *                     more than one subject with the same name , the
     *                     collection contains a collection of every subject's
     *                     parents if it has parents. The subjects ordered
     *                     by their path, the leftmost subject is the root
     *                     subject.
     */
    public Collection<Collection<String>> getParentNames(String subjectName) {
        List<Collection<String>> paths = new ArrayList<Collection<String>>();
        Connection connection = null;
        try {
            connection = getConnection();
            String sql =
                    "SELECT id_parent FROM hierarchical_subjects" + // NOI18N
                    " WHERE subject = ?"; // NOI18N
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, subjectName);
            AppLog.logFinest(DatabaseHierarchicalSubjects.class, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long idParent = rs.getLong(1);
                if (idParent > 0) {
                    List<String> path = new ArrayList<String>();
                    addPathToRoot(path, idParent, connection);
                    Collections.reverse(path);
                    paths.add(path);
                }
            }
            stmt.close();
        } catch (Exception ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
        } finally {
            free(connection);
        }
        return paths;
    }

    private void addPathToRoot(
            Collection<String> path, long idParent, Connection connection)
            throws SQLException {
        HierarchicalSubject subject = getSubject(idParent, connection);
        if (subject != null) {
            path.add(subject.getSubject());
            Long idNextParent = subject.getIdParent();
            if (idNextParent != null) {
                addPathToRoot(path, idNextParent, connection); // recursive
            }
        }
    }
}
