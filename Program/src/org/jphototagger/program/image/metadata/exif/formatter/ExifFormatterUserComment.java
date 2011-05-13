package org.jphototagger.program.image.metadata.exif.formatter;

import org.jphototagger.program.image.metadata.exif.Ensure;
import org.jphototagger.program.image.metadata.exif.ExifTag;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Formats an EXIF entry of the dataType {@code ExifTag.Id#USER_COMMENT}.
 *
 * @author Elmar Baumann
 */
public final class ExifFormatterUserComment extends ExifFormatter {
    public static final ExifFormatterUserComment INSTANCE = new ExifFormatterUserComment();
    private static final byte[] CODE_ASCII = {
        0x41, 0x53, 0x43, 0x49, 0x49, 0x00, 0x00, 0x00
    };
    private static final byte[] CODE_JIS = {
        0x4A, 0x49, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00
    };
    private static final byte[] CODE_UNICODE = {
        0x55, 0x4E, 0x49, 0x43, 0x4F, 0x44, 0x45, 0x00
    };
    private static final byte[] CODE_UNDEFINED = {
        0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0
    };

    private enum CharCode {
        ASCII("US-ASCII"), JIS("JISAutoDetect"), UNICODE("UTF-8"), UNDEFINED(""), UNREGOGNIZED(""),
        ;

        private final String charsetName;

        private CharCode(String name) {
            this.charsetName = name;
        }

        public boolean hasCharset() {
            return !charsetName.isEmpty();
        }

        public Charset charset() {
            if (charsetName.isEmpty()) {
                throw new IllegalStateException();
            }

            return Charset.forName(charsetName);
        }
    }

    private ExifFormatterUserComment() {}

    @Override
    public String format(ExifTag exifTag) {
        if (exifTag == null) {
            throw new NullPointerException("exifTag == null");
        }

        Ensure.exifTagId(exifTag, ExifTag.Id.USER_COMMENT);

        byte[] rawValue = exifTag.rawValue();

        if (rawValue.length <= 8) {
            return "";
        }

        CharCode charCode = getEncoding(rawValue);

        if (!charCode.hasCharset()) {
            return "";
        }

        byte[] raw = Arrays.copyOfRange(rawValue, 8, rawValue.length);

        return new String(raw, charCode.charset());
    }

    private static CharCode getEncoding(byte[] rawValue) {
        assert rawValue.length >= 8 : rawValue.length;

        byte[] code = Arrays.copyOf(rawValue, 8);

        if (Arrays.equals(code, CODE_ASCII)) {
            return CharCode.ASCII;
        }

        if (Arrays.equals(code, CODE_JIS)) {
            return CharCode.JIS;
        }

        if (Arrays.equals(code, CODE_UNICODE)) {
            return CharCode.UNICODE;
        }

        if (Arrays.equals(code, CODE_UNDEFINED)) {
            return CharCode.UNDEFINED;
        }

        return CharCode.UNREGOGNIZED;
    }
}
