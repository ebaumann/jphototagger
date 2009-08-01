package de.elmar_baumann.imv.database.metadata.xmp;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Column.DataType;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Spalte <code>rating</code> der Tabelle <code>xmp</code>.
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-31
 */
public final class ColumnXmpRating extends Column {

    public static final ColumnXmpRating INSTANCE = new ColumnXmpRating();

    private ColumnXmpRating() {
        super(
            TableXmp.INSTANCE,
            "rating", // NOI18N
            DataType.BIGINT);

        setLength(1);

        setDescription(Bundle.getString("ColumnXmpRating.Description")); // NOI18N
        setLongerDescription(Bundle.getString("ColumnXmpRating.LongerDescription")); // NOI18N
    }
}
