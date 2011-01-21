package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseApplicationProperties;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.exif.ColumnExifFocalLength;
import org.jphototagger.program.database.metadata.exif
    .ColumnExifIsoSpeedRatings;
import org.jphototagger.program.database.metadata.exif
    .ColumnExifRecordingEquipment;
import org.jphototagger.program.resource.JptBundle;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

/**
 * Removes invalid EXIF metadata (Bugfix).
 *
 * @author Elmar Baumann
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
        startMessage();
        if (!DatabaseApplicationProperties.INSTANCE.getBoolean(
                KEY_REMOVED_INVALID_EXIF)) {
            setNull(con);
            DatabaseApplicationProperties.INSTANCE.setBoolean(
                KEY_REMOVED_INVALID_EXIF, true);
        }
        SplashScreen.INSTANCE.removeMessage();
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

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesDeleteInvalidExif.Info"));
    }
}
