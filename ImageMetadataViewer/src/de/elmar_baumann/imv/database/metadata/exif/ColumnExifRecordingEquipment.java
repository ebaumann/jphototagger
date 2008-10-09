package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>exif_recording_equipment</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Model (Recording Equipment Model)</li>
 * <li>EXIF Tag-ID: 272 (Hex: 110).</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnExifRecordingEquipment extends Column {

    private static ColumnExifRecordingEquipment instance = new ColumnExifRecordingEquipment();

    public static Column getInstance() {
        return instance;
    }

    private ColumnExifRecordingEquipment() {
        super(
            TableExif.getInstance(),
            "exif_recording_equipment", // NOI18N
            DataType.String);

        setLength(125);
        setDescription(Bundle.getString("ColumnExifRecordingEquipment.Description"));
    }
}
