package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.data.Program;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Contains external Programs to start within the application. The primary
 * key of a program is it's <strong>ID</strong>
 * ({@link de.elmar_baumann.imv.data.Program#getId()}).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
public class DatabasePrograms extends Database {

    private static DatabasePrograms instance = new DatabasePrograms();

    private DatabasePrograms() {
    }

    public static DatabasePrograms getInstance() {
        return instance;
    }

    /**
     * Inserts a new program. Prevoius You should call 
     * {@link #existsProgram(de.elmar_baumann.imv.data.Program)}.
     * 
     * @param  program  program
     * @return true if inserted
     */
    synchronized public boolean insert(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            setId(connection, program);
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO programs" + // NOI18N
                " (" + // NOI18N
                "id" + // NOI18N -- 1 --
                ", filename" + // NOI18N -- 2 --
                ", alias" + // NOI18N -- 3 --
                ", parameters" + // NOI18N -- 4 --
                ", sequence_number" + // NOI18N -- 5 --
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?)"); // NOI18N
            setValuesInsert(stmt, program);
            logStatement(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    synchronized private void setId(Connection connection, Program program) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM programs");
        if (rs.next()) {
            program.setId(rs.getLong(1) + 1);
        }
    }

    private void setValuesInsert(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setLong(1, program.getId());
        stmt.setString(2, program.getFile().getAbsolutePath());
        stmt.setString(3, program.getAlias());
        String parameters = program.getParameters();
        stmt.setBytes(4, parameters == null ? null : parameters.getBytes());
        stmt.setInt(5, program.getSequenceNumber());
    }

    /**
     * Updates a program. <em>The id must exist!</em>
     * 
     * @param   program  program
     * @return  true if updated
     */
    synchronized public boolean update(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "UPDATE programs" + // NOI18N
                " SET" + // NOI18N
                " filename = ?" + // NOI18N -- 1 --
                ", alias = ?" + // NOI18N -- 2 --
                ", parameters = ?" + // NOI18N -- 3 --
                ", sequence_number = ?" + // NOI18N -- 4 --
                " WHERE id = ?"); // NOI18N
            setValuesUpdate(stmt, program);
            stmt.setLong(5, program.getId());
            logStatement(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    private void setValuesUpdate(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setString(1, program.getFile().getAbsolutePath());
        stmt.setString(2, program.getAlias());
        String parameters = program.getParameters();
        stmt.setBytes(3, parameters == null ? null : parameters.getBytes());
        stmt.setInt(4, program.getSequenceNumber());
    }

    /**
     * Deletes a program. <em>The ID must exist!</em>
     * 
     * @param  program   program
     * @return true if deleted
     */
    synchronized public boolean delete(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM programs WHERE id = ?"); // NOI18N
            stmt.setLong(1, program.getId());
            logStatement(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                handleException(ex1, Level.SEVERE);
            }
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    /**
     * Returns all programs ordered by their aliases.
     * 
     * @return programs
     */
    public List<Program> getAll() {
        List<Program> programs = new LinkedList<Program>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT id, filename, alias, parameters, sequence_number" + // NOI18N
                " FROM programs ORDER BY alias"); // NOI18N
            while (rs.next()) {
                byte[] parameters = rs.getBytes(4);
                programs.add(new Program(
                    rs.getLong(1),
                    new File(rs.getString(2)),
                    rs.getString(3),
                    parameters == null ? null : new String(parameters),
                    rs.getInt(5)));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return programs;
    }

    /**
     * Returns whether the database contains at least one program.
     * 
     * @return true if at least one program (ore more) exists
     */
    public boolean hasProgram() {
        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM programs"); // NOI18N
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return count > 0;
    }
}
