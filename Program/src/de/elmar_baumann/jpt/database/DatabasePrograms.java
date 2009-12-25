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
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.event.DatabaseProgramEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains external Programs to start within the application. The primary
 * key of a program is it's <strong>ID</strong>
 * ({@link de.elmar_baumann.jpt.data.Program#getId()}).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-04
 */
public final class DatabasePrograms extends Database {

    public static final DatabasePrograms INSTANCE = new DatabasePrograms();

    private DatabasePrograms() {
    }

    /**
     * Inserts a new program. Prevoius You should call {@link #hasProgram()}.
     * 
     * @param  program  program
     * @return true if inserted
     */
    public boolean insert(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            setId(connection, program);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO programs" +
                    " (" +
                    "id" +                              // --  1 --
                    ", action" +                        // --  2 --
                    ", filename" +                      // --  3 --
                    ", alias" +                         // --  4 --
                    ", parameters_before_filename" +    // --  5 --
                    ", parameters_after_filename" +     // --  6 --
                    ", input_before_execute" +          // --  7 --
                    ", input_before_execute_per_file" + // --  8 --
                    ", single_file_processing" +        // --  9 --
                    ", change_file" +                   // -- 10 --
                    ", sequence_number" +               // -- 11 --
                    ", use_pattern" +                   // -- 12 --
                    ", pattern" +                       // -- 13 --
                    ")" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            setValuesInsert(stmt, program);
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    private void setValuesInsert(PreparedStatement stmt, Program program) throws
            SQLException {
        stmt.setLong(1, program.getId());
        stmt.setBoolean(2, program.isAction());
        stmt.setString(3, program.getFile().getAbsolutePath());
        stmt.setString(4, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(5, parametersBeforeFilename == null
                         ? null
                         : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(6, parametersAfterFilename == null
                         ? null
                         : parametersAfterFilename.getBytes());
        stmt.setBoolean(7, program.isInputBeforeExecute());
        stmt.setBoolean(8, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(9, program.isSingleFileProcessing());
        stmt.setBoolean(10, program.isChangeFile());
        stmt.setInt(11, program.getSequenceNumber());
        stmt.setBoolean(12, program.isUsePattern());
        String pattern = program.getPattern();
        stmt.setBytes(13, pattern == null
                         ? null
                         : pattern.getBytes());
    }

    private void setId(Connection connection, Program program) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "SELECT MAX(id) FROM programs";
        logFinest(sql);
        ResultSet rs = stmt.executeQuery(sql);
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
    public boolean update(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE programs" +
                    " SET" +
                    " action = ?" +                         // --  1 --
                    ", filename = ?" +                      // --  2 --
                    ", alias = ?" +                         // --  3 --
                    ", parameters_before_filename = ?" +    // --  4 --
                    ", parameters_after_filename = ?" +     // --  5 --
                    ", input_before_execute = ?" +          // --  6 --
                    ", input_before_execute_per_file = ?" + // --  7 --
                    ", single_file_processing = ?" +        // --  8 --
                    ", change_file = ?" +                   // --  9 --
                    ", sequence_number = ?" +               // -- 10 --
                    ", use_pattern = ?" +                   // -- 11 --
                    ", pattern = ?" +                       // -- 12 --
                    " WHERE id = ?");                       // -- 13 --
            setValuesUpdate(stmt, program);
            stmt.setLong(13, program.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
            notifyDatabaseListener(DatabaseProgramEvent.Type.PROGRAM_UPDATED, program);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return countAffectedRows == 1;
    }

    private void setValuesUpdate(PreparedStatement stmt, Program program) throws
            SQLException {
        stmt.setBoolean(1, program.isAction());
        stmt.setString(2, program.getFile().getAbsolutePath());
        stmt.setString(3, program.getAlias());
        String parametersBeforeFilename = program.getParametersBeforeFilename();
        stmt.setBytes(4, parametersBeforeFilename == null
                         ? null
                         : parametersBeforeFilename.getBytes());
        String parametersAfterFilename = program.getParametersAfterFilename();
        stmt.setBytes(5, parametersAfterFilename == null
                         ? null
                         : parametersAfterFilename.getBytes());
        stmt.setBoolean(6, program.isInputBeforeExecute());
        stmt.setBoolean(7, program.isInputBeforeExecutePerFile());
        stmt.setBoolean(8, program.isSingleFileProcessing());
        stmt.setBoolean(9, program.isChangeFile());
        stmt.setInt(10, program.getSequenceNumber());
        stmt.setBoolean(11, program.isUsePattern());
        String pattern = program.getPattern();
        stmt.setBytes(12, pattern == null
                         ? null
                         : pattern.getBytes());
    }

    /**
     * Deletes a program. <em>The ID must exist!</em>
     * 
     * @param  program   program
     * @return true if deleted
     */
    public boolean delete(Program program) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM programs WHERE id = ?");
            stmt.setLong(1, program.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            // Hack because of dirty design of this table (no cascade possible)
            DatabaseActionsAfterDbInsertion.INSTANCE.delete(program);
            stmt.close();
            notifyDatabaseListener(DatabaseProgramEvent.Type.PROGRAM_DELETED, program);
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
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
                    getSelectProgramStmt(WhereFilter.ACTION));
            stmt.setBoolean(1, action);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                programs.add(getSelctedProgram(rs));
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
        } finally {
            free(connection);
        }
        return programs;
    }

    private enum WhereFilter {

        ID,
        ACTION
    }

    private String getSelectProgramStmt(WhereFilter filter) {
        return "SELECT" +
                " id" +                             // --  1 --
                ", action" +                        // --  2 --
                ", filename" +                      // --  3 --
                ", alias" +                         // --  4 --
                ", parameters_before_filename" +    // --  5 --
                ", parameters_after_filename" +     // --  6 --
                ", input_before_execute" +          // --  7 --
                ", input_before_execute_per_file" + // --  8 --
                ", single_file_processing" +        // --  9 --
                ", change_file" +                   // -- 10 --
                ", sequence_number" +               // -- 11 --
                ", use_pattern" +                   // -- 12 --
                ", pattern" +                       // -- 13 --
                " FROM programs" +
                (filter.equals(WhereFilter.ACTION)
                 ? " WHERE action = ?"
                 : filter.equals(WhereFilter.ID)
                   ? " WHERE id = ?"
                   : "") +
                " ORDER BY alias";
    }

    private Program getSelctedProgram(ResultSet rs) throws SQLException {
        byte[]  parametersBeforeFilename = rs.getBytes(5);
        byte[]  parametersAfterFilename  = rs.getBytes(6);
        byte[]  pattern                  = rs.getBytes(13);
        Program program                  = new Program();

        program.setId(rs.getLong(1));
        program.setAction(rs.getBoolean(2));
        program.setFile(new File(rs.getString(3)));
        program.setAlias(rs.getString(4));
        program.setParametersBeforeFilename(parametersBeforeFilename == null
                                            ? null
                                            : new String(parametersBeforeFilename));
        program.setParametersAfterFilename(parametersAfterFilename == null
                                           ? null
                                           : new String(parametersAfterFilename));
        program.setInputBeforeExecute(rs.getBoolean(7));
        program.setInputBeforeExecutePerFile(rs.getBoolean(8));
        program.setSingleFileProcessing(rs.getBoolean(9));
        program.setChangeFile(rs.getBoolean(10));
        program.setSequenceNumber(rs.getInt(11));
        program.setUsePattern(rs.getBoolean(12));
        program.setPattern(pattern == null
                                           ? null
                                           : new String(pattern));
        return program;
    }

    /**
     * Returns a program with a specific ID.
     *
     * @param  id  the program's ID
     * @return Program or null if no program has this ID
     */
    public Program getProgram(long id) {
        Program program = null;
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    getSelectProgramStmt(WhereFilter.ID));
            stmt.setLong(1, id);
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                program = getSelctedProgram(rs);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
        } finally {
            free(connection);
        }
        return program;
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
                    "SELECT COUNT(*) FROM programs WHERE action = FALSE");
            logFinest(stmt);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabasePrograms.class, ex);
        } finally {
            free(connection);
        }
        return count > 0;
    }
}
