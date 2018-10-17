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
    private static final int DEFAULT_SIZE = 8;
    private final int size;
    private String url;
    private String password = "";
    private String username = "sa";
    private Properties properties;
    private volatile boolean init;
    private JDBCPool pool;

    /**
     * Creates a pool with the default size
     */
    public HsqlDbConnectionPool() {
        this(DEFAULT_SIZE);
    }

    /**
     * @param size Default: 8
     */
    public HsqlDbConnectionPool(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("Pool size less than 1: " + size);
        }
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

        // DO NOT DELETE: If this property is not set, HSQLDB removes all
        // existing log handlers, esp. JPhotoTagger's log handlers.
        System.setProperty("hsqldb.reconfig_logging", "false");

        pool = new JDBCPool(size);
        pool.setUrl(url);
        pool.setPassword(password);
        pool.setUser(username);
        setProperties();
        LOGGER.log(Level.INFO, "Created database connection pool. URL: {0}, Username: {1}", new Object[]{url, username});
    }

    private void setProperties() {
        if (properties != null) {
            pool.setProperties(properties);
        }
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
     * If not null, these Connection properties will used. has to be called
     * before {@link #init()}.
       *
     * @param properties See HSQLDB documentation
     */
    public void setProperties(Properties properties) {
        this.properties = new Properties(properties);
    }

    /**
     * Sets the database URL and has to be called before {@link #init()}.
     *
     * @param url Database URL, see {@link #createFileUrl(java.lang.String)}
     */
    public void setUrl(String url) {
        this.url = Objects.requireNonNull(url, "url == null");
    }

    /**
     * Sets an <em>optional</em> user's database password and has to be called
     * before {@link #init()}.
     *
     * @param password Default: ""
     */
    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password == null");
    }

    /**
     * Sets an <em>optional</em> user's name and has to be called before
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
        return pool.getConnection();
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
        try {
            con.close();
        } catch (Throwable t) {
            Logger.getLogger(HsqlDbConnectionPool.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    /**
     * Closes all connections.
     */
    synchronized void closeAllConnections() {
        try {
            pool.close(1);
            LOGGER.info("Closed database connection pool");
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, "Error while closing connection pool", t);
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
