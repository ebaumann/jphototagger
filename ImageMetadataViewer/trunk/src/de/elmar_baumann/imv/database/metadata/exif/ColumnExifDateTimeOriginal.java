package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>exif_date_time_original</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: DateTimeOriginal</li>
 * <li>EXIF-Tag ID: 36867 (Hex: 9003); EXIF-IFD: 046C</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnExifDateTimeOriginal extends Column {

    public static final ColumnExifDateTimeOriginal INSTANCE = new ColumnExifDateTimeOriginal();

    private ColumnExifDateTimeOriginal() {
        super(
            TableExif.INSTANCE,
            "exif_date_time_original", // NOI18N
            DataType.DATE);

        setDescription(Bundle.getString("ColumnExifDateTimeOriginal.Description"));
    }
}
