package org.jphototagger.program.app.update.tables.v0;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseApplicationProperties;

/**
 * Removes invalid EXIF metadata (Bugfix).
 *
 * @author Elmar Baumann
 */
final class UpdateTablesDeleteInvalidExif {

    private static final String KEY_REMOVED_INVALID_EXIF = "Removed_Invalid_EXIF_1";    // Never change this!
    private static final Set<MetaDataValue> META_DATA_VALUES_NOT_POSITIVE = new HashSet<MetaDataValue>();

    static {
        META_DATA_VALUES_NOT_POSITIVE.add(ExifFocalLengthMetaDataValue.INSTANCE);
        META_DATA_VALUES_NOT_POSITIVE.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
    }

    void update(Connection con) throws SQLException {
        startMessage();

        if (!DatabaseApplicationProperties.INSTANCE.getBoolean(KEY_REMOVED_INVALID_EXIF)) {
            setNull(con);
            DatabaseApplicationProperties.INSTANCE.setBoolean(KEY_REMOVED_INVALID_EXIF, true);
        }

        SplashScreen.INSTANCE.removeMessage();
    }

    private void setNull(Connection con) throws SQLException {
        for (MetaDataValue mdValue : META_DATA_VALUES_NOT_POSITIVE) {
            setNullIfNotPositiv(con, mdValue);
        }

        checkRecordingEquipment(con);
    }

    private void setNullIfNotPositiv(Connection con, MetaDataValue mdValue) throws SQLException {
        Database.execute(con,
                "UPDATE " + mdValue.getCategory() + " SET " + mdValue.getValueName() + " = NULL WHERE "
                + mdValue.getValueName() + " <= 0");
    }

    private void checkRecordingEquipment(Connection con) throws SQLException {
        MetaDataValue mdValue = ExifRecordingEquipmentMetaDataValue.INSTANCE;

        Database.execute(con,
                "UPDATE " + mdValue.getCategory() + " SET " + mdValue.getValueName() + " = NULL WHERE "
                + mdValue.getValueName() + " = '0'");
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(Bundle.getString(UpdateTablesDeleteInvalidExif.class, "UpdateTablesDeleteInvalidExif.Info"));
    }
}
