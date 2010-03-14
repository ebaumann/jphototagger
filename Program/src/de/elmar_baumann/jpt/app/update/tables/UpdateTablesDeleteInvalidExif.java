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

import de.elmar_baumann.jpt.app.SplashScreen;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabaseApplicationProperties;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.jpt.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

/**
 * Removes invalid EXIF metadata (Bugfix).
 *
 * @author  Elmar Baumann
 * @version 2009-06-14
 */
final class UpdateTablesDeleteInvalidExif {
    private static final String KEY_REMOVED_INVALID_EXIF =
        "Removed_Invalid_EXIF_1";    // Never change this!
    private static final Set<Column> COLUMNS_NOT_POSITIVE =
        new HashSet<Column>();

    static {
        COLUMNS_NOT_POSITIVE.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS_NOT_POSITIVE.add(ColumnExifIsoSpeedRatings.INSTANCE);
    }

    void update(Connection con) throws SQLException {
        if (DatabaseApplicationProperties.INSTANCE.getBoolean(
                KEY_REMOVED_INVALID_EXIF)) {
            return;
        }

        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString(
                "UpdateTablesDeleteInvalidExif.Info.update"));
        setNull(con);
        SplashScreen.INSTANCE.setMessage("");
        DatabaseApplicationProperties.INSTANCE.setBoolean(
            KEY_REMOVED_INVALID_EXIF, true);
        SplashScreen.INSTANCE.setMessage("");
    }

    private void setNull(Connection con) throws SQLException {
        for (Column column : COLUMNS_NOT_POSITIVE) {
            setNullIfNotPositiv(con, column);
        }

        checkRecordingEquipment(con);
    }

    private void setNullIfNotPositiv(Connection con, Column column)
            throws SQLException {
        Database.execute(con,
                         "UPDATE " + column.getTablename() + " SET "
                         + column.getName() + " = NULL WHERE "
                         + column.getName() + " <= 0");
    }

    private void checkRecordingEquipment(Connection con) throws SQLException {
        Column column = ColumnExifRecordingEquipment.INSTANCE;

        Database.execute(con,
                         "UPDATE " + column.getTablename() + " SET "
                         + column.getName() + " = NULL WHERE "
                         + column.getName() + " = '0'");
    }
}
