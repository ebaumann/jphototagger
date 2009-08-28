package de.elmar_baumann.imv.app.update.tables;

import de.elmar_baumann.imv.database.DatabaseApplication;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Removes invalid EXIF metadata (Bugfix).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-14
 */
final class UpdateTablesDeleteInvalidExif {

    private static final String KEY_REMOVED_INVALID_EXIF =
            "Removed_Invalid_EXIF_1"; // NOI18N Never change this!
    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog progress = messages.getProgressDialog();
    private static final Set<Column> COLUMNS_NOT_POSITIVE =
            new HashSet<Column>();

    static {
        COLUMNS_NOT_POSITIVE.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS_NOT_POSITIVE.add(ColumnExifIsoSpeedRatings.INSTANCE);
    }

    void update(Connection connection) throws SQLException {
        if (DatabaseApplication.INSTANCE.getBoolean(KEY_REMOVED_INVALID_EXIF))
            return;
        progress.setIndeterminate(true);
        messages.message(Bundle.getString(
                "UpdateTablesDeleteInvalidExif.Info.update")); // NOI18N
        setNull(connection);
        progress.setIndeterminate(false);
        messages.message(""); // NOI18N
        DatabaseApplication.INSTANCE.setBoolean(KEY_REMOVED_INVALID_EXIF, true);
    }

    private void setNull(Connection connection) throws SQLException {
        for (Column column : COLUMNS_NOT_POSITIVE) {
            setNullIfNotPositiv(connection, column);
        }
        checkRecordingEquipment(connection);
    }

    private void setNullIfNotPositiv(Connection connection, Column column)
            throws
            SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("UPDATE " + column.getTable().getName() + // NOI18N
                " SET " + column.getName() + " = NULL" + // NOI18N
                " WHERE " + column.getName() + " <= 0"); // NOI18N
    }

    private void checkRecordingEquipment(Connection connection) throws
            SQLException {
        Column column = ColumnExifRecordingEquipment.INSTANCE;
        Statement stmt = connection.createStatement();
        stmt.execute("UPDATE " + column.getTable().getName() + // NOI18N
                " SET " + column.getName() + " = NULL" + // NOI18N
                " WHERE " + column.getName() + " = '0'"); // NOI18N
    }
}
