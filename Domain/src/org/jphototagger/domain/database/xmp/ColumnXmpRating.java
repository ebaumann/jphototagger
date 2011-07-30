package org.jphototagger.domain.database.xmp;

import org.jphototagger.lib.inputverifier.InputVerifierNumberRange;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.Column.DataType;
import javax.swing.InputVerifier;

/**
 * Spalte <code>rating</code> der Tabelle <code>xmp</code>.
 *
 * @author  Martin Pohlack
 */
public final class ColumnXmpRating extends Column {

    public static final ColumnXmpRating INSTANCE = new ColumnXmpRating();

    private ColumnXmpRating() {
        super("rating", "xmp", DataType.BIGINT);
        setLength(1);
        setDescription(Bundle.INSTANCE.getString("ColumnXmpRating.Description"));
        setLongerDescription(Bundle.INSTANCE.getString("ColumnXmpRating.LongerDescription"));
    }

    /**
     * Returns the minimum rating value. Lower values are treated as not
     * rated and should be set to null.
     *
     * @return minimum rating value
     */
    public static int getMinValue() {
        return 0;
    }

    /**
     * Returns the minimum rating value. Higher values shoul be set to this
     * value.
     *
     * @return minimum rating value
     */
    public static int getMaxValue() {
        return 5;
    }

    @Override
    public InputVerifier getInputVerifier() {
        return new InputVerifierNumberRange(1, 5);
    }
}
