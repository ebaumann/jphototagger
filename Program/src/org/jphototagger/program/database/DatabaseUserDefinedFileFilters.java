package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.event.listener.DatabaseUserDefinedFileFiltersListener;
import org.jphototagger.program.event.listener.impl.ListenerSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DatabaseUserDefinedFileFilters extends Database {
    public static final DatabaseUserDefinedFileFilters INSTANCE = new DatabaseUserDefinedFileFilters();
    private final ListenerSupport<DatabaseUserDefinedFileFiltersListener> ls =
        new ListenerSupport<DatabaseUserDefinedFileFiltersListener>();

    private String getInsertSql() {
        return "INSERT INTO user_defined_file_filters" + " (is_not, type, name, expression) VALUES (?, ?, ?, ?)";
    }

    public boolean insert(UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        if (exists(filter.getName())) {
            return update(filter);
        }

        checkFilter(filter, false);

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getInsertSql());
            stmt.setBoolean(1, filter.getIsNot());
            stmt.setInt(2, filter.getType().getValue());
            stmt.setString(3, filter.getName());
            stmt.setString(4, filter.getExpression());
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count == 1) {
                filter.setId(findId(con, filter.getName()));
                notifyInserted(filter);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilters.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count == 1;
    }

    private void checkFilter(UserDefinedFileFilter filter, boolean requiresId) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        if (requiresId && (filter.getId() == null)) {
            throw new IllegalArgumentException("Id is null: " + filter);
        }

        if (!filter.isValid()) {
            throw new IllegalArgumentException("Invalid filter: " + filter);
        }
    }

    private Long findId(Connection con, String name) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Long id = null;

        try {
            String sql = "SELECT id FROM user_defined_file_filters WHERE name = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, name);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                id = rs.getLong(1);
            }
        } finally {
            close(rs, stmt);
        }

        return id;
    }

    private String getUpdateSql() {
        return "UPDATE user_defined_file_filters SET is_not = ?, type = ?,"
                + " name = ?, expression = ? WHERE id = ?";
    }

    public boolean update(UserDefinedFileFilter filter) {
        checkFilter(filter, true);

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getUpdateSql());
            stmt.setBoolean(1, filter.getIsNot());
            stmt.setInt(2, filter.getType().getValue());
            stmt.setString(3, filter.getName());
            stmt.setString(4, filter.getExpression());
            stmt.setLong(5, filter.getId());
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count == 1) {
                notifyUpdated(filter);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilters.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count == 1;
    }

    private String getDeleteSql() {
        return "DELETE FROM user_defined_file_filters WHERE id = ?";
    }

    public boolean delete(UserDefinedFileFilter filter) {
        checkFilter(filter, true);

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getDeleteSql());
            stmt.setLong(1, filter.getId());
            logFiner(stmt);
            count = stmt.executeUpdate();
            con.commit();

            if (count == 1) {
                notifyDeleted(filter);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilters.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count == 1;
    }

    public boolean exists(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT COUNT (*) FROM user_defined_file_filters"
                    + " WHERE name = ?";

            con = getConnection();
            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilters.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }

    public Set<UserDefinedFileFilter> getAll() {
        Set<UserDefinedFileFilter> filter = new LinkedHashSet<UserDefinedFileFilter>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, is_not, type, name, expression FROM"
                         + " user_defined_file_filters ORDER BY name ASC";

            con = getConnection();
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserDefinedFileFilter f = new UserDefinedFileFilter();

                f.setId(getLong(rs, 1));
                f.setIsNot(rs.getBoolean(2));
                f.setType(UserDefinedFileFilter.Type.parseValue(getInt(rs, 3)));
                f.setName(getString(rs, 4));
                f.setExpression(getString(rs, 5));
                filter.add(f);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilters.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return filter;
    }

    public void addListener(DatabaseUserDefinedFileFiltersListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeListener(DatabaseUserDefinedFileFiltersListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    private void notifyInserted(UserDefinedFileFilter filter) {
        for (DatabaseUserDefinedFileFiltersListener listener : ls.get()) {
            listener.filterInserted(filter);
        }
    }

    private void notifyDeleted(UserDefinedFileFilter filter) {
        for (DatabaseUserDefinedFileFiltersListener listener : ls.get()) {
            listener.filterDeleted(filter);
        }
    }

    private void notifyUpdated(UserDefinedFileFilter filter) {
        for (DatabaseUserDefinedFileFiltersListener listener : ls.get()) {
            listener.filterUpdated(filter);
        }
    }

    private DatabaseUserDefinedFileFilters() {}
}
