package org.jphototagger.repository.hsqldb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hsqldb.jdbc.JDBCPool;

/**
 * Connection pool for a HSQLDB (http://hsqldb.org/) database.
 * <p>
 * Usage:
 * <pre>
 * HsqlDbConnectionPool pool = new HsqlDbConnectionPool();
 * pool.setUrl(HsqlDbConnectionPool.createFileUrl("/home/me/databases/myapp"));
 * pool.init();
 * Connection con = pool.getConnection();
 * ...
 * pool.free(con);
 * </pre>
 *
 * @author Elmar Baumann
 */
public final class HsqlDbConnectionPool {

    private static final Logger LOGGER = Logger.getLogger(HsqlDbConnectionPool.class.getName());
    private final int size;
    private final String driver = "org.hsqldb.jdbcDriver";
    private String url;
    private String password = "";
    private String username = "sa";
    private volatile boolean init;
    private JDBCPool pool;
    private Connection con = null;

    /**
     * Creates a pool with the default size
     */
    public HsqlDbConnectionPool() {
        this(8);
    }

    /**
     * @param size Default: 8
     */
    public HsqlDbConnectionPool(int size) {
        this.size = size;
    }

    synchronized boolean isInit() {
        return init;
    }

    synchronized void init() throws SQLException {
        if (init) {
            return;
        }
        try {
            createPool();
        } catch (Throwable t) {
            closeAllConnections();
            throw t;
        }
        init = true;
    }

    private void createPool() {
        LOGGER.log(Level.INFO, "Creating database connection pool");

        // If this property is not set, HSQLDB removes all existing log handlers
        // i.e. JPhotoTagger's log handlers.
        System.setProperty("hsqldb.reconfig_logging", "false");

        pool = new JDBCPool(size);
        pool.setUrl(url);
        pool.setPassword(password);
        pool.setUser(username);
        setProperties();
        LOGGER.log(Level.INFO, "Created database connection pool. Driver: {0}, URL: {1}, Username: {2}", new Object[]{driver, url, username});
    }

    private void setProperties() {
        Properties props = new Properties();
        props.put("hsqldb.applog", "3");
        pool.setProperties(props);
    }

    /**
     * Creates an URL for a specific database file.
     *
     * @param filePath absolute path to an existing database file. If the
     *                 directory exists but not the database file, it will be
     *                 created
     *
     * @return URL for a HSQLDB file (automatic shutdown)
     */
    public static String createFileUrl(String filePath) {
        Objects.requireNonNull(filePath, "filePath == null");

        return "jdbc:hsqldb:file:" + filePath;
    }

    /**
     * Sets the database URL and have to be called before {@link #init()}.
     *
     * @param url Database URL, see {@link #createFileUrl(java.lang.String)}
     */
    public void setUrl(String url) {
        this.url = Objects.requireNonNull(url, "url == null");
    }

    /**
     * Sets an <em>optional</em> user's database password and have to be called
     * before {@link #init()}.
     *
     * @param password Default: ""
     */
    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password == null");
    }

    /**
     * Sets an <em>optional</em> user's name and have to be called before
     * {@link #init()}.
     *
     * @param username Default: "sa"
     */
    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username == null");
    }

    /**
     * This method returns an available connection.
     *
     * @return Returns a connection
     *
     * @throws SQLException
     */
    synchronized Connection getConnection() throws SQLException {
        ensureInit();
        if (con == null) {
            con = pool.getConnection();
        }
        return con;
    }

    private synchronized void ensureInit() {
        if (!init) {
            LOGGER.severe("Database has not been initialized");
            throw new IllegalStateException("Connection is not established (init == false)!");
        }
    }

    /**
     * Releases a specific connection.
     *
     * @param con The connection to be released.
     */
    synchronized void free(Connection con) {
        // Nothing to do
    }

    /**
     * Closes all connections.
     */
    synchronized void closeAllConnections() {
        try {
            pool.close(1);
            LOGGER.info("Closed database connection pool");
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Error while closing pool", t);
        }
    }

    @Override
    public synchronized String toString() {
        StringBuilder info = new StringBuilder();
        info.append("HsqlDbConnectionPool")
                .append("(")
                .append("URL: ")
                .append(url)
                .append(", username: ")
                .append(username)
                .append(", size: ")
                .append(size)
                .append(")")
                ;
        return info.toString();
    }

    void setShutdown() {
        init = false;
    }
}
