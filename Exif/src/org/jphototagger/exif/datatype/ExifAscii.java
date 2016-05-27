package org.jphototagger.exif.datatype;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.ByteUtil;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * EXIF data type ASCII as described in the standard: An 8-bit byte
 * containing one 7-bit ASCII code. The final byte is terminated with NULL.
 *
 * @author Elmar Baumann
 */
public final class ExifAscii {

    private static final Charset CHARSET;

    static {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String key = "ExifImageDescriptionCharSet";
        if (prefs.containsKey(key)) {
            String charset = prefs.getString(key);
            // Default: Allow more characters than ASCII contains. ISO-8859-1 contains as subset all ASCII characters.
            Charset cs = Charset.forName("ISO-8859-1");
            try {
                if (StringUtil.hasContent(charset)) {
                    cs = Charset.forName(charset);
                    Logger.getLogger(ExifAscii.class.getName()).log(Level.INFO, "Using charset {0} to decode EXIF ASCII fields", charset);
                }
            } catch (Throwable t) {
                Logger.getLogger(ExifAscii.class.getName()).log(Level.SEVERE, null, t);
            }
            CHARSET = cs;
        } else {
            CHARSET = Charset.forName("ISO-8859-1");
        }
    }
    private final String value;

    public ExifAscii(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }
        value = convertRawValueToString(rawValue);
    }

    public static String convertRawValueToString(byte[] rawValue) {
        if (rawValue == null) {
            throw new NullPointerException("rawValue == null");
        }
        if (rawValue.length == 0) {
            return "";
        }
        if (!isNullTerminated(rawValue)) {
            return "?";
        }
        if (rawValue.length == 1) { // == "\0"
            return "";
        }
        byte[] notNullTerminatedRawValue = new byte[rawValue.length - 1]; // rawValue.length > 1
        System.arraycopy(rawValue, 0, notNullTerminatedRawValue, 0, notNullTerminatedRawValue.length);
        if (!onlyCharacters(notNullTerminatedRawValue)) {
            return "?";
        }
        String converted = new String(notNullTerminatedRawValue, CHARSET);
        return converted;
    }

    private static boolean onlyCharacters(byte[] bytes) {
        for (int index = 0; index < bytes.length - 1; index++) {
            int intValue = ByteUtil.toInt(bytes[index]);
            boolean isCharacter = intValue > 0 && intValue < 256; // Allow a more characters than ASCII contains, but conform to CHARSET
            if (!isCharacter) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNullTerminated(byte[] rawValue) {
        if (rawValue.length < 1) {
            return false;
        }
        byte lastByte = rawValue[rawValue.length - 1];
        return ByteUtil.toInt(lastByte) == 0;
    }

    public static ExifValueType getValueType() {
        return ExifValueType.ASCII;
    }

    public String getValue() {
        return value;
    }

    /**
     *
     * @param obj
     * @return true if the values of both objects are equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ExifAscii)) {
            return false;
        }
        ExifAscii other = (ExifAscii) obj;
        return this.value == null
                ? other.value == null
                : this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.value != null
                ? this.value.hashCode()
                : 0;
        return hash;
    }

    @Override
    public String toString() {
        return value;
    }
}
