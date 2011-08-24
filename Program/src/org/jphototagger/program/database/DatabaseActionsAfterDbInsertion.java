package org.jphototagger.program.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.event.listener.DatabaseActionsAfterDbInsertionListener;

/**
 * Contains (links to) external Programs to execute after inserting metadata
 * into the database.
 *
 * @author Elmar Baumann
 */
public final class DatabaseActionsAfterDbInsertion extends Database {
    private final ListenerSupport<DatabaseActionsAfterDbInsertionListener> ls = new ListenerSupport<DatabaseActionsAfterDbInsertionListener>();
    private static final Logger LOGGER = Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName());
    public static final DatabaseActionsAfterDbInsertion INSTANCE = new DatabaseActionsAfterDbInsertion();

    private DatabaseActionsAfterDbInsertion() {}

    /**
     * Inserts a new action. Prevoius You should call
     * {@link DatabasePrograms#hasProgram()}.
     *
     * @param program action
     * @param order   order of the action
     * @return        true if inserted
     */
    public boolean insert(Program program, int order) {
        if (program == null) {
            throw new NullPointerException("action == null");
        }

        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO actions_after_db_insertion"
                                        + " (id_program, action_order) VALUES (?, ?)");
            stmt.setLong(1, program.getId());
            stmt.setInt(2, order);
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();

            if (countAffectedRows > 0) {
                notifyInserted(program);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return countAffectedRows == 1;
    }

    /**
     * Deletes an action. <em>The ID {@link Program#getId()} must exist!</em>
     *
     * @param  program action to delete
     * @return         true if deleted
     */
    public boolean delete(Program program) {
        if (program == null) {
            throw new NullPointerException("action == null");
        }

        int countAffectedRows = 0;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("DELETE FROM actions_after_db_insertion WHERE id_program = ?");
            stmt.setLong(1, program.getId());
            logFiner(stmt);
            countAffectedRows = stmt.executeUpdate();
            con.commit();

            if (countAffectedRows > 0) {
                notifyDeleted(program);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
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
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.createStatement();

            String sql = "SELECT id_program FROM actions_after_db_insertion ORDER BY action_order ASC";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                long idProgram = rs.getLong(1);
                Program program = DatabasePrograms.INSTANCE.find(idProgram);

                if (program == null) {
                    LOGGER.log(Level.WARNING,
                            "Error getting an action to start after insertion of metadata into the database: The programm whith the ID {0} not exist!",
                            idProgram);
                } else {
                    programs.add(program);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
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
        if (action == null) {
            throw new NullPointerException("action == null");
        }

        boolean exists = false;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            stmt = con.prepareStatement("SELECT COUNT(*) FROM actions_after_db_insertion"
                                        + " WHERE id_program = ?");
            stmt.setLong(1, action.getId());
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return exists;
    }

    public int getCount() {
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();

            String sql = "SELECT COUNT(*) FROM actions_after_db_insertion";

            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count;
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
        if (actions == null) {
            throw new NullPointerException("actions == null");
        }

        Connection con = null;
        boolean allReordered = false;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE actions_after_db_insertion SET action_order = ?"
                                        + " WHERE id_program = ?");

            int index = startIndex;
            int countAffected = 0;

            for (Program action : actions) {
                stmt.setInt(1, index++);
                stmt.setLong(2, action.getId());
                logFiner(stmt);
                countAffected += stmt.executeUpdate();
            }

            con.commit();
            allReordered = countAffected == actions.size();

            if (allReordered) {
                notifyReordered();
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseActionsAfterDbInsertion.class.getName()).log(Level.SEVERE, null, ex);
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return allReordered;
    }

    public void addListener(DatabaseActionsAfterDbInsertionListener listener) {
        ls.add(listener);
    }

    public void removeListener(DatabaseActionsAfterDbInsertionListener listener) {
        ls.remove(listener);
    }

    private void notifyInserted(Program program) {
        for (DatabaseActionsAfterDbInsertionListener listener : ls.get()) {
            listener.programInserted(program);
        }
    }

    private void notifyDeleted(Program program) {
        for (DatabaseActionsAfterDbInsertionListener listener : ls.get()) {
            listener.programDeleted(program);
        }
    }

    private void notifyReordered() {
        for (DatabaseActionsAfterDbInsertionListener listener : ls.get()) {
            listener.programsReordered();
        }
    }
}
