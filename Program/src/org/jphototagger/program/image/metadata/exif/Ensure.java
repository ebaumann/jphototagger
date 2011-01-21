package org.jphototagger.program.image.metadata.exif;

import org.jphototagger.program.image.metadata.exif.datatype.ExifDataType;

/**
 * Checks conditions and throws Exceptions if not fullified.
 *
 * @author Elmar Baumann
 */
public final class Ensure {

    /**
     * Ensures that an EXIF tag has a specific tag ID.
     *
     * @param  exifTag tag
     * @param  id      tag id
     * @throws         IllegalArgumentException if the tag doesn't have that ID
     */
    public static void exifTagId(ExifTag exifTag, ExifTag.Id id)
            throws IllegalArgumentException {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        if (id == null) {
            throw new NullPointerException("id == null");
        }

        if (exifTag.idValue() != id.value()) {
            throw new IllegalArgumentException("Wrong tag: "
                                               + exifTag.idValue()
                                               + ". Expected: " + id.value());
        }
    }

    /**
     * Ensures that an EXIF tag is of a specific dataType.
     *
     * @param  exifTag  tag
     * @param  dataType dataType
     * @throws          IllegalArgumentException if the tag doesn't have that dataType
     */
    public static void exifDataType(ExifTag exifTag, ExifDataType dataType)
            throws IllegalArgumentException {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        if (dataType == null) {
            throw new NullPointerException("dataType == null");
        }

        if (!exifTag.dataType().equals(dataType)) {
            throw new IllegalArgumentException("Wrong type: "
                                               + exifTag.dataType()
                                               + ". Expected: " + dataType);
        }
    }

    /**
     * Ensures that a value is zero or zeroOrPositive.
     *
     * @param  value value
     * @throws       IllegalArgumentException if the value is negative
     */
    public static void zeroOrPositive(long value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IllegalArgumentException("Negativ value: " + value);
        }
    }

    /**
     * Ensures that a fraction is zero or positive.
     *
     * @param  numerator   numerator
     * @param  denominator denominator
     * @throws             IllegalArgumentException if the fraction is negative
     */
    public static void zeroOrPositive(long numerator, long denominator)
            throws IllegalArgumentException {
        boolean negative = ((numerator < 0) && (denominator > 0))
                           || ((numerator > 0) && (denominator < 0));

        if (negative) {
            throw new IllegalArgumentException("Negative fraction: "
                                               + numerator + "/" + denominator);
        }
    }

    /**
     * Ensures that a denominator of a fraction is not zero.
     *
     * @param  denominator denominator
     * @throws             IllegalArgumentException if the denominator is equals
     *                     to zero
     */
    public static void noDivisionByZero(long denominator)
            throws IllegalArgumentException {
        if (denominator == 0) {
            throw new IllegalArgumentException("Zero division");
        }
    }

    /**
     * Ensures the length of a byte array.
     *
     * @param  bytes  array
     * @param  length required length
     * @throws        IllegalArgumentException if the array length is not equals
     *                to <code>length</code>
     */
    public static void length(byte[] bytes, int length)
            throws IllegalArgumentException {
        if (bytes == null) {
            throw new NullPointerException("bytes == null");
        }

        if (bytes.length != length) {
            throw new IllegalArgumentException("Illegal length: "
                                               + bytes.length + ". Required: "
                                               + length);
        }
    }

    private Ensure() {}
}
