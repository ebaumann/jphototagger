package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.openide.util.Lookup;

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

    public static final ConnectionPool INSTANCE = new ConnectionPool();
    private static final Logger LOGGER = Logger.getLogger(ConnectionPool.class.getName());
    private static final int INITIAL_CONNECTIONS = 3;
    private static final int MAX_CONNECTIONS = 15;
    private final LinkedList<Connection> availableConnections = new LinkedList<>();
    private final LinkedList<Connection> busyConnections = new LinkedList<>();
    private final boolean waitForFreeConnectionIfBusy = true;
    private boolean connectionPending = false;
    private volatile boolean init;
    private String driver;
    private String url;
    private String password;
    private String username;

    synchronized boolean isInit() {
        return init;
    }

    synchronized void init() throws SQLException {
        if (init) {
            return;
        }
        setConnectionUrlTokens();
        try {
        for (int i = 0; i < INITIAL_CONNECTIONS; i++) {
            availableConnections.add(makeNewConnection());
        }
        } catch (SQLException e) {
            closeAllConnections();
            throw e;
        }
        init = true;
    }

    private void setConnectionUrlTokens() {
        driver = "org.hsqldb.jdbcDriver";
        url = createUrl();
        username = "sa";
        password = "";
    }

    private String createUrl() {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        String file = provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH_NO_SUFFIX);
        return "jdbc:hsqldb:file:" + file + ";shutdown=true";
    }

    /**
     * This method returns a availabel connection.
     * @return Returns a connection
     * @throws SQLException
     */
    synchronized Connection getConnection() throws SQLException {
        ensureInit();
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
            if ((totalConnections() < MAX_CONNECTIONS) && !connectionPending) {
                makeBackgroundConnection();
            } else if (!waitForFreeConnectionIfBusy) {
                throw new SQLException("Connection limit reached");
            }
            // Wait for either a new connection to be established
            // (if you called makeBackgroundConnection) or for
            // an existing connection to be freed up.
            while (availableConnections.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
            // Someone freed up a connection, so try again.
            return (getConnection());
        }
    }

    private void ensureInit() {
        if (!init) {
            throw new IllegalStateException("Connection is not established (init == false)!");
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
            Thread connectThread = new Thread(this, "JPhotoTagger: Making background JDBC connection");

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
            LOGGER.log(Level.SEVERE, null, ex);
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
            Connection con = DriverManager.getConnection(url, username, password);
            return (con);
        } catch (ClassNotFoundException cnfe) {
            throw new SQLException("Can't find class for driver: " + driver);
        } catch (Exception ce) {
            throw new SQLException("Can't connect to server " + url + "! " + ce);
        }
    }

    /**
     * Releases a specific connection
     * @param con The connection to be released.
     */
    synchronized void free(Connection con) {
        ensureInit();
        if (con == null) {
            throw new NullPointerException("con == null");
        }
        busyConnections.remove(con);
        availableConnections.add(con);
        // Wake up threads that are waiting for a connection
        notifyAll();
    }

    /**
     * Returns the number of total connections in this connection pool.
     * @return The number of connections in this connection pool.
     */
    private synchronized int totalConnections() {
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
    synchronized void closeAllConnections() {
        closeConnections(availableConnections);
        availableConnections.clear();
        closeConnections(busyConnections);
        busyConnections.clear();
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

    @Override
    public synchronized String toString() {
        StringBuilder info = new StringBuilder();
        info.append("ConnectionPool(")
                .append(url)
                .append(",")
                .append(username)
                .append(")")
                .append(", available=")
                .append(availableConnections.size())
                .append(", busy=")
                .append(busyConnections.size())
                .append(", max=").append(MAX_CONNECTIONS);
        return info.toString();
    }

    void setShutdown() {
        init = false;
    }

    private ConnectionPool() {
}
}
