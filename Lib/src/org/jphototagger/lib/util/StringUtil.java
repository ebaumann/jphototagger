package org.jphototagger.lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utils for strings.
 *
 * @author Elmar Baumann
 */
public final class StringUtil {
    private static final String WORD_DELIMITER = " \t\n\r";

    /**
     * Calls {@link #wrapWords(java.lang.String, int, java.lang.String)} with
     * predefined word delimiter.
     *
     * @param text            text
     * @param maxCharsPerLine maximum characters per line
     * @return                lines
     */
    public static List<String> wrapWords(String text, int maxCharsPerLine) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (maxCharsPerLine <= 0) {
            throw new IllegalArgumentException("maxCharPerLine <= 0: " + maxCharsPerLine);
        }

        return wrapWords(text, maxCharsPerLine, WORD_DELIMITER);
    }

    /**
     * Wraps text into lines where a line has a maximum number of chars. After
     * wrapping leading word delimiters will not be removed, only one delimiter
     * per line will be removed.
     *
     * @param text            text
     * @param maxCharsPerLine maximum numbers per line
     * @param wordDelimiter   characters where lines shall be breaked. Only if
     *                        that is not possible words will be cutted and
     *                        continued at the next line
     * @return                lines
     */
    public static List<String> wrapWords(String text, int maxCharsPerLine, String wordDelimiter) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (wordDelimiter == null) {
            throw new NullPointerException("wordDelimiter == null");
        }

        if (maxCharsPerLine <= 0) {
            throw new IllegalArgumentException("Invalid max chars per line: " + maxCharsPerLine);
        }

        if (wordDelimiter.isEmpty()) {
            throw new IllegalArgumentException("Empty word delimiter string!");
        }

        List<String> lines = new ArrayList<String>();
        int textLength = text.length();
        int lineBeginIndex = 0;
        int lineEndIndex = 0;
        int currentLineBreakCharIndex = 0;
        int prevLineBreakCharIndex = 0;
        int index = 0;
        boolean end = text.isEmpty();

        while (!end) {
            if (isWordDelimiter(text.charAt(index), wordDelimiter)) {
                prevLineBreakCharIndex = currentLineBreakCharIndex;
                currentLineBreakCharIndex = index;
            }

            int lineCount = lines.size();
            int maxBreakIndex = lineCount * maxCharsPerLine + maxCharsPerLine;

            if (index == maxBreakIndex) {
                if (currentLineBreakCharIndex > lineBeginIndex) {
                    lineEndIndex = currentLineBreakCharIndex;
                } else if (prevLineBreakCharIndex > lineBeginIndex) {
                    lineEndIndex = prevLineBreakCharIndex;
                } else {
                    lineEndIndex = (lineBeginIndex == maxBreakIndex)
                                   ? maxBreakIndex + 1
                                   : maxBreakIndex;
                }

                lines.add(text.substring(lineBeginIndex, lineEndIndex));
                lineBeginIndex = isWordDelimiter(text.charAt((lineEndIndex < textLength)
                        ? lineEndIndex
                        : textLength - 1), wordDelimiter)
                                 ? lineEndIndex + 1
                                 : lineEndIndex;
            }

            index++;
            end = index >= textLength;
        }

        if (lineBeginIndex <= textLength - 1) {
            lines.add(text.substring(lineBeginIndex, textLength));
        }

        return lines;
    }

    private static boolean isWordDelimiter(char c, String wordDelimiter) {
        return wordDelimiter.contains(Character.toString(c));
    }

    public static List<String> getTrimmed(Collection<? extends String> strings) {
        if (strings == null) {
            throw new NullPointerException("strings == null");
        }

        List<String> trimmedStrings = new ArrayList<String>(strings.size());

        for (String string : strings) {
            trimmedStrings.add(string.trim());
        }

        return trimmedStrings;
    }

    public static List<String> getWordsOf(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        List<String> words = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(string, WORD_DELIMITER);

        while (st.hasMoreTokens()) {
            words.add(st.nextToken().trim());
        }

        return words;
    }

    public static boolean isIndex(String s, int index) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }

        return (index >= 0) && (index < s.length());
    }

    public static boolean isSubstring(String s, int beginIndex, int endIndex) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }

        int len = s.length();

        return (len > 0) && (beginIndex >= 0) && (endIndex >= beginIndex) && (endIndex <= len);
    }

    /**
     * Returns a to its last characters shortened string with dots as prefix
     * instead of the string content.
     *
     * @param s         string
     * @param maxLength maximum length of the string included the dots
     * @return          shortened string or the string itself if it's length
     *                  is less or equal to <code>maxLength</code>
     * @throws          NullPointerException if s is null
     * @throws          IllegalArgumentException if maxLength is less than 3
     */
    public static String getPrefixDotted(String s, int maxLength) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }

        if (maxLength < 3) {
            throw new IllegalArgumentException("Max length < 3: " + maxLength);
        }

        String prefix = "...";
        int stringLength = s.length();
        int prefixLength = prefix.length();
        int substringLength = maxLength - prefixLength;

        if (stringLength <= maxLength) {
            return s;
        }

        if (maxLength == prefixLength) {
            return prefix;
        }

        if (stringLength <= substringLength) {
            return prefix + "s";
        }

        return prefix + s.substring(stringLength - substringLength);
    }

    public static String toString(Object[] objects) {
        if (objects == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int index = 0; index < objects.length; index++) {
            Object object = objects[index];

            sb.append((index == 0)
                      ? "'"
                      : "', '");
            sb.append((object == null)
                      ? "null"
                      : object.toString());

            if (index == objects.length - 1) {
                sb.append("'");
            }
        }

        sb.append("]");

        return sb.toString();
    }

    public static String toStringNullToEmptyString(Object object) {
        return object == null
                ? ""
                : object.toString();
    }

    /**
     * Reads the input stream into a string and finally closes the input stream.
     *
     * @param  is          input stream
     * @param  charsetName encoding of the characters in the stream
     * @return
     * @throws IOException
     */
    public static String convertStreamToString(InputStream is, String charsetName) throws IOException {
        if (is == null) {
            throw new NullPointerException("is == null");
        }

        if (charsetName == null) {
            throw new NullPointerException("encoding == null");
        }

        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader br = new BufferedReader(new InputStreamReader(is, charsetName));
            int n;
            while ((n = br.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }

        return writer.toString();
    }

    private StringUtil() {}
}
