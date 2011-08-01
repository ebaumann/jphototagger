package org.jphototagger.domain.database.exif;

import org.jphototagger.domain.database.Column;
import org.jphototagger.lib.resource.Bundle;

/**
 * Tabellenspalte <code>exif_iso_speed_ratings</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: ISOSpeedRatings;</li>
 * <li>EXIF Tag-ID: 34855 (Hex: 8827); EXIF-IFD: .</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class ColumnExifIsoSpeedRatings extends Column {
    public static final ColumnExifIsoSpeedRatings INSTANCE = new ColumnExifIsoSpeedRatings();

    private ColumnExifIsoSpeedRatings() {
        super("exif_iso_speed_ratings", "exif", DataType.SMALLINT);
        setDescription(Bundle.getString(ColumnExifIsoSpeedRatings.class, "ColumnExifIsoSpeedRatings.Description"));
    }
}
