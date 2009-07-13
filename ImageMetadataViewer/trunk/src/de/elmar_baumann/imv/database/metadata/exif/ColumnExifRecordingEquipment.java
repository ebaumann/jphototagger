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
public final class ColumnExifRecordingEquipment extends Column {

    public static final ColumnExifRecordingEquipment INSTANCE = new ColumnExifRecordingEquipment();

    private ColumnExifRecordingEquipment() {
        super(
            TableExif.INSTANCE,
            "exif_recording_equipment", // NOI18N
            DataType.STRING);

        setLength(125);
        setDescription(Bundle.getString("ColumnExifRecordingEquipment.Description")); // NOI18N
    }
}
