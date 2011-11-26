package org.jphototagger.lib.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import static org.junit.Assert.*;
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
}
