/*
 * @(#)DatabasePrograms.java    Created on 2008-11-04
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.event.listener.DatabaseProgramsListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains external Programs to start within the application. The primary
 * key of a program is it's <strong>ID</strong>
 * ({@link org.jphototagger.program.data.Program#getId()}).
 *
 * @author  Elmar Baumann
 */
public final class DatabasePrograms extends Database {
    public static final DatabasePrograms                    INSTANCE =
        new DatabasePrograms();
    private final ListenerSupport<DatabaseProgramsListener> ls =
        new ListenerSupport<DatabaseProgramsListener>();

    /**
     *
     */
    public enum Type { ACTION, PROGRAM }

    private enum WhereFilter { ID, ACTION }

    private DatabasePrograms() {}

    /**
     * Inserts a new program. Prevoius You should call {@link #hasProgram()}.
     *
     * @param  program  program
     * @return true if inserted
     */
    public boolean insert(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        int               countAffectedRows = 0;
        Connection        con               = null;
        PreparedStatement stmt              = null;

        try {
            String sql = "INSERT INTO programs (id"             // --  1 --
                         + ", action"                           // --  2 --
                         + ", filename"                         // --  3 --
                         + ", alias"                            // --  4 --
                         + ", parameters_before_filename"       // --  5 --
                         + ", parameters_after_filename"        // --  6 --
                         + ", input_before_execute"             // --  7 --
                         + ", input_before_execute_per_file"    // --  8 --
                         + ", single_file_processing"           // --  9 --
                         + ", change_file"                      // -- 10 --
                         + ", sequence_number"                  // -- 11 --
                         + ", use_pattern"                      // -- 12 --
                         + ", pattern"                          // -- 13 --
                         + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            con = getConnection();
            con.setAutoCommit(false);
            setId(con, program);
            stmt = con.prepareStatement(sql);
            setValuesInsert(stmt, program);
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            notifyInserted(program);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffectedRows == 1;
    }

    private void setValuesInsert(PreparedStatement stmt, Program program)
            throws SQLException {
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
        ResultSet rs   = null;

        try {
            stmt = con.createStatement();

            String sql = "SELECT MAX(id) FROM programs";

            logFinest(sql);
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
     * @param   program  program
     * @return  true if updated
     */
    public boolean update(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        int               countAffectedRows = 0;
        Connection        con               = null;
        PreparedStatement stmt              = null;

        try {
            String sql = "UPDATE programs SET action = ?"           // --  1 --
                         + ", filename = ?"                         // --  2 --
                         + ", alias = ?"                            // --  3 --
                         + ", parameters_before_filename = ?"       // --  4 --
                         + ", parameters_after_filename = ?"        // --  5 --
                         + ", input_before_execute = ?"             // --  6 --
                         + ", input_before_execute_per_file = ?"    // --  7 --
                         + ", single_file_processing = ?"           // --  8 --
                         + ", change_file = ?"                      // --  9 --
                         + ", sequence_number = ?"                  // -- 10 --
                         + ", use_pattern = ?"                      // -- 11 --
                         + ", pattern = ?"                          // -- 12 --
                         + " WHERE id = ?";                         // -- 13 --

            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(sql);
            setValuesUpdate(stmt, program);
            stmt.setLong(13, program.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();
            notifyUpdated(program);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffectedRows == 1;
    }

    private void setValuesUpdate(PreparedStatement stmt, Program program)
            throws SQLException {
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
     * @param  program program
     * @return         true if deleted
     */
    public boolean delete(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        int               countAffectedRows = 0;
        Connection        con               = null;
        PreparedStatement stmt              = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM programs WHERE id = ?");
            stmt.setLong(1, program.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();

            // Hack because of dirty design of this table (no cascade possible)
            DatabaseActionsAfterDbInsertion.INSTANCE.delete(program);
            notifyDeleted(program);
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
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
     * @param  type program type
     * @return      programs
     */
    public List<Program> getAll(Type type) {
        if (type == null) {
            throw new NullPointerException("type == null");
        }

        List<Program>     programs = new LinkedList<Program>();
        Connection        con      = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(
                getSelectProgramSql(WhereFilter.ACTION));
            stmt.setBoolean(1, type.equals(Type.ACTION));
            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                programs.add(createProgramOfCurrentRecord(rs));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return programs;
    }

    private String getSelectProgramSql(WhereFilter filter) {
        return "SELECT id"                            // --  1 --
               + ", action"                           // --  2 --
               + ", filename"                         // --  3 --
               + ", alias"                            // --  4 --
               + ", parameters_before_filename"       // --  5 --
               + ", parameters_after_filename"        // --  6 --
               + ", input_before_execute"             // --  7 --
               + ", input_before_execute_per_file"    // --  8 --
               + ", single_file_processing"           // --  9 --
               + ", change_file"                      // -- 10 --
               + ", sequence_number"                  // -- 11 --
               + ", use_pattern"                      // -- 12 --
               + ", pattern"                          // -- 13 --
               + " FROM programs" + (filter.equals(WhereFilter.ACTION)
                                     ? " WHERE action = ?"
                                     : filter.equals(WhereFilter.ID)
                                       ? " WHERE id = ?"
                                       : "") + " ORDER BY sequence_number, alias";
    }

    private Program createProgramOfCurrentRecord(ResultSet rs)
            throws SQLException {
        byte[]  parametersBeforeFilename = rs.getBytes(5);
        byte[]  parametersAfterFilename  = rs.getBytes(6);
        byte[]  pattern                  = rs.getBytes(13);
        Program program                  = new Program();

        program.setId(rs.getLong(1));
        program.setAction(rs.getBoolean(2));
        program.setFile(new File(rs.getString(3)));
        program.setAlias(rs.getString(4));
        program.setParametersBeforeFilename((parametersBeforeFilename == null)
                ? null
                : new String(parametersBeforeFilename));
        program.setParametersAfterFilename((parametersAfterFilename == null)
                                           ? null
                                           : new String(
                                           parametersAfterFilename));
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
        return "SELECT id"                            // --  1 --
               + ", action"                           // --  2 --
               + ", filename"                         // --  3 --
               + ", alias"                            // --  4 --
               + ", parameters_before_filename"       // --  5 --
               + ", parameters_after_filename"        // --  6 --
               + ", input_before_execute"             // --  7 --
               + ", input_before_execute_per_file"    // --  8 --
               + ", single_file_processing"           // --  9 --
               + ", change_file"                      // -- 10 --
               + ", sequence_number"                  // -- 11 --
               + ", use_pattern"                      // -- 12 --
               + ", pattern"                          // -- 13 --
               + " FROM programs WHERE action = FALSE AND sequence_number = 0";
    }

    /**
     * Returns the default image open program: The program with the sequence
     * number 0.
     *
     * @return program or null if the database has no program or on errors
     */
    public Program getDefaultImageOpenProgram() {
        Program    program = null;
        Connection con     = null;
        Statement  stmt    = null;
        ResultSet  rs      = null;

        try {
            con = getConnection();

            String sql = getDefaultImageOpenProgramSql();

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                program = createProgramOfCurrentRecord(rs);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return program;
    }

    /**
     * Returns whether a program exists in the database.
     * <p>
     * Programs are treated as equals, if their aliases and filenames are equal.
     *
     * @param  program program
     * @return         true if the program does exist
     */
    public boolean exists(Program program) {
        if (program == null) {
            throw new NullPointerException("program == null");
        }

        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        boolean           exists = false;

        try {
            String sql = "SELECT COUNT(*) FROM programs"
                         + " WHERE alias = ? AND filename = ?";

            con  = getConnection();
            stmt = con.prepareStatement(sql);
            setString(program.getAlias(), stmt, 1);
            setString(program.getFile().getAbsolutePath(), stmt, 2);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getLong(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Returns a program with a specific ID.
     *
     * @param  id  the program's ID
     * @return Program or null if no program has this ID
     */
    public Program find(long id) {
        Program           program = null;
        Connection        con     = null;
        PreparedStatement stmt    = null;
        ResultSet         rs      = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(getSelectProgramSql(WhereFilter.ID));
            stmt.setLong(1, id);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                program = createProgramOfCurrentRecord(rs);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return program;
    }

    /**
     * Returns whether the database contains at least one program (<em>no</em>
     * action).
     *
     * @return true if at least one program (ore more) exists
     */
    public boolean hasProgram() {
        return has(false);
    }

    /**
     * Returns whether the database contains at least one action (<em>no</em>
     * program).
     *
     * @return true if at least one action (ore more) exists
     */
    public boolean hasAction() {
        return has(true);
    }

    private boolean has(boolean action) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement(
                "SELECT COUNT(*) FROM programs WHERE action = " + (action
                    ? "TRUE"
                    : "FALSE"));
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabasePrograms.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }

    public void addListener(DatabaseProgramsListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseProgramsListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyDeleted(Program program) {
        Set<DatabaseProgramsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseProgramsListener listener : listeners) {
                listener.programDeleted(program);
            }
        }
    }

    private void notifyInserted(Program program) {
        Set<DatabaseProgramsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseProgramsListener listener : listeners) {
                listener.programInserted(program);
            }
        }
    }

    private void notifyUpdated(Program program) {
        Set<DatabaseProgramsListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseProgramsListener listener : listeners) {
                listener.programUpdated(program);
            }
        }
    }
}
