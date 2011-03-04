package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Column.DataType;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnExifRecordingEquipment.Description"));
    }
}
