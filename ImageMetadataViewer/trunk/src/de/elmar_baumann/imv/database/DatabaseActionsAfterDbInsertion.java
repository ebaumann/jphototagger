package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.resource.Bundle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains (links to) external Programs to execute after inserting metadata
 * into the database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/07
 */
public final class DatabaseActionsAfterDbInsertion extends Database {

    public static final DatabaseActionsAfterDbInsertion INSTANCE = new DatabaseActionsAfterDbInsertion();

    private DatabaseActionsAfterDbInsertion() {
    }

    /**
     * Inserts a new action. Prevoius You should call {@link #hasProgram()}.
     * 
     * @param action  action
     * @param order   order of the action
     * @return true if inserted
     */
    public boolean insert(Program action, int order) {
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO actions_after_db_insertion" + // NOI18N
                    " (" + // NOI18N
                    "id_programs" + // NOI18N -- 1 --
                    ", action_order" + // NOI18N -- 2 --
                    ")" + // NOI18N
                    " VALUES (?, ?)"); // NOI18N
            stmt.setLong(1, action.getId());
            stmt.setInt(2, order);
            AppLog.logFiner(DatabaseActionsAfterDbInsertion.class,
                    stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(connection);
        } finally {
            free(connection);
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
        int countAffectedRows = 0;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM actions_after_db_insertion WHERE id_programs = ?"); // NOI18N
            stmt.setLong(1, action.getId());
            AppLog.logFiner(DatabaseActionsAfterDbInsertion.class,
                    stmt.toString());
            countAffectedRows = stmt.executeUpdate();
            connection.commit();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(connection);
        } finally {
            free(connection);
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
        Connection connection = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "SELECT" +
                    " id_programs" + // NOI18N -- 1 --
                    " FROM actions_after_db_insertion" + // NOI18N
                    " ORDER BY action_order ASC"; // NOI18N
            AppLog.logFinest(DatabaseActionsAfterDbInsertion.class, sql);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long idProgram = rs.getLong(1);
                Program program = DatabasePrograms.INSTANCE.getProgram(idProgram);
                if (program == null) {
                    AppLog.logWarning(DatabaseActionsAfterDbInsertion.class,
                            Bundle.getString(
                            "DatabaseActionsAfterDbInsertion.ProgramDoesNotExist",
                            idProgram));
                } else {
                    programs.add(program);
                }
            }
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseActionsAfterDbInsertion.class, ex);
        } finally {
            free(connection);
        }
        return programs;
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
    public boolean reorder(List<Program> actions, int startIndex) {
        Connection connection = null;
        boolean allReordered = false;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE actions_after_db_insertion" + // NOI18N
                    " SET" + // NOI18N
                    " action_order = ?" + // NOI18N -- 1 --
                    " WHERE id_programs = ?"); // NOI18N
            int index = startIndex;
            int countAffected = 0;
            for (Program action : actions) {
                stmt.setInt(1, index++);
                stmt.setLong(2, action.getId());
                AppLog.logFiner(DatabaseActionsAfterDbInsertion.class, stmt.
                        toString());
                countAffected += stmt.executeUpdate();
            }
            connection.commit();
            allReordered = countAffected == actions.size();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logWarning(DatabaseActionsAfterDbInsertion.class, ex);
            rollback(connection);
        } finally {
            free(connection);
        }
        return allReordered;
    }
}
