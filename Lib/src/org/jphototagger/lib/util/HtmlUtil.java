package org.jphototagger.lib.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * @author Elmar Baumann
 */
public final class HtmlUtil {

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

    public static String html2PlainText(String htmlText) {
        if (htmlText == null) {
            throw new NullPointerException("htmlText == null");
        }
        ParserCallback parser = new ParserCallback();
        try {
            parser.parse(new StringReader(htmlText));
        } catch (IOException ex) {
            Logger.getLogger(ParserCallback.class.getName()).log(Level.SEVERE, null, ex);
        }
        return parser.getText();
    }

    private static final class ParserCallback extends HTMLEditorKit.ParserCallback {

        private StringBuilder sb;

        private void parse(Reader reader) throws IOException {
            sb = new StringBuilder();
            ParserDelegator delegator = new ParserDelegator();
            delegator.parse(reader, this, true);
        }

        @Override
        public void handleText(char[] text, int pos) {
            sb.append(text);
        }

        private String getText() {
            return sb.toString();
        }
    }

    private HtmlUtil() {
    }
}
