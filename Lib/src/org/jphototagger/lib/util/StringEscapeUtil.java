package org.jphototagger.lib.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elmar Baumann
 */
public final class StringEscapeUtil {

    private static final Map<Character, String> ESCAPED_HTML_CHAR_OF = new HashMap<>();

    static {
        ESCAPED_HTML_CHAR_OF.put('<', "&lt;");
        ESCAPED_HTML_CHAR_OF.put('>', "&gt;");
        ESCAPED_HTML_CHAR_OF.put('"', "&quot;");
        ESCAPED_HTML_CHAR_OF.put('&', "&amp;");
    }

    public static String escapeHTML(String string) {
        StringBuilder sb = new StringBuilder();

        for (char character : string.toCharArray()) {
            String escapedCharacter = ESCAPED_HTML_CHAR_OF.get(character);

            sb.append(escapedCharacter == null ? character : escapedCharacter);
        }

        return sb.toString();
    }

    private StringEscapeUtil() {
    }
}
