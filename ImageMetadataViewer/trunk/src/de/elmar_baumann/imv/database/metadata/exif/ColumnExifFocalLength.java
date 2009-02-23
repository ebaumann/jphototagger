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
public final class ColumnExifFocalLength extends Column {

    public static final ColumnExifFocalLength INSTANCE = new ColumnExifFocalLength();

    private ColumnExifFocalLength() {
        super(
            TableExif.INSTANCE,
            "exif_focal_length", // NOI18N
            DataType.REAL);

        setDescription(Bundle.getString("ColumnExifFocalLength.Description"));
    }
}
