package de.elmar_baumann.imv.database;

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
 * @version 2009/06/14
 */
final class UpdateTablesDeleteInvalidExif {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;
    private final ProgressDialog dialog = messages.getProgressDialog();
    private static final Set<Column> COLUMNS_NOT_POSITIVE = new HashSet<Column>();


    static {
        COLUMNS_NOT_POSITIVE.add(ColumnExifFocalLength.INSTANCE);
        COLUMNS_NOT_POSITIVE.add(ColumnExifIsoSpeedRatings.INSTANCE);
    }

    void update(Connection connection) throws SQLException {
        dialog.setIndeterminate(true);
        messages.message(Bundle.getString(
                "UpdateTablesDeleteInvalidExif.InformationMessage.update"));
        setNull(connection);
        dialog.setIndeterminate(false);
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
