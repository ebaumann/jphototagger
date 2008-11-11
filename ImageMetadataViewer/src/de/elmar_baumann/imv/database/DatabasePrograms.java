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
                ", action" + // NOI18N -- 2 --
                ", filename" + // NOI18N -- 3 --
                ", alias" + // NOI18N -- 4 --
                ", parameters_before_filename" + // NOI18N -- 5 --
                ", parameters_after_filename" + // NOI18N -- 6 --
                ", input_before_execute" + // NOI18N -- 7 --
                ", input_before_execute_per_file" + // NOI18N -- 8 --
                ", single_file_processing" + // NOI18N -- 9 --
                ", change_file" + // NOI18N -- 10 --
                ", sequence_number" + // NOI18N -- 11 --
                ")" + // NOI18N
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"); // NOI18N
            setValuesInsert(stmt, program);
            logStatement(stmt, Level.FINER);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    private void setValuesInsert(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setLong(1, program.getId());
        stmt.setBoolean(2, program.isAction());
        stmt.setString(3, program.getFile().getAbsolutePath());
        stmt.setString(4, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(5, parametersBeforeFilename == null
            ? null : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(6, parametersAfterFilename == null
            ? null : parametersAfterFilename.getBytes());
        stmt.setBoolean(7, program.isInputBeforeExecute());
        stmt.setBoolean(8, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(9, program.isSingleFileProcessing());
        stmt.setBoolean(10, program.isChangeFile());
        stmt.setInt(11, program.getSequenceNumber());
    }

    synchronized private void setId(Connection connection, Program program) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM programs");
        if (rs.next()) {
            program.setId(rs.getLong(1) + 1);
        }
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
                " action = ?" + // NOI18N -- 1 --
                ", filename = ?" + // NOI18N -- 2 --
                ", alias = ?" + // NOI18N -- 3 --
                ", parameters_before_filename = ?" + // NOI18N -- 4 --
                ", parameters_after_filename = ?" + // NOI18N -- 5 --
                ", input_before_execute = ?" + // NOI18N -- 6 --
                ", input_before_execute_per_file = ?" + // NOI18N -- 7 --
                ", single_file_processing = ?" + // NOI18N -- 8 --
                ", change_file = ?" + // NOI18N -- 9 --
                ", sequence_number = ?" + // NOI18N -- 10 --
                " WHERE id = ?"); // NOI18N
            setValuesUpdate(stmt, program);
            stmt.setLong(11, program.getId());
            logStatement(stmt, Level.FINER);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    private void setValuesUpdate(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setBoolean(1, program.isAction());
        stmt.setString(2, program.getFile().getAbsolutePath());
        stmt.setString(3, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(4, parametersBeforeFilename == null
            ? null : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(5, parametersAfterFilename == null
            ? null : parametersAfterFilename.getBytes());
        stmt.setBoolean(6, program.isInputBeforeExecute());
        stmt.setBoolean(7, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(8, program.isSingleFileProcessing());
        stmt.setBoolean(9, program.isChangeFile());
        stmt.setInt(10, program.getSequenceNumber());
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
            logStatement(stmt, Level.FINER);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    /**
     * Returns all programs ordered by their aliases.
     * 
     * @param action  true if only return actions, false if only return
     *                programs
     * @return programs
     */
    public List<Program> getAll(boolean action) {
        List<Program> programs = new LinkedList<Program>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT" +
                " id" + // NOI18N -- 1 --
                ", action" + // NOI18N -- 2 --
                ", filename" + // NOI18N -- 3 --
                ", alias" + // NOI18N -- 4 --
                ", parameters_before_filename" + // NOI18N -- 5 --
                ", parameters_after_filename" + // NOI18N -- 6 --
                ", input_before_execute" + // NOI18N -- 7 --
                ", input_before_execute_per_file" + // NOI18N -- 8 --
                ", single_file_processing" + // NOI18N -- 9 --
                ", change_file" + // NOI18N -- 10 --
                ", sequence_number" + // NOI18N -- 11 --
                " FROM programs" + // NOI18N
                " WHERE action = ?" + // NOI18N
                " ORDER BY alias"); // NOI18N
            stmt.setBoolean(1, action);
            logStatement(stmt, Level.FINEST);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                byte[] parametersBeforeFilename = rs.getBytes(5);
                byte[] parametersAfterFilename = rs.getBytes(6);
                Program program = new Program();

                program.setId(rs.getLong(1));
                program.setAction(rs.getBoolean(2));
                program.setFile(new File(rs.getString(3)));
                program.setAlias(rs.getString(4));
                program.setParametersBeforeFilename(
                    parametersBeforeFilename == null
                    ? null : new String(parametersBeforeFilename));
                program.setParametersAfterFilename(
                    parametersAfterFilename == null
                    ? null : new String(parametersAfterFilename));
                program.setInputBeforeExecute(rs.getBoolean(7));
                program.setInputBeforeExecutePerFile(rs.getBoolean(8));
                program.setSingleFileProcessing(rs.getBoolean(9));
                program.setChangeFile(rs.getBoolean(10));
                program.setSequenceNumber(rs.getInt(11));

                programs.add(program);
            }
            stmt.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
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
                "SELECT COUNT(*) FROM programs WHERE action = FALSE"); // NOI18N
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
        } finally {
            free(connection);
        }
        return count > 0;
    }
}
