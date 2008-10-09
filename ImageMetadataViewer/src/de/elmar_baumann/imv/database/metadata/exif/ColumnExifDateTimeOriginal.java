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
public class ColumnExifDateTimeOriginal extends Column {

    private static ColumnExifDateTimeOriginal instance = new ColumnExifDateTimeOriginal();

    public static Column getInstance() {
        return instance;
    }

    private ColumnExifDateTimeOriginal() {
        super(
            TableExif.getInstance(),
            "exif_date_time_original", // NOI18N
            DataType.Date);

        setDescription(Bundle.getString("ColumnExifDateTimeOriginal.Description"));
    }
}
