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
        setLongerDescription(Bundle.getString(
                "ColumnXmpRating.LongerDescription")); // NOI18N
    }

    /**
     * Returns the minimum rating value. Lower values are treated as not
     * rated and should be set to null.
     *
     * TODO: generalize for Column
     *
     * @return minimum rating value.
     */
    public static int getMinValue() {
        return 1;
    }

    /**
     * Returns the minimum rating value. Higher values shoul be set to this
     * value.
     *
     * TODO: generalize for Column
     *
     * @return minimum rating value.
     */
    public static int getMaxValue() {
        return 5;
    }
}
