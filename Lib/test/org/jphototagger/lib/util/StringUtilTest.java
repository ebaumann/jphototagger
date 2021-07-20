package org.jphototagger.lib.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class StringUtilTest {

    public StringUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of wrapWords method, of class StringUtil.
     */
    @Test
    public void testWrapWords() {
        String text = "";
        int maxCharsPerLine = 1;
        List<String> expResult = Collections.emptyList();
        List<String> result = StringUtil.wrapWords(text, maxCharsPerLine);

        assertEquals(expResult, result);
        text = "a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text = "aa";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text = "a a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text = "aa a";
        maxCharsPerLine = 1;
        expResult = Arrays.asList("a", "a", "a");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text = "Dies ist ein längerer Text mit 43 Zeichen.";
        maxCharsPerLine = 25;
        expResult = Arrays.asList("Dies ist ein längerer",
                "Text mit 43 Zeichen.");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
        text = "DiesisteinlängererTextmit36Zeichen.";
        maxCharsPerLine = 25;
        expResult = Arrays.asList("DiesisteinlängererTextmit",
                "36Zeichen.");
        result = StringUtil.wrapWords(text, maxCharsPerLine);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetNTimesRepeated() {
        String result = StringUtil.getNTimesRepeated("", 0);
        assertEquals("", result);

        result = StringUtil.getNTimesRepeated("", 100);
        assertEquals("", result);

        result = StringUtil.getNTimesRepeated(".", 0);
        assertEquals("", result);

        result = StringUtil.getNTimesRepeated(".", 1);
        assertEquals(".", result);

        result = StringUtil.getNTimesRepeated(".", 3);
        assertEquals("...", result);

        result = StringUtil.getNTimesRepeated("abc", 0);
        assertEquals("", result);

        result = StringUtil.getNTimesRepeated("abc", 1);
        assertEquals("abc", result);

        result = StringUtil.getNTimesRepeated("abc", 3);
        assertEquals("abcabcabc", result);
    }

    @Test
    public void testGetSubstringCount() {
        String string = "";
        String substringRegex = "";
        int count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(0, count);
        substringRegex = "bla";
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(0, count);
        string = substringRegex;
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(1, count);
        string = substringRegex + " " + substringRegex;
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(2, count);
        string = substringRegex + substringRegex + substringRegex;
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(3, count);
        substringRegex = "Multiple words here ";
        string = substringRegex + "abc" + substringRegex + substringRegex + " " + substringRegex;
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(4, count);
        string = "blubb";
        count = StringUtil.getSubstringCount(string, substringRegex);
        assertEquals(0, count);
}

    public void testRemoveLastOf() {
        String newString = StringUtil.removeLast("", "");
        assertEquals("", newString);
        newString = StringUtil.removeLast("", "bla");
        assertEquals("", newString);
        newString = StringUtil.removeLast("bla", "");
        assertEquals("bla", newString);
        newString = StringUtil.removeLast("bla", "bla");
        assertEquals("", newString);
        newString = StringUtil.removeLast("bla bla bla", "bla bla");
        assertEquals("bla ", newString);
        newString = StringUtil.removeLast("xyz", "bla bla");
        assertEquals("xyz", newString);
        newString = StringUtil.removeLast("xyz bla xyz", "bla");
        assertEquals("xyz  xyz", newString);
    }
}
