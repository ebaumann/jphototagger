package org.jphototagger.program.app.update.tables;

import org.jphototagger.lib.util.Version;
import org.jphototagger.program.app.update.tables.v0.UpdateTablesV0;
import org.jphototagger.program.database.DatabaseMetadata;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.core.Storage;
import org.openide.util.Lookup;

/**
 * Creates updaters for updating an older database version and let them updatePostCreation
 * the database.
 *
 * @author Elmar Baumann
 */
public final class UpdateTablesFactory {

    public static final UpdateTablesFactory INSTANCE = new UpdateTablesFactory();
    private final List<Updater> allUpdaters = new ArrayList<Updater>();
    private final List<Updater> runningUpdaters = new ArrayList<Updater>();

    private UpdateTablesFactory() {
        initAllUpdaters();
        addUpdaters();
    }

    private void initAllUpdaters() {
        allUpdaters.add(new UpdateTablesV0());
    }

    private void addUpdaters() {
        boolean force = isForceUpdate();

        for (Updater updater : allUpdaters) {
            if (force || isRunUpdaterOfMajorVersion(updater.getMajorVersion())) {
                runningUpdaters.add(updater);
            }
        }
    }

    public void updatePreCreation(Connection con) throws SQLException {
        if (isUpdate()) {
            Level defaultLogLevel = getLogLevel();

            setLogLevel(Level.FINEST);

            try {
                for (Updater updater : runningUpdaters) {
                    updater.updatePreCreation(con);
                }
            } finally {
                setLogLevel(defaultLogLevel);
            }
        }
    }

    private Level getLogLevel() {
        Level level = null;
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        if (storage.containsKey(Storage.KEY_LOG_LEVEL)) {
            String levelString = storage.getString(Storage.KEY_LOG_LEVEL);

            try {
                level = Level.parse(levelString);
            } catch (Exception ex) {
                Logger.getLogger(UpdateTablesFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (level == null) {
            storage.setString(Storage.KEY_LOG_LEVEL, Level.INFO.getLocalizedName());
        }

        return level == null ? Level.INFO : level;
    }

    private void setLogLevel(Level logLevel) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setString(Storage.KEY_LOG_LEVEL, logLevel.toString());
    }

    public void updatePostCreation(Connection con) throws SQLException {
        if (isUpdate()) {
            Level defaultLogLevel = getLogLevel();

            setLogLevel(Level.FINEST);

            try {
                for (Updater updater : runningUpdaters) {
                    updater.updatePostCreation(con);
                }

                DatabaseMetadata.setCurrentAppVersionToDatabase();
            } finally {
                setLogLevel(defaultLogLevel);
            }
        }
    }

    private static boolean isRunUpdaterOfMajorVersion(int version) {
        String dbVersion = DatabaseMetadata.getDatabaseAppVersion();

        /*
         *  dbVersion == null: Only versions prior to 0.8.3 do not write version
         * info into the database. In that case, every upater has to be created.
         */
        return (dbVersion == null)
                ? true
                : version >= Version.parseVersion(dbVersion, ".").getMajor();
    }

    /**
     * Returns, whether a database updatePostCreation shall be forced.
     * <p>
     * This is true, if in the properties file the key
     * <strong>"UdateTables.ForceUpdate"</strong> is set to <code>"1"</code>.
     *
     * @return true, if an updatePostCreation shall be forced
     */
    private static boolean isForceUpdate() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.getBoolean("UdateTables.ForceUpdate");
    }

    /**
     * Returns whether to updatePostCreation the database.
     *
     * @return true if {@link #isForceUpdate()} or the current application
     *         version {@link AppInfo#APP_VERSION} is different from the
     *         last updatePostCreation. This info will be written into the
     *         {@link DatabaseApplicationProperties} after an successful updatePostCreation.
     */
    private boolean isUpdate() {
        return isForceUpdate() || DatabaseMetadata.isDatabaseOfOlderVersion();
    }

    /**
     * An updater updates the database.
     */
    public interface Updater {

        /**
         * Updates the database before the tables were created.
         *
         * @param  con connection
         * @throws SQLException on database errors
         */
        void updatePreCreation(Connection con) throws SQLException;

        /**
         * Updates the database after the tables were created.
         *
         * @param  con connection
         * @throws SQLException on database errors
         */
        void updatePostCreation(Connection con) throws SQLException;

        /**
         * Returns the major version of JPhotoTagger, for that the updates were
         * built for.
         * <p>
         * That is a compromise between the minimum required count of updates
         * and updatePostCreation everything from "scratch": The updaters are not separated
         * by Major.Minor.Patch versions, they are bundled within a major
         * version.
         *
         * @return major version
         */
        int getMajorVersion();
    }
}
