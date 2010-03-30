/*
 * @(#)DatabaseUserDefinedFileFilter.java    Created on 2010-03-30
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
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.event.listener
    .DatabaseUserDefinedFileFilterListener;
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
public final class DatabaseUserDefinedFileFilter extends Database {
    public static final DatabaseUserDefinedFileFilter INSTANCE =
        new DatabaseUserDefinedFileFilter();
    private final ListenerSupport<DatabaseUserDefinedFileFilterListener> ls =
        new ListenerSupport<DatabaseUserDefinedFileFilterListener>();

    private String getInsertSql() {
        return "INSERT INTO user_defined_file_filter"
               + " (is_not, type, name, expression) VALUES (?, ?, ?, ?)";
    }

    public boolean insert(UserDefinedFileFilter filter) {
        if (exists(filter.getName())) {
            return update(filter);
        }

        checkFilter(filter, false);

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

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
            AppLogger.logSevere(DatabaseUserDefinedFileFilter.class, ex);
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
        ResultSet         rs   = null;
        Long              id   = null;

        try {
            String sql =
                "SELECT id FROM user_defined_file_filter WHERE name = ?";

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
        return "UPDATE user_defined_file_filter SET is_not = ?, type = ?,"
               + " name = ?, expression = ? WHERE id = ?";
    }

    public boolean update(UserDefinedFileFilter filter) {
        checkFilter(filter, true);

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

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
            AppLogger.logSevere(DatabaseUserDefinedFileFilter.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count == 1;
    }

    private String getDeleteSql() {
        return "DELETE FROM user_defined_file_filter WHERE id = ?";
    }

    public boolean delete(UserDefinedFileFilter filter) {
        checkFilter(filter, true);

        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;

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
            AppLogger.logSevere(DatabaseUserDefinedFileFilter.class, ex);
            count = 0;
            rollback(con);
        } finally {
            close(stmt);
            free(con);
        }

        return count == 1;
    }

    public boolean exists(String name) {
        int               count = 0;
        Connection        con   = null;
        PreparedStatement stmt  = null;
        ResultSet         rs    = null;

        try {
            String sql = "SELECT COUNT (*) FROM user_defined_file_filter"
                         + " WHERE name = ?";

            con  = getConnection();
            stmt = con.prepareStatement(sql);
            logFinest(stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilter.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return count > 0;
    }

    public Set<UserDefinedFileFilter> getAll() {
        Set<UserDefinedFileFilter> filter =
            new LinkedHashSet<UserDefinedFileFilter>();
        Connection con  = null;
        Statement  stmt = null;
        ResultSet  rs   = null;

        try {
            String sql = "SELECT id, is_not, type, name, expression FROM"
                         + " user_defined_file_filter ORDER BY name ASC";

            con = getConnection();
            stmt = con.createStatement();
            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                UserDefinedFileFilter f = new UserDefinedFileFilter();

                f.setId(getLong(rs, 1));
                f.setIsNot(getBoolean(rs, 2));
                f.setType(UserDefinedFileFilter.Type.parseValue(getInt(rs, 3)));
                f.setName(getString(rs, 4));
                f.setExpression(getString(rs, 5));
                filter.add(f);
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseUserDefinedFileFilter.class, ex);
        } finally {
            close(rs, stmt);
            free(con);
        }

        return filter;
    }

    public void addListener(DatabaseUserDefinedFileFilterListener listener) {
        ls.add(listener);
    }

    public void removeListener(DatabaseUserDefinedFileFilterListener listener) {
        ls.remove(listener);
    }

    private void notifyInserted(UserDefinedFileFilter filter) {
        Set<DatabaseUserDefinedFileFilterListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseUserDefinedFileFilterListener listener : listeners) {
                listener.filterInserted(filter);
            }
        }
    }

    private void notifyDeleted(UserDefinedFileFilter filter) {
        Set<DatabaseUserDefinedFileFilterListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseUserDefinedFileFilterListener listener : listeners) {
                listener.filterDeleted(filter);
            }
        }
    }

    private void notifyUpdated(UserDefinedFileFilter filter) {
        Set<DatabaseUserDefinedFileFilterListener> listeners = ls.get();

        synchronized (listeners) {
            for (DatabaseUserDefinedFileFilterListener listener : listeners) {
                listener.filterUpdated(filter);
            }
        }
    }

    private DatabaseUserDefinedFileFilter() {}
}
