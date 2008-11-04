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
 * Contains external Programs to start within the application.
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

    synchronized public boolean insert(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO programs" + // NOI18N
                " (nickname" + // NOI18N -- 1 --
                ", filename" + // NOI18N -- 2 --
                ", parameters" + // NOI18N -- 3 --
                ")" + // NOI18N
                " VALUES (?, ?, ?)"); // NOI18N
            setValues(stmt, program);
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

    private void setValues(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setString(1, program.getNickname());
        stmt.setString(2, program.getFile().getAbsolutePath());
        String parameters = program.getParameters();
        stmt.setBytes(3, parameters == null ? null : parameters.getBytes());
    }

    synchronized public boolean update(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "UPTDATE programs" + // NOI18N
                " SET" + // NOI18N
                " nickname = ?" + // NOI18N -- 1 --
                ", filename = ?" + // NOI18N -- 2 --
                ", parameters = ?" + // NOI18N -- 3 --
                " WHERE nickname = ?"); // NOI18N
            setValues(stmt, program);
            stmt.setString(4, program.getNickname());
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

    synchronized public boolean delete(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM programs WHERE nickname = ?"); // NOI18N
            stmt.setString(1, program.getNickname());
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

    public List<Program> selectAll() {
        List<Program> programs = new LinkedList<Program>();
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT nickname, filename, parameters FROM programs ORDER BY 1"); // NOI18N
            while (rs.next()) {
                byte[] parameters = rs.getBytes(3);
                programs.add(new Program(
                    rs.getString(1), new File(rs.getString(2)),
                    parameters == null ? null : new String(parameters)));
            }
            stmt.close();
        } catch (SQLException ex) {
            handleException(ex, Level.SEVERE);
        } finally {
            free(connection);
        }
        return programs;
    }

    public boolean existsProgram(Program program) {
        int count = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM programs WHERE nickname = ?"); // NOI18N
            stmt.setString(1, program.getNickname());
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
        return count == 1;
    }

    /**
     * Returns whether a program exists (program count greater zero?).
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
