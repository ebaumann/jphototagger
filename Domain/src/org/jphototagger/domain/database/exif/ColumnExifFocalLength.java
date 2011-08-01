package org.jphototagger.domain.database.exif;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.resource.Bundle;

/**
 * Tabellenspalte <code>exif_focal_length</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: FocalLength</li>
 * <li>EXIF Tag-ID: 37386 (Hex: 920A)</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ColumnExifFocalLength extends Column {
    public static final ColumnExifFocalLength INSTANCE = new ColumnExifFocalLength();

    private ColumnExifFocalLength() {
        super("exif_focal_length", "exif", DataType.REAL);
        setDescription(Bundle.getString(ColumnExifFocalLength.class, "ColumnExifFocalLength.Description"));
    }
}
