package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>exif_focal_length</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: FocalLength</li>
 * <li>EXIF Tag-ID: 37386 (Hex: 920A)</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public class ColumnExifFocalLength extends Column {

    private static ColumnExifFocalLength instance = new ColumnExifFocalLength();

    public static Column getInstance() {
        return instance;
    }

    private ColumnExifFocalLength() {
        super(
            TableExif.getInstance(),
            "exif_focal_length", // NOI18N
            DataType.Real);

        setDescription(Bundle.getString("ColumnExifFocalLength.Description"));
    }
}
