/*
 * @(#)ConnectionPool.java    Created 2005-10-03
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
import org.jphototagger.program.types.Filename;
import org.jphototagger.program.UserSettings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.LinkedList;
import java.util.List;

/**
 * A class for preallocating, recycling, and managing
 * JDBC connections.
 * <p />
 * <b>Usage</b>
 * <pre>
 * ConnectionPool pool = ConnectionPool.getInstance();
 * Connection con = pool.getConnection();
 * Statement st = con.createStatement();
 * ResultSet result = st.executeQuery("select * from users");
 * result.close();
 * st.close();
 * pool.free(con.close());
 * </pre>
 * <p />
 * <b>Notice:</b><br />
 * Compatible with J2SE 1.6.0 and above</b>
 *
 * @author Tobias Stening
 */
public final class ConnectionPool implements Runnable {
    public static final ConnectionPool INSTANCE          = new ConnectionPool();
    private boolean                    connectionPending = false;

    /**
     * The list of available connections
     */
    private LinkedList<Connection> availableConnections;

    /**
     * The list of busy connections
     */
    private LinkedList<Connection> busyConnections;

    /**
     * The name of the JDBC-Driver.
     */
    private String           driver;
    private volatile boolean init;

    /**
     * The maximum number of connections.
     */
    private static final int MAX_CONNECTIONS = 15;

    /**
     * Number of initial connections.
     */
    private static final int INITIAL_CONNECTIONS = 3;

    /**
     * The database password.
     */
    private String password;

    /**
     * The URL of the database.
     */
    private String url;

    /**
     * The database username.
     */
    private String username;

    /**
     * Indicates, if the connection pool should wait for
     * a free connection, if all connections are busy.
     */
    private boolean waitIfBusy;

    private ConnectionPool() {}

    public synchronized void init() throws SQLException {
        if (init) {
            assert false;

            return;
        }

        init = true;

        String file = UserSettings.INSTANCE.getDatabaseFileName(
                          Filename.FULL_PATH_NO_SUFFIX);

        url      = "jdbc:hsqldb:file:" + file + ";shutdown=true";
        driver   = "org.hsqldb.jdbcDriver";
        username = "sa";
        password = "";

        waitIfBusy = true;

        busyConnections      = new LinkedList<Connection>();
        availableConnections = new LinkedList<Connection>();

        for (int i = 0; i < INITIAL_CONNECTIONS; i++) {
            availableConnections.add(makeNewConnection());
        }
    }

    /**
     * This method returns a availabel connection.
     * @return Returns a connection
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        assert init;

        if (!availableConnections.isEmpty()) {
            Connection existingConnection = availableConnections.pollLast();

            // If connection on available list is closed (e.g.,
            // it timed out), then remove it from available list
            // and repeat the process of obtaining a connection.
            // Also wake up threads that were waiting for a
            // connection because maxConnection limit was reached.
            if (existingConnection.isClosed()) {
                notifyAll();    // Freed up a spot for anybody waiting

                return (getConnection());
            } else {
                busyConnections.add(existingConnection);

                return (existingConnection);
            }
        } else {

            // Three possible cases:
            // 1) You haven't reached maxConnections limit. So
            // establish one in the background if there isn't
            // already one pending, then wait for
            // the next available connection (whether or not
            // it was the newly established one).
            // 2) You reached maxConnections limit and waitIfBusy
            // flag is false. Throw SQLException in such a case.
            // 3) You reached maxConnections limit and waitIfBusy
            // flag is true. Then do the same thing as in second
            // part of step 1: wait for next available connection.
            if ((totalConnections() < MAX_CONNECTIONS) &&!connectionPending) {
                makeBackgroundConnection();
            } else if (!waitIfBusy) {
                throw new SQLException("Connection limit reached");
            }

            // Wait for either a new connection to be established
            // (if you called makeBackgroundConnection) or for
            // an existing connection to be freed up.
            while (availableConnections.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            }

            // Someone freed up a connection, so try again.
            return (getConnection());
        }
    }

    /*
     *  NOTICE
     *
     * You can't just make a new connection in the foreground
     * when none are available, since this can take several
     * seconds with a slow network connection. Instead,
     * start a thread that establishes a new connection,
     * then wait. You get woken up either when the new connection
     * is established or if someone finishes with an existing
     * connection.
     */

    /**
     * This method starts a thread for creating a connection
     * in the background.
     */
    private void makeBackgroundConnection() {
        connectionPending = true;

        try {
            Thread connectThread = new Thread(this);

            connectThread.setName("Connection pool creating connection @ "
                                  + getClass().getSimpleName());
            connectThread.start();
        } catch (OutOfMemoryError oome) {

            // Give up on new connection
        }
    }

    /**
     * This method is the main method of this Thread.
     * It creates a connection and makes it available.
     */
    @Override
    public void run() {
        try {
            Connection con = makeNewConnection();

            synchronized (this) {
                availableConnections.add(con);
                connectionPending = false;
                notifyAll();
            }
        } catch (SQLException ex) {    // SQLException

            // Give up on new connection and wait for existing one
            // to free up.
            AppLogger.logSevere(getClass(), ex);
        }
    }

    /**
     * This explicitly makes a new connection. Called in
     * the foreground when initializing the ConnectionPool,
     * and called in the background when running.
     */
    private Connection makeNewConnection() throws SQLException {
        try {

            // Load database driver if not already loaded
            Class.forName(driver);

            // Establish network connection to database
            Connection con = DriverManager.getConnection(url, username,
                                 password);

            return (con);
        } catch (ClassNotFoundException cnfe) {
            throw new SQLException("Can't find class for driver: " + driver);
        } catch (Exception ce) {
            throw new SQLException("Can't connect to server " + url + "! "
                                   + ce);
        }
    }

    /**
     * Releases a specific connection
     * @param con The connection to be released.
     */
    public synchronized void free(Connection con) {
        if (con == null) {
            throw new NullPointerException("con == null");
        }

        assert init;

        busyConnections.remove(con);
        availableConnections.add(con);

        // Wake up threads that are waiting for a connection
        notifyAll();
    }

    /**
     * Returns the number of total connections in this connection pool.
     * @return The number of connections in this connection pool.
     */
    public synchronized int totalConnections() {
        return (availableConnections.size() + busyConnections.size());
    }

    /**
     * Close all the connections. Use with caution:
     *  be sure no connections are in use before
     *  calling. Note that you are not <I>required</I> to
     *  call this when done with a ConnectionPool, since
     *  connections are guaranteed to be closed when
     *  garbage collected. But this method gives more control
     *  regarding when the connections are closed.
     */
    public synchronized void closeAllConnections() {
        assert init;

        closeConnections(availableConnections);
        availableConnections = new LinkedList<Connection>();
        closeConnections(busyConnections);
        busyConnections = new LinkedList<Connection>();
    }

    /**
     * Closes one or more specific connections.
     * @param connections The connections to be closed.
     */
    private void closeConnections(List<Connection> connections) {
        try {
            for (int i = 0; i < connections.size(); i++) {
                Connection con = connections.get(i);

                if (!con.isClosed()) {
                    con.close();
                }
            }
        } catch (Exception sqle) {

            // Ignore errors; garbage collect anyhow
        }
    }

    /**
     *
     */
    @Override
    public synchronized String toString() {
        StringBuilder info = new StringBuilder();

        info.append("ConnectionPool(" + url + "," + username + ")");
        info.append(", available=" + availableConnections.size());
        info.append(", busy=" + busyConnections.size());
        info.append(", max=" + MAX_CONNECTIONS);

        return info.toString();
    }
}
