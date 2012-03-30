package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;

/**
 * @author Elmar Baumann
 */
final class UserDefinedFileFiltersDatabase extends Database {

    static final UserDefinedFileFiltersDatabase INSTANCE = new UserDefinedFileFiltersDatabase();
    private static final Logger LOGGER = Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName());

    private String getInsertSql() {
        return "INSERT INTO user_defined_file_filters" + " (is_not, type, name, expression) VALUES (?, ?, ?, ?)";
    }

    boolean insertUserDefinedFileFilter(UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }
        if (existsUserDefinedFileFilter(filter.getName())) {
            return updateUserDefinedFileFilter(filter);
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
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count == 1) {
                filter.setId(findId(con, filter.getName()));
                notifyInserted(filter);
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName()).log(Level.SEVERE, null, ex);
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
            LOGGER.log(Level.FINEST, stmt.toString());
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

    boolean updateUserDefinedFileFilter(UserDefinedFileFilter filter) {
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
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count == 1) {
                notifyUpdated(filter);
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName()).log(Level.SEVERE, null, ex);
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

    boolean deleteUserDefinedFileFilter(UserDefinedFileFilter filter) {
        checkFilter(filter, true);
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(getDeleteSql());
            stmt.setLong(1, filter.getId());
            LOGGER.log(Level.FINER, stmt.toString());
            count = stmt.executeUpdate();
            con.commit();
            if (count == 1) {
                notifyDeleted(filter);
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName()).log(Level.SEVERE, null, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }
        return count == 1;
    }

    boolean existsUserDefinedFileFilter(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT COUNT (*) FROM user_defined_file_filters WHERE name = ?";
            con = getConnection();
            stmt = con.prepareStatement(sql);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return count > 0;
    }

    Set<UserDefinedFileFilter> getAllUserDefinedFileFilters() {
        Set<UserDefinedFileFilter> filter = new LinkedHashSet<UserDefinedFileFilter>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT id, is_not, type, name, expression FROM"
                    + " user_defined_file_filters ORDER BY name ASC";
            con = getConnection();
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
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
            Logger.getLogger(UserDefinedFileFiltersDatabase.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }
        return filter;
    }

    private void notifyInserted(UserDefinedFileFilter filter) {
        EventBus.publish(new UserDefinedFileFilterInsertedEvent(this, filter));
    }

    private void notifyDeleted(UserDefinedFileFilter filter) {
        EventBus.publish(new UserDefinedFileFilterDeletedEvent(this, filter));
    }

    private void notifyUpdated(UserDefinedFileFilter filter) {
        EventBus.publish(new UserDefinedFileFilterUpdatedEvent(this, filter));
    }

    private UserDefinedFileFiltersDatabase() {
    }
}
