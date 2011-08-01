package org.jphototagger.domain.database.exif;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.resource.Bundle;

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
    public static final ColumnExifDateTimeOriginal INSTANCE = new ColumnExifDateTimeOriginal();

    private ColumnExifDateTimeOriginal() {
        super("exif_date_time_original", "exif", DataType.DATE);
        setDescription(Bundle.getString(ColumnExifDateTimeOriginal.class, "ColumnExifDateTimeOriginal.Description"));
    }
}
