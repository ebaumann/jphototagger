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
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseHierarchicalSubjects.class, ex);
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
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseHierarchicalSubjects.class, ex);
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
        assert subject.getSubject().trim().isEmpty() : "Subject empty!";
        try {
            connection = getConnection();
            connection.setAutoCommit(true);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO hierarchical_subjects (id, id_parent, subject)" + // NOI18N
                    " VALUES (?, ?, ?)"); // NOI18N
            long nextId = getNextId(connection);
            stmt.setLong(1, nextId);
            stmt.setLong(2, subject.getIdParent());
            stmt.setString(3, subject.getSubject().trim());
            AppLog.logFiner(DatabaseHierarchicalSubjects.class, stmt.toString());
            inserted = stmt.executeUpdate() == 1;
            if (inserted) {
                subject.setId(nextId);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseHierarchicalSubjects.class, ex);
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
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseHierarchicalSubjects.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return deleted;
    }

    private synchronized long getNextId(Connection connection)
            throws SQLException {
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
}
