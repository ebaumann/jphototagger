/*
 * @(#)DatabaseActionsAfterDbInsertion.java    2009-06-07
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

package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Program;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains (links to) external Programs to execute after inserting metadata
 * into the database.
 *
 * @author  Elmar Baumann
 */
public final class DatabaseActionsAfterDbInsertion extends Database {
    public static final DatabaseActionsAfterDbInsertion INSTANCE =
        new DatabaseActionsAfterDbInsertion();

    private DatabaseActionsAfterDbInsertion() {}

    /**
     * Inserts a new action. Prevoius You should call
     * {@link DatabasePrograms#hasProgram()}.
     *
     * @param action  action
     * @param order   order of the action
     * @return true if inserted
     */
    public boolean insert(Program action, int order) {
        int               countAffectedRows = 0;
        Connection        con               = null;
        PreparedStatement stmt              = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "INSERT INTO actions_after_db_insertion"
                + " (id_programs, action_order) VALUES (?, ?)");
            stmt.setLong(1, action.getId());
            stmt.setInt(2, order);
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffectedRows == 1;
    }

    /**
     * Deletes an action. <em>The ID must exist!</em>
     *
     * @param  action  action to delete
     * @return true if deleted
     */
    public boolean delete(Program action) {
        int               countAffectedRows = 0;
        Connection        con               = null;
        PreparedStatement stmt              = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "DELETE FROM actions_after_db_insertion WHERE id_programs = ?");
            stmt.setLong(1, action.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffectedRows == 1;
    }

    /**
     * Returns all Actions ordered by their aliases.
     *
     * @return programs sorted ascending by their order
     */
    public List<Program> getAll() {
        List<Program> programs = new LinkedList<Program>();
        Connection    con      = null;
        Statement     stmt     = null;
        ResultSet     rs       = null;

        try {
            con  = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT id_programs FROM actions_after_db_insertion"
                         + " ORDER BY action_order ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                long    idProgram = rs.getLong(1);
                Program program   = DatabasePrograms.INSTANCE.find(idProgram);

                if (program == null) {
                    AppLogger.logWarning(
                        getClass(),
                        "DatabaseActionsAfterDbInsertion.ProgramDoesNotExist",
                        idProgram);
                } else {
                    programs.add(program);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseActionsAfterDbInsertion.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return programs;
    }

    /**
     * Returns whether an action exists in this database.
     *
     * @param  action  action
     * @return true if the action exists
     */
    public boolean exists(Program action) {
        boolean           exists = false;
        Connection        con    = null;
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) "
                                        + " FROM actions_after_db_insertion"
                                        + " WHERE id_programs = ?");
            stmt.setLong(1, action.getId());
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseAutoscanDirectories.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    /**
     * Sets the order of the actions.
     *
     * @param actions     actions: the order in the list is the action's new
     *                    order
     * @param startIndex  index of the first action. The index of the other
     *                    actions is this index plus their list index
     * @return            true if reordered all actions
     */
    public boolean setOrder(List<Program> actions, int startIndex) {
        Connection        con          = null;
        boolean           allReordered = false;
        PreparedStatement stmt         = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
                "UPDATE actions_after_db_insertion SET action_order = ?"
                + " WHERE id_programs = ?");

            int index         = startIndex;
            int countAffected = 0;

            for (Program action : actions) {
                stmt.setInt(1, index++);
                stmt.setLong(2, action.getId());
                logFiner(stmt);
                countAffected += stmt.executeUpdate();
            }

            con.commit();
            allReordered = countAffected == actions.size();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return allReordered;
    }
}
