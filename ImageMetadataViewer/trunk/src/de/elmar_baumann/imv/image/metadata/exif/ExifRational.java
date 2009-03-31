package de.elmar_baumann.imv.image.metadata.exif;

import java.nio.ByteBuffer;

/**
 * Rational Data Type of Exif Metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/03/17
 */
public final class ExifRational {

    private int numerator = Integer.MIN_VALUE;
    private int denominator = Integer.MIN_VALUE;

    public ExifRational(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public ExifRational(byte[] numeratorRawValue, byte[] denominatorRawValue, ExifMetadata.ByteOrder byteOrder) {
        if (numeratorRawValue.length != 4 || denominatorRawValue.length != 4)
            throw new IllegalArgumentException("Numerator und/oder Denominatorlänge != 4");
        numerator = intFromRawValue(numeratorRawValue, byteOrder);
        denominator = intFromRawValue(denominatorRawValue, byteOrder);
    }

    private int intFromRawValue(byte[] rawValue, ExifMetadata.ByteOrder byteOrder) {
        assert rawValue.length == 4 : rawValue.length;
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder.equals(ExifMetadata.ByteOrder.LITTLE_ENDIAN)
            ? java.nio.ByteOrder.LITTLE_ENDIAN
            : java.nio.ByteOrder.BIG_ENDIAN);
        return buf.getInt();
    }

    public long getDenominator() {
        return denominator;
    }

    public long getNumerator() {
        return numerator;
    }
}
