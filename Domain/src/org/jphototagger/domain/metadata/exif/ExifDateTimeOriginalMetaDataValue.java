package org.jphototagger.domain.metadata.exif;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>exif_date_time_original</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: DateTimeOriginal</li>
 * <li>EXIF-Tag ID: 36867 (Hex: 9003); EXIF-IFD: 046C</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ExifDateTimeOriginalMetaDataValue extends MetaDataValue {
    public static final ExifDateTimeOriginalMetaDataValue INSTANCE = new ExifDateTimeOriginalMetaDataValue();

    private ExifDateTimeOriginalMetaDataValue() {
        super("exif_date_time_original", "exif", ValueType.DATE);
        setDescription(Bundle.getString(ExifDateTimeOriginalMetaDataValue.class, "ExifDateTimeOriginalMetaDataValue.Description"));
    }
}
