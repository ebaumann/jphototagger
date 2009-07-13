package de.elmar_baumann.imv.database.metadata.exif;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Tabellenspalte <code>exif_iso_speed_ratings</code> der Tabelle <code>exif</code>.
 * <ul>
 * <li>EXIF: ISOSpeedRatings;</li>
 * <li>EXIF Tag-ID: 34855 (Hex: 8827); EXIF-IFD: .</li>
 * </ul>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/27
 */
public final class ColumnExifIsoSpeedRatings extends Column {

    public static final ColumnExifIsoSpeedRatings INSTANCE = new ColumnExifIsoSpeedRatings();

    private ColumnExifIsoSpeedRatings() {
        super(
            TableExif.INSTANCE,
            "exif_iso_speed_ratings", // NOI18N
            DataType.SMALLINT);

        setDescription(Bundle.getString("ColumnExifIsoSpeedRatings.Description")); // NOI18N
    }
}
