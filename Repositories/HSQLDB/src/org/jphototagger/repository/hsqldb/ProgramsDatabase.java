package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.EventBus;
import org.jphototagger.domain.programs.DefaultProgram;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ActionsAfterRepoUpdatesRepository;
import org.jphototagger.domain.repository.event.programs.DefaultProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.DefaultProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.DefaultProgramUpdatedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramDeletedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramInsertedEvent;
import org.jphototagger.domain.repository.event.programs.ProgramUpdatedEvent;
import org.jphototagger.domain.repository.event.repoupdates.ActionAfterRepoUpdateDeletedEvent;
import org.openide.util.Lookup;

/**
 * Contains external Programs to start within the application. The primary key of a program is it's <strong>ID</strong>
 * ({@code org.jphototagger.program.data.Program#getId()}).
 *
 * @author Elmar Baumann
 */
final class ProgramsDatabase extends Database {

    static final ProgramsDatabase INSTANCE = new ProgramsDatabase();
    private static final Logger LOGGER = Logger.getLogger(ProgramsDatabase.class.getName());
    private final ActionsAfterRepoUpdatesRepository repo = Lookup.getDefault().lookup(ActionsAfterRepoUpdatesRepository.class);

    private enum WhereFilter {

        ID, ACTION
    }

    private ProgramsDatabase() {
    }

