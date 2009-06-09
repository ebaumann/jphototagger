package de.elmar_baumann.imv.image.metadata.exif;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * An user comment as defined in the EXIF standard, tag 37510 (9286.H).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/09
 */
public final class ExifUserComment {

    private static final byte[] CODE_ASCII = {0x41, 0x53, 0x43, 0x49, 0x49, 0x00, 0x00, 0x00};
    private static final byte[] CODE_JIS = {0x4A, 0x49, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00};
    private static final byte[] CODE_UNICODE = {0x55, 0x4E, 0x49, 0x43, 0x4F, 0x44, 0x45, 0x00};
    private static final byte[] CODE_UNDEFINED = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

    private enum CharCode {

        ASCII, JIS, UNICODE, UNDEFINED, UNREGOGNIZED
    }

    /**
     * Decodes an user comment.
     *
     * @param  rawValue  raw value
     * @return user comment or empty string if not decoded
     */
    public static String decode(byte[] rawValue) {
        if (rawValue.length <= 8) return "";
        CharCode charCode = getEncoding(rawValue);
        byte[] rawComment = Arrays.copyOfRange(rawValue, 8, rawValue.length);
        if (charCode.equals(CharCode.ASCII))
            return new String(rawComment, Charset.forName("US-ASCII"));
        if (charCode.equals(CharCode.JIS))
            return new String(rawComment, Charset.forName("JISAutoDetect"));
        if (charCode.equals(CharCode.UNICODE))
            return new String(rawComment, Charset.forName("UTF-8"));
        return "";
    }

    private static CharCode getEncoding(byte[] rawValue) {
        assert rawValue.length >= 8 : rawValue.length;
        byte[] code = Arrays.copyOf(rawValue, 8);
        if (Arrays.equals(code, CODE_ASCII)) return CharCode.ASCII;
        if (Arrays.equals(code, CODE_JIS)) return CharCode.JIS;
        if (Arrays.equals(code, CODE_UNICODE)) return CharCode.UNICODE;
        if (Arrays.equals(code, CODE_UNDEFINED)) return CharCode.UNDEFINED;
        return CharCode.UNREGOGNIZED;
    }

    private ExifUserComment() {
    }
}
