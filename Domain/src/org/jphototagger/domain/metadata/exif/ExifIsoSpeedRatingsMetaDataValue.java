package org.jphototagger.domain.metadata.exif;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.lib.util.Bundle;

/**
 * Tabellenspalte <code>exif_iso_speed_ratings</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: ISOSpeedRatings;</li>
 * <li>EXIF Tag-ID: 34855 (Hex: 8827); EXIF-IFD: .</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ExifIsoSpeedRatingsMetaDataValue extends MetaDataValue {

    public static final ExifIsoSpeedRatingsMetaDataValue INSTANCE = new ExifIsoSpeedRatingsMetaDataValue();

    private ExifIsoSpeedRatingsMetaDataValue() {
        super("exif_iso_speed_ratings", "exif", ValueType.SMALLINT);
        setDescription(Bundle.getString(ExifIsoSpeedRatingsMetaDataValue.class, "ExifIsoSpeedRatingsMetaDataValue.Description"));
    }
}
