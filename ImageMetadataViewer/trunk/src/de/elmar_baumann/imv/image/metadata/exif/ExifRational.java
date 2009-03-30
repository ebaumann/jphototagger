package de.elmar_baumann.imv.image.metadata.exif;

import java.nio.ByteBuffer;
import java.util.StringTokenizer;

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

    public enum ByteOrder {

        LITTLE_ENDIAN,
        BIG_ENDIAN
    }

    public ExifRational(byte[] numeratorRawValue, byte[] denominatorRawValue, ByteOrder byteOrder) {
        if (numeratorRawValue.length != 4 || denominatorRawValue.length != 4)
            throw new IllegalArgumentException("Numerator und/oder Denominatorlänge != 4");
        numerator = intFromRawValue(numeratorRawValue, byteOrder);
        denominator = intFromRawValue(denominatorRawValue, byteOrder);
    }

    private int intFromRawValue(byte[] rawValue, ByteOrder byteOrder) {
        assert rawValue.length == 4 : rawValue.length;
        ByteBuffer buf = ByteBuffer.wrap(rawValue);
        buf.order(byteOrder.equals(ByteOrder.LITTLE_ENDIAN)
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

    public static long getNumerator(byte[] rawValue) {
        long n = Long.MIN_VALUE;
        if (rawValue != null && rawValue.length == 1) {
            StringTokenizer tok = new StringTokenizer(new String(rawValue), "-/");
            if (tok.countTokens() == 2) {
                n = Long.getLong(tok.nextToken());
            }
        }
        return n;
    }

    public static long getDenominator(byte[] rawValue) {
        long n = Long.MIN_VALUE;
        if (rawValue != null && rawValue.length == 1) {
            StringTokenizer tok = new StringTokenizer(new String(rawValue), "-/");
            if (tok.countTokens() == 2) {
                tok.nextToken();
                n = Long.getLong(tok.nextToken());
            }
        }
        return n;
    }
}
