package org.jphototagger.domain.database.exif;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import org.jphototagger.lib.resource.Bundle;

/**
 * Tabellenspalte <code>exif_recording_equipment</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Model (Recording Equipment Model)</li>
 * <li>EXIF Tag-ID: 272 (Hex: 110).</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ColumnExifRecordingEquipment extends Column {
    public static final ColumnExifRecordingEquipment INSTANCE = new ColumnExifRecordingEquipment();

    private ColumnExifRecordingEquipment() {
        super("equipment", "exif_recording_equipment", DataType.STRING);
        setLength(125);
        setDescription(Bundle.getString(ColumnExifRecordingEquipment.class, "ColumnExifRecordingEquipment.Description"));
    }
}
