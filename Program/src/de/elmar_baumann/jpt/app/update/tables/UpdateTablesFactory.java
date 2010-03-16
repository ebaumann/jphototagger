/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.app.AppInfo;
import de.elmar_baumann.jpt.app.update.tables.v0.UpdateTablesV0;
import de.elmar_baumann.jpt.database.DatabaseApplicationProperties;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.util.Version;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-15
 */
public final class UpdateTablesFactory {
    public static final UpdateTablesFactory INSTANCE =
        new UpdateTablesFactory();
    private static final String KEY_JPT_VERSION_LAST_DB_UPDATE =
        "VersionLastDbUpdate";
    private final Set<Updater> UPDATERS = new HashSet<Updater>();

    private UpdateTablesFactory() {
        addUpdaters();
    }

    private void addUpdaters() {
        boolean force = isForceUpdate();

        if (force || isCurrentMajorVersion(0)) {
            UPDATERS.add(new UpdateTablesV0());
        }
    }

    public void update(Connection con) throws SQLException {
        if (isUpdate()) {
            Level defaultLogLevel = UserSettings.INSTANCE.getLogLevel();

            UserSettings.INSTANCE.setLogLevel(Level.FINEST);

            try {
                for (Updater updater : UPDATERS) {
                    updater.update(con);
                }

                DatabaseApplicationProperties.INSTANCE.setString(
                    KEY_JPT_VERSION_LAST_DB_UPDATE, AppInfo.APP_VERSION);
            } finally {
                UserSettings.INSTANCE.setLogLevel(defaultLogLevel);
            }
        }
    }

    public static boolean isCurrentMajorVersion(int version) {
        int cuMajor = Version.parseVersion(AppInfo.APP_VERSION, ".").getMajor();

        return version == cuMajor;
    }

    /**
     * Returns, whether a database update shall be forced.
     * <p>
     * This is true, if in the properties file the key
     * <strong>"UdateTables.ForceUpdate"</strong> is set to <code>"1"</code>.
     *
     * @return true, if an update shall be forced
     */
    public static boolean isForceUpdate() {
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
        String lastVersion = DatabaseApplicationProperties.INSTANCE.getString(
                                 KEY_JPT_VERSION_LAST_DB_UPDATE);

        return isForceUpdate()
               ? true
               : (lastVersion == null)
                 ? true
                 : !lastVersion.equals(AppInfo.APP_VERSION);
    }

    public interface Updater {
        void update(Connection con) throws SQLException;
    }
}
