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
package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.types.Filename;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * A class for preallocating, recycling, and managing
 * JDBC connections.
 * <p />
 * <b>Usage</b><br />
 * ConnectionPool pool = ConnectionPool.getInstance();<br />
 * Connection con = pool.getConnection();<br />
 * Statement st = con.createStatement();<br />
 * ResultSet result = st.executeQuery("select * from users");<br />
 * result.close();
 * st.close();
 * con.close();
 * <p />
 * <b>Configuration</b><br />
 * The configuration properties are located in the file config.properties.
 * <p />
 * webUserImport.mysqlUrl = jdbc:mysql://localhost:3306/typo3<br />
 * webUserImport.mysqlDriver = org.gjt.mm.mysql.Driver<br />
 * webUserImport.mysqlUsername = root<br />
 * webUserImport.mysqlPassword = <br />
 * webUserImport.initialConnections = 3<br />
 * webUserImport.maxConnections = 10<br />
 * webUserImport.waitIfBusy = true;<br />
 * <p />
 * <b>Notice:</b><br />
 * Compatible with J2SE 1.6.0 and above</b>
 * 
 * @version 1.0
 * @since 2005-10-03
 * @author Tobias Stening  
 */
public final class ConnectionPool implements Runnable {

    /**
     * The name of the JDBC-Driver.
     */
    private String driver;
    /**
     * The URL of the database.
     */
    private String url;
    /**
     * The database username.
     */
    private String username;
    /**
     * The database password.
     */
    private String password;
    /**
     * The maximum number of connections.
     */
    private int maxConnections;
    /**
     * Indicates, if the connection pool should wait for
     * a free connection, if all connections are busy.
     */
    private boolean waitIfBusy;
    /**
     * The list of available connections
     */
    private LinkedList<Connection> availableConnections;
    /**
     * Zhe list of busy connections
     */
    private LinkedList<Connection> busyConnections;
    /**
     * 
     */
    private boolean connectionPending = false;
    /**
     * The unique connection poll instance
     */
    private static ConnectionPool instance;

    /**
     * Constructor
     * @throws SQLException
     */
    private ConnectionPool() throws SQLException {
        url = "jdbc:hsqldb:file:" + // NOI18N
                UserSettings.INSTANCE.getDatabaseFileName(
                Filename.FULL_PATH_NO_SUFFIX) +
                ";shutdown=true";  // NOI18N

        driver = "org.hsqldb.jdbcDriver"; // NOI18N
        username = "sa"; // NOI18N
        password = ""; // NOI18N
        int initialConnections = 3;
        maxConnections = 15;
        waitIfBusy = true;

        if (initialConnections > maxConnections) {
            initialConnections = maxConnections;
        }

        busyConnections = new LinkedList<Connection>();
        availableConnections = new LinkedList<Connection>();
        for (int i = 0; i < initialConnections; i++) {
            availableConnections.add(makeNewConnection());
        }
    }

    /**
     * Returns the unique instance of this connection pool.
     * @return The unique instance of this connection pool
     * @throws SQLException
     */
    public static ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    /**
     * This method returns a availabel connection.
     * @return Returns a connection
     * @throws SQLException
     */
    public synchronized Connection getConnection() throws SQLException {
        if (!availableConnections.isEmpty()) {
            Connection existingConnection = availableConnections.getLast();
            availableConnections.removeLast();
            // If connection on available list is closed (e.g.,
            // it timed out), then remove it from available list
            // and repeat the process of obtaining a connection.
            // Also wake up threads that were waiting for a
            // connection because maxConnection limit was reached.
            if (existingConnection.isClosed()) {
                notifyAll(); // Freed up a spot for anybody waiting

                return (getConnection());
            } else {
                busyConnections.add(existingConnection);
                return (existingConnection);
            }
        } else {
            // Three possible cases:
            // 1) You haven't reached maxConnections limit. So
            //    establish one in the background if there isn't
            //    already one pending, then wait for
            //    the next available connection (whether or not
            //    it was the newly established one).
            // 2) You reached maxConnections limit and waitIfBusy
            //    flag is false. Throw SQLException in such a case.
            // 3) You reached maxConnections limit and waitIfBusy
            //    flag is true. Then do the same thing as in second
            //    part of step 1: wait for next available connection.

            if ((totalConnections() < maxConnections) && !connectionPending) {
                makeBackgroundConnection();
            } else if (!waitIfBusy) {
                throw new SQLException("Connection limit reached"); // NOI18N
            }
            // Wait for either a new connection to be established
            // (if you called makeBackgroundConnection) or for
            // an existing connection to be freed up.
            try {
                wait();
            } catch (InterruptedException ie) {
            }
            // Someone freed up a connection, so try again.
            return (getConnection());
        }
    }

    /* NOTICE
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
            connectThread.setName("Connection pool creating connection" + " @ " + // NOI18N
                    getClass().getName());
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
            Connection connection = makeNewConnection();
            synchronized (this) {
                availableConnections.add(connection);
                connectionPending = false;
                notifyAll();
            }
        } catch (Exception e) { // SQLException or OutOfMemory
            // Give up on new connection and wait for existing one
            // to free up.
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
            Connection connection = DriverManager.getConnection(url, username,
                    password);
            return (connection);
        } catch (ClassNotFoundException cnfe) {
            throw new SQLException("Can't find class for driver: " + driver); // NOI18N
        } catch (Exception ce) {
            throw new SQLException("Can't connect to server " + url + "! " + ce); // NOI18N
        }
    }

    /**
     * Releases a specific connection
     * @param connection The connection to be released.
     */
    public synchronized void free(Connection connection) {
        busyConnections.remove(connection);
        availableConnections.add(connection);
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
        closeConnections(availableConnections);
        availableConnections = new LinkedList<Connection>();
        closeConnections(busyConnections);
        busyConnections = new LinkedList<Connection>();
    }

    /**
     * Closes one or more specific connections.
     * @param connections The connections to be closed.
     */
    private void closeConnections(List connections) {
        try {
            for (int i = 0; i < connections.size(); i++) {
                Connection connection = (Connection) connections.get(i);
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
        } catch (SQLException sqle) {
            // Ignore errors; garbage collect anyhow
        }
    }

    /**
     * 
     */
    @Override
    public synchronized String toString() {
        StringBuffer info = new StringBuffer();
        info.append("ConnectionPool(" + url + "," + username + ")"); // NOI18N
        info.append(", available=" + availableConnections.size()); // NOI18N
        info.append(", busy=" + busyConnections.size()); // NOI18N
        info.append(", max=" + maxConnections); // NOI18N
        return info.toString();
    }
}
