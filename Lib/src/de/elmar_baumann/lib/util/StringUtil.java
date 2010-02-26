/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utils for strings.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-25
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
        if (text == null           ) throw new NullPointerException("text == null");
        if (maxCharsPerLine <= 0   ) throw new IllegalArgumentException("Invalid max chars per line: " + maxCharsPerLine);
        if (wordDelimiter.isEmpty()) throw new IllegalArgumentException("Empty word delimiter string!");

        List<String> lines                     = new ArrayList<String>();
        int          textLength                = text.length();
        int          lineBeginIndex            = 0;
        int          lineEndIndex              = 0;
        int          currentLineBreakCharIndex = 0;
        int          prevLineBreakCharIndex    = 0;
        int          index                     = 0;
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

    public static List<String> getTrimmed(Collection<? extends String> strings) {
        List<String> trimmedStrings = new ArrayList<String>(strings.size());

        for (String string : strings) {
            trimmedStrings.add(string.trim());
        }

        return trimmedStrings;
    }

    public static List<String> getWordsOf(String string) {
        List<String>    words = new ArrayList<String>();
        StringTokenizer st    = new StringTokenizer(string, WORD_DELIMITER);

        while (st.hasMoreTokens()) {
            words.add(st.nextToken().trim());
        }
        return words;
    }

    private StringUtil() {
    }
}
