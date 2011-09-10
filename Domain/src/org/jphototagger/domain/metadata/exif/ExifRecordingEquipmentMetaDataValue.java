package org.jphototagger.domain.metadata.exif;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>exif_recording_equipment</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: Model (Recording Equipment Model)</li>
 * <li>EXIF Tag-ID: 272 (Hex: 110).</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ExifRecordingEquipmentMetaDataValue extends MetaDataValue {
    public static final ExifRecordingEquipmentMetaDataValue INSTANCE = new ExifRecordingEquipmentMetaDataValue();

    private ExifRecordingEquipmentMetaDataValue() {
        super("equipment", "exif_recording_equipment", ValueType.STRING);
        setValueLength(125);
        setDescription(Bundle.getString(ExifRecordingEquipmentMetaDataValue.class, "ExifRecordingEquipmentMetaDataValue.Description"));
    }
}
