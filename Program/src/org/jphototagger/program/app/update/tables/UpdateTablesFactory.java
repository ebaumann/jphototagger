/*
 * @(#)UpdateTablesFactory.java    Created on 2010-03-15
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

package org.jphototagger.program.app.update.tables;

import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.update.tables.v0.UpdateTablesV0;
import org.jphototagger.program.database.DatabaseApplicationProperties;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.util.Version;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Creates updaters for updating an older database version and let them update
 * the database.
 *
 * @author  Elmar Baumann
 */
public final class UpdateTablesFactory {
    public static final UpdateTablesFactory INSTANCE =
        new UpdateTablesFactory();
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

    public void update(Connection con) throws SQLException {
        if (isUpdate()) {
            Level defaultLogLevel = UserSettings.INSTANCE.getLogLevel();

            UserSettings.INSTANCE.setLogLevel(Level.FINEST);

            try {
                for (Updater updater : runningUpdaters) {
                    updater.update(con);
                }

                DatabaseMetadata.setCurrentAppVersionToDatabase();
            } finally {
                UserSettings.INSTANCE.setLogLevel(defaultLogLevel);
            }
        }
    }

    private static boolean isRunUpdaterOfMajorVersion(int version) {
        String dbVersion = DatabaseMetadata.getDatabaseAppVersion();

        /* dbVersion == null: Only versions prior to 0.8.3 do not write version
         * info into the database. In that case, every upater has to be created.
         */
        return (dbVersion == null)
               ? true
               : version >= Version.parseVersion(dbVersion, ".").getMajor();
    }

    /**
     * Returns, whether a database update shall be forced.
     * <p>
     * This is true, if in the properties file the key
     * <strong>"UdateTables.ForceUpdate"</strong> is set to <code>"1"</code>.
     *
     * @return true, if an update shall be forced
     */
    private static boolean isForceUpdate() {
        return UserSettings.INSTANCE.getSettings().getBoolean(
            "UdateTables.ForceUpdate");
    }

    /**
     * Returns whether to update the database.
     *
     * @return true if {@link #isForceUpdate()} or the current application
     *         version {@link AppInfo#APP_VERSION} is different from the
     *         last update. This info will be written into the
     *         {@link DatabaseApplicationProperties} after an successful update.
     */
    private boolean isUpdate() {
        return isForceUpdate() || DatabaseMetadata.isDatabaseOfOlderVersion();
    }

    /**
     * An updater updates the database.
     */
    public interface Updater {

        /**
         * Updates the database.
         *
         * @param  con connection
         * @throws SQLException on database errors
         */
        void update(Connection con) throws SQLException;

        /**
         * Returns the major version of JPhotoTagger, for that the updates were
         * built for.
         * <p>
         * That is a compromise between the minimum required count of updates
         * and update everything from "scratch": The updaters are not separated
         * by Major.Minor.Patch versions, they are bundled within a major
         * version.
         *
         * @return major version
         */
        int getMajorVersion();
    }
}