    /**
     * Inserts a new program. Prevoius You should call {@code #hasProgram()}.
     *
     * @param program program
     * @return true if inserted
     */
    boolean insertProgram(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO programs (id" // --  1 --
                    + ", action" // --  2 --
                    + ", filename" // --  3 --
                    + ", alias" // --  4 --
                    + ", parameters_before_filename" // --  5 --
                    + ", parameters_after_filename" // --  6 --
                    + ", input_before_execute" // --  7 --
                    + ", input_before_execute_per_file" // --  8 --
                    + ", single_file_processing" // --  9 --
                    + ", change_file" // -- 10 --
                    + ", sequence_number" // -- 11 --
                    + ", use_pattern" // -- 12 --
                    + ", pattern" // -- 13 --
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            con = getConnection();
            con.setAutoCommit(false);
            setId(con, program);
            stmt = con.prepareStatement(sql);
            ensureSequenceNumber(con, program);
            setValuesInsert(stmt, program);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            notifyInserted(program);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    private void setValuesInsert(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setLong(1, program.getId());
        stmt.setBoolean(2, program.isAction());
        stmt.setString(3, program.getFile().getAbsolutePath());
        stmt.setString(4, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(5, (parametersBeforeFilename == null)
                ? null
                : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(6, (parametersAfterFilename == null)
                ? null
                : parametersAfterFilename.getBytes());
        stmt.setBoolean(7, program.isInputBeforeExecute());
        stmt.setBoolean(8, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(9, program.isSingleFileProcessing());
        stmt.setBoolean(10, program.isChangeFile());
        stmt.setInt(11, program.getSequenceNumber());
        stmt.setBoolean(12, program.isUsePattern());
        String pattern = program.getPattern();
        stmt.setBytes(13, (pattern == null)
                ? null
                : pattern.getBytes());
    }

    private void setId(Connection con, Program program) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            String sql = "SELECT MAX(id) FROM programs";
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                program.setId(rs.getLong(1) + 1);
            }
        } finally {
            close(rs, stmt);
        }
    }

    /**
     * Updates a program. <em>The id must exist!</em>
     *
     * @param program program
     * @return true if updated
     */
    boolean updateProgram(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            String sql = "UPDATE programs SET action = ?" // --  1 --
                    + ", filename = ?" // --  2 --
                    + ", alias = ?" // --  3 --
                    + ", parameters_before_filename = ?" // --  4 --
                    + ", parameters_after_filename = ?" // --  5 --
                    + ", input_before_execute = ?" // --  6 --
                    + ", input_before_execute_per_file = ?" // --  7 --
                    + ", single_file_processing = ?" // --  8 --
                    + ", change_file = ?" // --  9 --
                    + ", sequence_number = ?" // -- 10 --
                    + ", use_pattern = ?" // -- 11 --
                    + ", pattern = ?" // -- 12 --
                    + " WHERE id = ?";    // -- 13 --
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            ensureSequenceNumber(con, program);
            setValuesUpdate(stmt, program);
            stmt.setLong(13, program.getId());
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            notifyUpdated(program);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    private void setValuesUpdate(PreparedStatement stmt, Program program) throws SQLException {
        stmt.setBoolean(1, program.isAction());
        stmt.setString(2, program.getFile().getAbsolutePath());
        stmt.setString(3, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(4, (parametersBeforeFilename == null)
                ? null
                : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(5, (parametersAfterFilename == null)
                ? null
                : parametersAfterFilename.getBytes());
        stmt.setBoolean(6, program.isInputBeforeExecute());
        stmt.setBoolean(7, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(8, program.isSingleFileProcessing());
        stmt.setBoolean(9, program.isChangeFile());
        stmt.setInt(10, program.getSequenceNumber());
        stmt.setBoolean(11, program.isUsePattern());
        String pattern = program.getPattern();
        stmt.setBytes(12, (pattern == null)
                ? null
                : pattern.getBytes());
    }

    /**
     * Deletes a program. <em>The ID must exist!</em>
     *
     * @param program program
     * @return true if deleted
     */
    boolean deleteProgram(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }
        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt.setLong(1, program.getId());
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            // Hack because of dirty design of this table (no cascade possible)
            boolean actionDeleted = repo.deleteAction(con, program);
            deleteProgramFromDefaultPrograms(con, program.getId());
            con.commit();
            if (actionDeleted) {
                EventBus.publish(new ActionAfterRepoUpdateDeletedEvent(this, program));
            }
            notifyDeleted(program);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    /**
     * Returns all programs ordered by their sequence numbers and aliases.
     *
     * @param type program type
     * @return programs
     */
    List<Program> getAllPrograms(ProgramType type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        List<Program> programs = new LinkedList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getSelectProgramSql(WhereFilter.ACTION));
            stmt.setBoolean(1, type.equals(ProgramType.ACTION));
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            while (rs.next()) {
                programs.add(createProgramOfCurrentRecord(rs));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return programs;
    }

    private String getSelectProgramSql(WhereFilter filter) {
        return "SELECT id" // --  1 --
                + ", action" // --  2 --
                + ", filename" // --  3 --
                + ", alias" // --  4 --
                + ", parameters_before_filename" // --  5 --
                + ", parameters_after_filename" // --  6 --
                + ", input_before_execute" // --  7 --
                + ", input_before_execute_per_file" // --  8 --
                + ", single_file_processing" // --  9 --
                + ", change_file" // -- 10 --
                + ", sequence_number" // -- 11 --
                + ", use_pattern" // -- 12 --
                + ", pattern" // -- 13 --
                + " FROM programs" + (filter.equals(WhereFilter.ACTION)
                ? " WHERE action = ?"
                : filter.equals(WhereFilter.ID)
                ? " WHERE id = ?"
                : "") + " ORDER BY sequence_number, alias";
    }

    private Program createProgramOfCurrentRecord(ResultSet rs) throws SQLException {
        byte[] parametersBeforeFilename = rs.getBytes(5);
        byte[] parametersAfterFilename = rs.getBytes(6);
        byte[] pattern = rs.getBytes(13);
        Program program = new Program();
        program.setId(rs.getLong(1));
        program.setAction(rs.getBoolean(2));
        program.setFile(new File(rs.getString(3)));
        program.setAlias(rs.getString(4));
        program.setParametersBeforeFilename((parametersBeforeFilename == null)
                ? null
                : new String(parametersBeforeFilename));
        program.setParametersAfterFilename((parametersAfterFilename == null)
                ? null
                : new String(parametersAfterFilename));
        program.setInputBeforeExecute(rs.getBoolean(7));
        program.setInputBeforeExecutePerFile(rs.getBoolean(8));
        program.setSingleFileProcessing(rs.getBoolean(9));
        program.setChangeFile(rs.getBoolean(10));
        program.setSequenceNumber(rs.getInt(11));
        program.setUsePattern(rs.getBoolean(12));
        program.setPattern((pattern == null)
                ? null
                : new String(pattern));
        return program;
    }

    private String getDefaultImageOpenProgramSql() {
        return "SELECT id" // --  1 --
                + ", action" // --  2 --
                + ", filename" // --  3 --
                + ", alias" // --  4 --
                + ", parameters_before_filename" // --  5 --
                + ", parameters_after_filename" // --  6 --
                + ", input_before_execute" // --  7 --
                + ", input_before_execute_per_file" // --  8 --
                + ", single_file_processing" // --  9 --
                + ", change_file" // -- 10 --
                + ", sequence_number" // -- 11 --
                + ", use_pattern" // -- 12 --
                + ", pattern" // -- 13 --
                + " FROM programs WHERE action = FALSE AND sequence_number = 0";
    }

    /**
     * Returns the default image open program: The program with the sequence number 0.
     *
     * @return program or null if the database has no program or on errors
     */
    Program getDefaultImageOpenProgram() {
        Program program = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String sql = getDefaultImageOpenProgramSql();
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                program = createProgramOfCurrentRecord(rs);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return program;
    }

    /**
     * Returns whether a program existsAction in the database. <p> Programs are treated as equals, if their aliases and
     * filenames are equal.
     *
     * @param program program
     * @return true if the program does exist
     */
    boolean existsProgram(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;
        try {
            String sql = "SELECT COUNT(*) FROM programs" + " WHERE alias = ? AND filename = ?";
            con = getConnection();
            stmt = con.prepareStatement(sql);
            setString(program.getAlias(), stmt, 1);
            setString(program.getFile().getAbsolutePath(), stmt, 2);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                exists = rs.getLong(1) > 0;
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return exists;
    }

    /**
     * Returns a program with a specific ID.
     *
     * @param id the program's ID
     * @return Program or null if no program has this ID
     */
    Program findProgram(long id) {
        Program program = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(getSelectProgramSql(WhereFilter.ID));
            stmt.setLong(1, id);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                program = createProgramOfCurrentRecord(rs);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return program;
    }

    /**
     * Returns whether the database contains at least one program (<em>no</em> action).
     *
     * @return true if at least one program (ore more) existsAction
     */
    boolean hasProgram() {
        return has(false);
    }

    /**
     * Returns whether the database contains at least one action (<em>no</em> program).
     *
     * @return true if at least one action (ore more) existsAction
     */
    boolean hasAction() {
        return has(true);
    }

    private boolean has(boolean action) {
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM programs WHERE action = "
                    + (action ? "TRUE" : "FALSE"));
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count > 0;
    }

    private void notifyDeleted(Program program) {
        EventBus.publish(new ProgramDeletedEvent(this, program));
    }

    private void notifyInserted(Program program) {
        EventBus.publish(new ProgramInsertedEvent(this, program));
    }

    private void notifyUpdated(Program program) {
        EventBus.publish(new ProgramUpdatedEvent(this, program));
    }

    /**
     * Returns the number of programs or actions.
     *
     * @param actions true if the number of actions shall be returned, false, if the number of actions shall be returned
     * @return number of programs or actions
     */
    int getProgramCount(boolean actions) {
        Connection con = null;
        try {
            con = getConnection();
            return getProgramCount(con, actions);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return 0;
        } finally {
            free(con);
        }
    }

    /**
     * Returns the number of programs or actions.
     *
     * @param actions true if the number of actions shall be returned, false, if the number of actions shall be returned
     * @return number of programs or actions
     */
    int getProgramCount(Connection con, boolean actions) {
        int count = 0;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT(*) FROM programs WHERE action = ?";
            stmt = con.prepareStatement(sql);
            stmt.setBoolean(1, actions);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
        }
        return count;
    }

    private void ensureSequenceNumber(Connection con, Program program) throws SQLException {
        if (program.getSequenceNumber() >= 0) {
            return;
        }
        int count = getProgramCount(con, program.isAction());
        if (count <= 0) {
            program.setSequenceNumber(0);
            return;
        }
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT MAX(sequence_number) FROM programs WHERE action = ?";
            stmt = con.prepareStatement(sql);
            stmt.setBoolean(1, program.isAction());
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                int max = rs.getInt(1);
                program.setSequenceNumber((max < 0)
                        ? 0
                        : max + 1);
            }
        } finally {
            close(rs, stmt);
        }
    }

    List<DefaultProgram> findAllDefaultPrograms() {
        Connection con = null;
        PreparedStatement stmt = null;
        List<DefaultProgram> defaultPrograms = new LinkedList<>();
        try {
            con = getConnection();
            String sql = "SELECT d.id_program, d.filename_suffix, p.alias"
                    + " FROM default_programs d INNER JOIN programs p"
                    + " ON d.id_program = p.id";
            stmt = con.prepareStatement(sql);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DefaultProgram defaultProgram = new DefaultProgram();
                defaultProgram.setIdProgram(rs.getLong(1));
                defaultProgram.setFilenameSuffix(rs.getString(2));
                defaultProgram.setProgramAlias(rs.getString(3));
                defaultPrograms.add(defaultProgram);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            return Collections.emptyList();
        } finally {
            close(stmt);
            free(con);
        }
        return defaultPrograms;
    }

    boolean setDefaultProgram(String filenameSuffix, long idProgram) {
        if (filenameSuffix == null) {
            throw new NullPointerException("filenameSuffix == null");
        }
        return existsDefaultProgram(filenameSuffix)
                ? updateDefaultProgram(filenameSuffix, idProgram) == 1
                : insertDefaultProgram(filenameSuffix, idProgram) == 1;
    }

    private int insertDefaultProgram(String filenameSuffix, long idProgram) {
        Connection con = null;
        PreparedStatement stmt = null;
        int countAffectedRows = 0;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sql = "INSERT INTO default_programs (id_program, filename_suffix) VALUES(?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, idProgram);
            stmt.setString(2, filenameSuffix);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            if (countAffectedRows == 1) {
                EventBus.publish(new DefaultProgramInsertedEvent(this, filenameSuffix, idProgram));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows;
    }

    private int updateDefaultProgram(String filenameSuffix, long idProgram) {
        Connection con = null;
        PreparedStatement stmt = null;
        int countAffectedRows = 0;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sql = "UPDATE default_programs SET id_program = ? WHERE filename_suffix = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, idProgram);
            stmt.setString(2, filenameSuffix);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            if (countAffectedRows == 1) {
                EventBus.publish(new DefaultProgramUpdatedEvent(this, filenameSuffix, idProgram));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows;
    }

    boolean deleteDefaultProgram(String filenameSuffix) {
        Connection con = null;
        PreparedStatement stmt = null;
        int countAffectedRows = 0;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            String sql = "DELETE FROM default_programs WHERE filename_suffix = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, filenameSuffix);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            if (countAffectedRows == 1) {
                EventBus.publish(new DefaultProgramDeletedEvent(this, filenameSuffix));
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return countAffectedRows == 1;
    }

    private void deleteProgramFromDefaultPrograms(Connection con, long idProgram) throws SQLException {
        PreparedStatement stmt = null;
        int countAffected;
        try {
            String sql = "DELETE FROM default_programs WHERE id_program = ?";
            stmt = con.prepareStatement(sql);
            stmt.setLong(1, idProgram);
            LOGGER.log(Level.FINER, stmt.toString());
            countAffected = stmt.executeUpdate(); // Possibly critical: Not notifications to event listeners
            if (countAffected > 0) {
                LOGGER.log(Level.INFO,
                        "Deleted {0} entries from default programs where program id was {1}",
                        new Object[]{countAffected, idProgram});
            }
        } finally {
            close(stmt);
        }
    }

    Program findDefaultProgram(String filenameSuffix) {
        if (filenameSuffix == null) {
            throw new NullPointerException("filenameSuffix == null");
        }
        Connection con = null;
        try {
            con = getConnection();
            long id = findDefaultProgramId(con, filenameSuffix);
            if (id > 0) {
                return findProgram(id);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            free(con);
        }
        return null;
    }

    private long findDefaultProgramId(Connection con, String filenameSuffix) throws SQLException {
        long id = Long.MIN_VALUE;
        PreparedStatement stmt = null;
        try {
            String sql = "SELECT id_program FROM default_programs WHERE filename_suffix = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, filenameSuffix);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(stmt);
        }
        return id;
    }

    boolean existsDefaultProgram(String filenameSuffix) {
        if (filenameSuffix == null) {
            throw new NullPointerException("filenameSuffix == null");
        }
        long count = 0;
        PreparedStatement stmt = null;
        Connection con = null;
        try {
            con = getConnection();
            String sql = "SELECT COUNT(*) FROM default_programs WHERE filename_suffix = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, filenameSuffix);
            LOGGER.log(Level.FINEST, stmt.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        } finally {
            close(stmt);
            free(con);
        }
        return count > 0;
    }
}
