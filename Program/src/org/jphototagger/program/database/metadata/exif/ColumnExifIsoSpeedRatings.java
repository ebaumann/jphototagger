package org.jphototagger.program.database.metadata.exif;

import org.jphototagger.domain.Column;
import org.jphototagger.program.resource.JptBundle;

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
        setDescription(JptBundle.INSTANCE.getString("ColumnExifIsoSpeedRatings.Description"));
    }
}
