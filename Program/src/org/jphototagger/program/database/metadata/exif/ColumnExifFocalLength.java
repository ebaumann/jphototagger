package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.domain.Column;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnExifFocalLength.Description"));
    }
}
