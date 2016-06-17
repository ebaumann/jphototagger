package org.jphototagger.exif.datatype;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
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

    private static final String PREF_KEY_CHARSET = "ExifImageDescriptionCharSet";
    private static final String DEFAULT_CHARSET = "UTF-8"; // Default: Allow more characters than ASCII contains. UTF-8 contains as subset all ASCII characters.
    private static final Collection<String> VALID_EXIF_CHARSETS = Arrays.asList("UTF-8", "ISO-8859-1", "ASCII");
    private static final CharsetChangeListener CHARSET_CHANGE_LISTENER = new CharsetChangeListener(); // Exactly one instance is required
    private static Charset charset = Charset.forName(DEFAULT_CHARSET);
    private final String value;

    static {
        setCharset(getCharset());
    }

    public static String[] getValidCharsets() {
        return VALID_EXIF_CHARSETS.toArray(new String[VALID_EXIF_CHARSETS.size()]);
    }

    public static boolean isValidCharset(String charset) {
        return VALID_EXIF_CHARSETS.contains(charset);
    }

    public static String getCharset() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(PREF_KEY_CHARSET)) {
            String cs = prefs.getString(ExifAscii.PREF_KEY_CHARSET);
            return isValidCharset(cs)
                    ? cs
                    : DEFAULT_CHARSET;
        }
        return DEFAULT_CHARSET;
    }

    public static void persistCharset(String charset) {
        if (StringUtil.hasContent(charset) && isValidCharset(charset)) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setString(PREF_KEY_CHARSET, charset);
        }
    }

    private static synchronized void setCharset(String newCharset) {
        if (StringUtil.hasContent(newCharset) && isValidCharset(newCharset)) {
            try {
                Logger.getLogger(ExifAscii.class.getName()).log(Level.INFO, "Using charset {0} to decode EXIF ASCII fields", newCharset);
                charset = Charset.forName(newCharset);
            } catch (Throwable t) {
                Logger.getLogger(ExifAscii.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static final class CharsetChangeListener {

        private CharsetChangeListener() {
            AnnotationProcessor.process(this);
        }

        @EventSubscriber(eventClass = PreferencesChangedEvent.class)
        public void preferencesChanged(PreferencesChangedEvent evt) {
            if (PREF_KEY_CHARSET.equals(evt.getKey())) {
                Object newValue = evt.getNewValue();
                if (newValue instanceof String) {
                    String charset = (String) newValue;
                    setCharset(charset);
                }
            }
        }
    }

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
        String converted = new String(notNullTerminatedRawValue, charset);
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
