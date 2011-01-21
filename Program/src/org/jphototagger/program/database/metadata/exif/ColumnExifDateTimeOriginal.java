package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.resource.JptBundle;

/**
 * Tabellenspalte <code>exif_date_time_original</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: DateTimeOriginal</li>
 * <li>EXIF-Tag ID: 36867 (Hex: 9003); EXIF-IFD: 046C</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ColumnExifDateTimeOriginal extends Column {
    public static final ColumnExifDateTimeOriginal INSTANCE =
        new ColumnExifDateTimeOriginal();

    private ColumnExifDateTimeOriginal() {
        super("exif_date_time_original", "exif", DataType.DATE);
        setDescription(
            JptBundle.INSTANCE.getString(
                "ColumnExifDateTimeOriginal.Description"));
    }
}
