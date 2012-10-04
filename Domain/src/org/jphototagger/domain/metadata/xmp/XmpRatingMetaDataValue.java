package org.jphototagger.domain.metadata.xmp;

import javax.swing.InputVerifier;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.MetaDataValue.ValueType;
import org.jphototagger.lib.swing.inputverifier.NumberRangeInputVerifier;
import org.jphototagger.lib.util.Bundle;

/**
 * Spalte <code>rating</code> der Tabelle <code>xmp</code>.
 *
 * @author  Martin Pohlack
 */
public final class XmpRatingMetaDataValue extends MetaDataValue {

    public static final XmpRatingMetaDataValue INSTANCE = new XmpRatingMetaDataValue();

    private XmpRatingMetaDataValue() {
        super("rating", "xmp", ValueType.BIGINT);
        setValueLength(1);
        setDescription(Bundle.getString(XmpRatingMetaDataValue.class, "XmpRatingMetaDataValue.Description"));
        setLongerDescription(Bundle.getString(XmpRatingMetaDataValue.class, "XmpRatingMetaDataValue.LongerDescription"));
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
        return new NumberRangeInputVerifier(1, 5);
    }
}
