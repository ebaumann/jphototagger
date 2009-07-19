package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils for strings.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-25
 */
public final class StringUtil {

    private static final String WORD_DELIMITER = " \t\n\r"; // NOI18N

    /**
     * Calls {@link #wrapWords(java.lang.String, int, java.lang.String)} with
     * predefined word delimiter.
     *
     * @param text            text
     * @param maxCharsPerLine maximum characters per line
     * @return                lines
     */
    public static List<String> wrapWords(String text, int maxCharsPerLine) {
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
    public static List<String> wrapWords(String text, int maxCharsPerLine,
            String wordDelimiter) {
        if (text == null)
            throw new NullPointerException("text == null"); // NOI18N
        if (maxCharsPerLine <= 0)
            throw new IllegalArgumentException("Invalid max chars per line: " + // NOI18N
                    maxCharsPerLine);
        if (wordDelimiter.isEmpty())
            throw new IllegalArgumentException("Empty word delimiter string!"); // NOI18N

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
            int maxBreakIndex =
                    lineCount * maxCharsPerLine + maxCharsPerLine;
            if (index == maxBreakIndex) {
                if (currentLineBreakCharIndex > lineBeginIndex) {
                    lineEndIndex = currentLineBreakCharIndex;
                } else if (prevLineBreakCharIndex > lineBeginIndex) {
                    lineEndIndex = prevLineBreakCharIndex;
                } else {
                    lineEndIndex = lineBeginIndex == maxBreakIndex
                                   ? maxBreakIndex + 1
                                   : maxBreakIndex;
                }
                lines.add(text.substring(lineBeginIndex, lineEndIndex));
                lineBeginIndex = isWordDelimiter(text.charAt(
                        lineEndIndex < textLength
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

    private StringUtil() {
    }
}
